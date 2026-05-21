package com.example.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.MainActivity
import com.example.data.local.*
import com.example.data.remote.*
import com.example.data.repository.DevHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DevHubViewModel(application: Application) : AndroidViewModel(application) {

    private val toolDao = DeveloperHubDatabase.getDatabase(application).toolDao()
    private val repository = DevHubRepository(toolDao)
    private val channelId = "dev_hub_alerts"

    // UI States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Interactive Eligibility Form Info for perks scoring
    val activeStack = MutableStateFlow("Android, Kotlin, Node.js")
    val companySize = MutableStateFlow("Startup (< 5 people)")

    // Gemini API Insight Engine State
    private val _aiInsight = MutableStateFlow<String?>(null)
    val aiInsight: StateFlow<String?> = _aiInsight.asStateFlow()

    private val _isGeneratingInsight = MutableStateFlow(false)
    val isGeneratingInsight: StateFlow<Boolean> = _isGeneratingInsight.asStateFlow()

    // Cache List Flows
    val allToolsStream: StateFlow<List<ToolEntity>> = repository.allTools
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteToolsStream: StateFlow<List<ToolEntity>> = repository.favoriteTools
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyUpdatesStream: StateFlow<List<DailyUpdateEntity>> = repository.allUpdates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val perksStream: StateFlow<List<PerkEntity>> = repository.allPerks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined filtered tools for Catalog
    val filteredTools: StateFlow<List<ToolEntity>> = combine(
        repository.allTools,
        _searchQuery,
        _selectedCategory
    ) { tools, query, category ->
        tools.filter { tool ->
            val matchesSearch = tool.name.contains(query, ignoreCase = true) ||
                    tool.shortDescription.contains(query, ignoreCase = true) ||
                    tool.category.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || tool.category.equals(category, ignoreCase = true)
            matchesSearch && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined matched perks based on Eligibility Form
    val matchedPerks: StateFlow<List<Pair<PerkEntity, Boolean>>> = combine(
        repository.allPerks,
        companySize
    ) { perks, size ->
        perks.map { perk ->
            val isMatch = when (perk.id) {
                "perk_gcp_startup" -> size.contains("Startup", ignoreCase = true)
                "perk_firebase_spark" -> true // Free for everyone
                "perk_gemini_trial" -> true   // Free for programmers
                "perk_maps_credits" -> true   // Free for billing accounts
                "perk_cloud_boost" -> true    // Training free for devs
                else -> true
            }
            Pair(perk, isMatch)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeDataIfEmpty()
        }
        createNotificationChannel()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleFavorite(toolId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(toolId, !currentStatus)
        }
    }

    fun markUpdateAsRead(updateId: Int) {
        viewModelScope.launch {
            repository.markUpdateRead(updateId)
        }
    }

    fun updatePerkStatus(perkId: String, status: String) {
        viewModelScope.launch {
            repository.updatePerkStatus(perkId, status)
        }
    }

    fun simulateAlert(title: String, category: String, description: String, techImpact: String) {
        viewModelScope.launch {
            val createdAlert = repository.addSimulatedAlert(title, category, description, techImpact)
            sendDeviceNotification(createdAlert)
        }
    }

    // AI Insight Generator using Gemini API
    fun generateDeveloperInsight(experience: Int) {
        viewModelScope.launch {
            _isGeneratingInsight.value = true
            _aiInsight.value = null

            val stackText = activeStack.value
            val companyText = companySize.value
            val key = BuildConfig.GEMINI_API_KEY

            if (key.isEmpty() || key == "MY_GEMINI_API_KEY") {
                // Return high-quality pre-designed offline developer recommendations mimicking AI
                _aiInsight.value = generateFallbackInsight(experience, stackText, companyText)
                _isGeneratingInsight.value = false
                return@launch
            }

            val prompt = """
                You are a senior Developer Relations Engineer at Google.
                Generate a highly tailored, elite architectural recommendation and insight package for an experienced software developer with $experience years of experience.
                Current Stack: $stackText
                Current Setup Target: $companyText
                
                Please organize your response into 3 clean markdown sections:
                1. ### Elite Multi-Cloud Architecture Integration
                   - Recommend specific Google Developer and enterprise tools (e.g., GKE, Cloud Spanner, Vertex AI, BigQuery) suited to their active stack. Mention production configuration hints.
                2. ### Trial Strategy & Credit Optimization
                   - Actionable guide on combining Google Cloud Startup Credit Pools ($200k), Google AI Studio Free Tiers, and Firebase Spark Tier to build zero-marginal-cost microservice deployments.
                3. ### Multi-Threaded Code Design & Documentation
                   - Provide a complex, annotated Kotlin or Python snippet demonstrating proper retry logic, security validation via App Check, or streaming Gemini endpoints for enterprise load in their stack.
                
                Keep the tone extremely professional, technical, authoritative and encouraging.
            """.trimIndent()

            try {
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(temperature = 0.3f)
                )
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(key, request)
                }
                val ans = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                _aiInsight.value = ans ?: "Unable to read Google Dev AI recommendations. Please check network connections."
            } catch (e: Exception) {
                _aiInsight.value = "Failed to run Gemini API calls. Fallback recommendations for active stack: \n\n" +
                        generateFallbackInsight(experience, stackText, companyText)
            } finally {
                _isGeneratingInsight.value = false
            }
        }
    }

    private fun generateFallbackInsight(exp: Int, stack: String, target: String): String {
        return """
            ### Elite Multi-Cloud Architecture Integration (Fallback recommendations)
            As a software developer with $exp years of experience working with **$stack**, we recommend incorporating **Google Cloud Run** for serverless container auto-scaling and **Firebase App Check** for secure hardware attestation inside your active endpoints.
            
            - **Container Networking**: Route standard Microservices via VPC connectors strictly into backend **Cloud SQL** networks to completely eliminate public exposure.
            - **AI Engine integration**: Deploy **Gemini 3.5 Flash** as your base orchestration agent. Keep temperatures at `0.2f` with `application/json` output schemas for steady, structured system parsing.
            
            ### Trial Program & Credit Optimization
            With current target **$target**, you represent a highly eligible candidate for the following major Google developer tiers:
            1. **Google Cloud Startup Program**: Grants up to **$200,000** for verified groups. Skip server bills entirely for 2 years.
            2. **Google Maps Free Allotment**: Save up to $2,400/year using Google's automatic monthly recurring $200 grant.
            3. **Google AI Studio Free Tier**: Leverage 1,500 free daily queries to completely build, test, and polish your vector RAG loops.
            
            ### Multi-Threaded Code Design & Documentation
            Here is an optimized architectural routine utilizing Kotlin Coroutines and Exponential Backoff for resilient high-volume API consumption:
            ```kotlin
            import kotlinx.coroutines.delay
            import java.io.IOException
            
            suspend fun <T> executeResilientCall(
                maxRetries: Int = 3, 
                block: suspend () -> T
            ): T {
                var currentDelay = 1000L // start backoff standard
                for (attempt in 1..maxRetries) {
                    try {
                        return block()
                    } catch (e: IOException) {
                        if (attempt == maxRetries) throw e
                        // apply 2x multiplier
                        delay(currentDelay)
                        currentDelay *= 2
                    }
                }
                throw IllegalStateException("Execution loop failed")
            }
            ```
        """.trimIndent()
    }

    // Device Notifications Simulation
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Google DevHub Alerts"
            val descriptionText = "Notifications for Google Developer launches and perks updates"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendDeviceNotification(update: DailyUpdateEntity) {
        val context = getApplication<Application>()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("New Launch: ${update.title}")
            .setContentText(update.description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(update.description + "\nImpact: " + update.techImpact))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(update.id, builder.build())
    }
}
