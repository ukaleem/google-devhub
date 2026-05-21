package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*

class DevHubRepository(private val toolDao: ToolDao) {

    val allTools: Flow<List<ToolEntity>> = toolDao.getAllTools()
    val favoriteTools: Flow<List<ToolEntity>> = toolDao.getFavoriteTools()
    val allUpdates: Flow<List<DailyUpdateEntity>> = toolDao.getDailyUpdates()
    val allPerks: Flow<List<PerkEntity>> = toolDao.getAllPerks()

    suspend fun updateFavorite(toolId: String, isFav: Boolean) {
        toolDao.updateFavoriteStatus(toolId, isFav)
    }

    suspend fun markUpdateRead(updateId: Int) {
        toolDao.markUpdateAsRead(updateId)
    }

    suspend fun updatePerkStatus(perkId: String, status: String) {
        toolDao.updatePerkStatus(perkId, status)
    }

    suspend fun addSimulatedAlert(title: String, category: String, description: String, techImpact: String): DailyUpdateEntity {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val newAlert = DailyUpdateEntity(
            title = title,
            category = category,
            description = description,
            techImpact = techImpact,
            releaseDate = dateStr,
            isRead = false,
            isAlertSimulated = true
        )
        toolDao.insertDailyUpdates(listOf(newAlert))
        return newAlert
    }

    suspend fun initializeDataIfEmpty() {
        val toolsCount = toolDao.getAllTools().first().size
        if (toolsCount == 0) {
            val initialTools = generateInitialTools()
            toolDao.insertTools(initialTools)
        }

        val updatesCount = toolDao.getDailyUpdates().first().size
        if (updatesCount == 0) {
            val initialUpdates = generateInitialUpdates()
            toolDao.insertDailyUpdates(initialUpdates)
        }

        val perksCount = toolDao.getAllPerks().first().size
        if (perksCount == 0) {
            val initialPerks = generateInitialPerks()
            toolDao.insertPerks(initialPerks)
        }
    }

    private fun generateInitialTools(): List<ToolEntity> {
        return listOf(
            // AI & ML
            ToolEntity(
                id = "gemini_api",
                name = "Gemini API",
                category = "AI & Machine Learning",
                shortDescription = "State-of-the-art multimodal AI model series for text, code, images, and audio reasoning.",
                fullDescription = "Connects professional developers to Google's highly advanced Gemini models (including Gemini 3.5 Flash and Gemini 3.1 Pro). Designed for high translation accuracy, complex reasoning, content generation, and code explanation. Features high-security integrations via Firebase App Check, structured JSON formats, schema validation, and low latency processing.",
                docUrl = "https://ai.google.dev/docs",
                apiDocSnippet = """
                    // Direct REST API Call using Kotlin and Retrofit
                    val requestBody = GenerateContentRequest(
                        contents = listOf(Content(parts = listOf(Part(text = "Review this database query: SELECT * FROM users")))),
                        generationConfig = GenerationConfig(
                            temperature = 0.2f,
                            responseMimeType = "application/json"
                        )
                    )
                    val response = RetrofitClient.service.generateContent(apiKey, requestBody)
                    val jsonAns = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                """.trimIndent(),
                tierPerks = "Free Prototyping Tier: 15 RPM, 1 Million TPM, 1,500 RPD. Pay-as-you-go thereafter with highly optimized pricing per million tokens."
            ),
            ToolEntity(
                id = "tensorflow",
                name = "TensorFlow",
                category = "AI & Machine Learning",
                shortDescription = "An end-to-end open source platform for machine learning and deep neural networks.",
                fullDescription = "Provides a comprehensive, flexible ecosystem of tools, libraries, and community resources that lets researchers push the state-of-the-art in ML, and developers easily build and deploy ML-powered applications. Supports distributed GPU and TPU training across large cloud infra panels.",
                docUrl = "https://www.tensorflow.org/api_docs",
                apiDocSnippet = """
                    import tensorflow as tf
                    
                    # Create and execute simple neural layer
                    model = tf.keras.Sequential([
                        tf.keras.layers.Dense(128, activation='relu', input_shape=(784,)),
                        tf.keras.layers.Dropout(0.2),
                        tf.keras.layers.Dense(10, activation='softmax')
                    ])
                    model.compile(optimizer='adam', loss='sparse_categorical_crossentropy', metrics=['accuracy'])
                """.trimIndent(),
                tierPerks = "Open Source. Fully integrated with Google Cloud Vertex AI TPU/GPU clusters with zero tooling premiums."
            ),
            ToolEntity(
                id = "mediapipe",
                name = "MediaPipe",
                category = "AI & Machine Learning",
                shortDescription = "Cross-platform, customizable ML solutions for live, streaming, and on-device media.",
                fullDescription = "Offers customizable ML components to detect gestures, perform face tracking, gesture control, object detection, and segmentation on iOS, Android, Web, and Edge devices with minimal CPU utilization. Highly optimized for on-device execution.",
                docUrl = "https://developers.google.com/mediapipe",
                apiDocSnippet = """
                    // Configure on-device MediaPipe Face Detector
                    val options = FaceDetectorOptions.builder()
                        .setBaseOptions(BaseOptions.builder().setModelAssetPath("detector.tflite").build())
                        .setConfidenceThreshold(0.7f)
                        .setTrackingEnabled(true)
                        .build()
                    val faceDetector = FaceDetector.createFromOptions(context, options)
                """.trimIndent(),
                tierPerks = "100% Free on-device library. No server-side API calls required. Save thousands in on-device image/video processing."
            ),
            ToolEntity(
                id = "vertex_ai",
                name = "Vertex AI Platform",
                category = "AI & Machine Learning",
                shortDescription = "Unified AutoML and Custom Model development platform on Google Cloud Infrastructure.",
                fullDescription = "Empowers enterprise engineering teams to build, deploy, and scale machine learning models with pre-built APIs, custom training loops, feature stores, and robust pipelines. Supports model registry, endpoints management, and LLM fine-tuning scripts easily.",
                docUrl = "https://cloud.google.com/vertex-ai/docs",
                apiDocSnippet = """
                    from google.cloud import aiplatform
                    
                    # Initialize Vertex AI client and deploy LLM model endpoint
                    aiplatform.init(project='my-enterprise-project', location='us-central1')
                    endpoint = aiplatform.Endpoint(endpoint_name='projects/123/locations/us-central1/endpoints/456')
                    response = endpoint.predict(instances=[{"prompt": "Generate cloud cluster spec"}])
                """.trimIndent(),
                tierPerks = "300 worth of free Google Cloud credits for testing. Exclusive credits for startups (up to 200,000 for verified programs)."
            ),

            // Cloud & Infrastructure
            ToolEntity(
                id = "compute_engine",
                name = "Google Compute Engine",
                category = "Cloud & Infrastructure",
                shortDescription = "Secure and customizable virtual machines (VMs) running on Google's world-wide network.",
                fullDescription = "Delivers high-performance virtual machines with custom CPU, storage, and networking options. Features high performance, live migration, and microsecond network scaling for massive enterprise microservices and databases.",
                docUrl = "https://cloud.google.com/compute/docs",
                apiDocSnippet = """
                    # gcloud command line to launch high-performance machine instance
                    gcloud compute instances create enterprise-vm-cluster \
                        --machine-type=n2-standard-4 \
                        --zone=us-central1-a \
                        --image-family=ubuntu-2204-lts \
                        --image-project=ubuntu-os-cloud
                """.trimIndent(),
                tierPerks = "Free Tier: 1 f1-micro VM instance per month (US zones only). Startups receive free persistent disk allowance up to 30GB."
            ),
            ToolEntity(
                id = "cloud_run",
                name = "Google Cloud Run",
                category = "Cloud & Infrastructure",
                shortDescription = "Fully managed serverless container execution platform with ultra-fast scaling.",
                fullDescription = "Deploys containerized microservices written in any programming language. Automatically scales from zero to thousands of instances with responsive millisecond triggers, and charges strictly per micro-second of execution.",
                docUrl = "https://cloud.google.com/run/docs",
                apiDocSnippet = """
                    # YAML deployment definition for Cloud Run microservice
                    apiVersion: serving.knative.dev/v1
                    kind: Service
                    metadata:
                      name: developer-insights-service
                    spec:
                      template:
                        spec:
                          containers:
                            - image: gcr.io/dev-hub/insights-api:latest
                """.trimIndent(),
                tierPerks = "Always Free allowance: 2 Million free requests per month, 360,000 GB-seconds memory, and 180,000 vCPU-seconds."
            ),
            ToolEntity(
                id = "cloud_spanner",
                name = "Cloud Spanner",
                category = "Cloud & Infrastructure",
                shortDescription = "Enterprise-grade globally distributed relational database with unlimited scale and 99.999% SLA.",
                fullDescription = "Combines standard relational SQL transaction consistency with global distributed scaling. Features automatic dual-region failover, multi-continent backups, security protection, and zero downtime schema mutations.",
                docUrl = "https://cloud.google.com/spanner/docs",
                apiDocSnippet = """
                    // Execute ACID multi-row transaction in Spanner
                    databaseClient.readWriteTransaction().run { transaction ->
                        val currentBalance = transaction.readRow("Balances", Key.of(101L), listOf("amount")).getLong(0)
                        val updatedPayload = ValueBinder.of(currentBalance - 50L)
                        transaction.buffer(Mutation.newUpdateBuilder("Balances").set("id").to(101L).set("amount").to(updatedPayload).build())
                        null
                    }
                """.trimIndent(),
                tierPerks = "Free Trial Instance available: 1 CPU core, 10GB high-speed database Storage totally free for 90 days of sandbox testing."
            ),
            ToolEntity(
                id = "bigquery",
                name = "BigQuery",
                category = "Cloud & Infrastructure",
                shortDescription = "Serverless, highly scalable cloud data warehouse with built-in machine learning capabilities.",
                fullDescription = "Engineered for petabyte-scale data analytics with fast SQL queries across massive datasets. Integrated with Vertex AI models (BigQuery ML) to execute prediction vectors natively inside the query schema.",
                docUrl = "https://cloud.google.com/bigquery/docs",
                apiDocSnippet = """
                    -- BigQuery SQL to train a linear model directly inside the warehouse
                    CREATE OR REPLACE MODEL `retail_dataset.customer_segment_model`
                    OPTIONS(model_type='linear_reg', input_label_cols=['purchase_value']) AS
                    SELECT loyalty_points, age_bracket, purchase_value 
                    FROM `retail_dataset.customer_logs`
                """.trimIndent(),
                tierPerks = "Query capacity allowance: 10GB storage tier and 1TB of monthly SQL data query execution absolutely free."
            ),

            // Mobile Development
            ToolEntity(
                id = "jetpack_compose",
                name = "Jetpack Compose",
                category = "Mobile Development",
                shortDescription = "Modern, declarative UI toolkit to build beautiful, native Android interfaces.",
                fullDescription = "Simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs. Fully supports material 3 layouts, dynamic theme matching, system navigation and window-insets optimization.",
                docUrl = "https://developer.android.com/compose",
                apiDocSnippet = """
                    // Interactive Component with Spring state transition
                    @Composable
                    fun PerkClaimButton(claimed: Boolean, onClaimed: () -> Unit) {
                        val scale by animateFloatAsState(if (claimed) 1.05f else 1.0f, spring())
                        Button(onClick = onClaimed, modifier = Modifier.scale(scale)) {
                            Text(if (claimed) "Claimed" else "Redeem Free Credits")
                        }
                    }
                """.trimIndent(),
                tierPerks = "100% Free and Open Source framework. Pre-packaged inside Android Studio IDE."
            ),
            ToolEntity(
                id = "flutter",
                name = "Flutter",
                category = "Mobile Development",
                shortDescription = "Open-source UI software development kit for cross-platform app deployment.",
                fullDescription = "Builds, tests, and deploys high-performance, visually stunning apps for mobile, web, desktop, and embedded devices from a single Dart codebase. Rendered natively using SKIA / Impeller engines.",
                docUrl = "https://docs.flutter.dev",
                apiDocSnippet = """
                    // High-performance custom animation controller in Dart
                    class DevHubLogoAnimator extends StatefulWidget {
                      @override
                      _DevHubLogoState createState() => _DevHubLogoState();
                    }
                """.trimIndent(),
                tierPerks = "Open Source Framework with extensive packages ecosystem supported by Google Devs globally."
            ),

            // Firebase Engine
            ToolEntity(
                id = "cloud_firestore",
                name = "Firebase Firestore",
                category = "Firebase Engine",
                shortDescription = "Scalable NoSQL document database with live, real-time client-side synchronization.",
                fullDescription = "Maintains application state with robust multi-region database replication, built-in offline synchronization caches, secure client-side rules, and reactive real-time queries for web, mobile, and server clients.",
                docUrl = "https://firebase.google.com/docs/firestore",
                apiDocSnippet = """
                    // Listen to reactive real-time collection streams in Kotlin
                    Firebase.firestore.collection("developer_offers")
                        .whereEqualTo("eligibleRole", "STARTUP")
                        .addSnapshotListener { snapshot, error ->
                            val docs = snapshot?.documents?.map { it.data }
                            // Reactively update system presentation state
                        }
                """.trimIndent(),
                tierPerks = "Spark Tier: 1GB database storage, 50,000 daily read documents, and 20,000 daily write, delete invocations free."
            ),
            ToolEntity(
                id = "firebase_auth",
                name = "Firebase Authentication",
                category = "Firebase Engine",
                shortDescription = "Easy-to-use secure authentication system supporting email, OAuth, and custom credentials.",
                fullDescription = "Provides secure, professional authentication configurations out of the box. Handles email verification, SMS OTPs, password reset loops, and major OAuth platforms like Google, GitHub, and Apple with highly optimized flows.",
                docUrl = "https://firebase.google.com/docs/auth",
                apiDocSnippet = """
                    // One-tap secure OAuth signing using Google Identity
                    val mAuth = FirebaseAuth.getInstance()
                    mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) val user = mAuth.currentUser
                        }
                """.trimIndent(),
                tierPerks = "Spark Tier: Unlimited free social logins (Google, GitHub). Up to 10,000 phone logins/month free."
            ),
            ToolEntity(
                id = "firebase_app_check",
                name = "Firebase App Check",
                category = "Firebase Engine",
                shortDescription = "Advanced security layer protecting cloud resources from abuse, bots, and unauthorized access.",
                fullDescription = "Verifies that incoming API traffic originates strictly from authentic app installations on real devices. Integrates with Device Check, App Attest, and Play Integrity to block bot farms, scraper endpoints, and key sniffers.",
                docUrl = "https://firebase.google.com/docs/app-check",
                apiDocSnippet = """
                    // Initialize App Check with Google Play Integrity Provider on Android
                    val firebaseAppCheck = FirebaseAppCheck.getInstance()
                    firebaseAppCheck.installAppCheckProviderFactory(
                        PlayIntegrityAppCheckProviderFactory.getInstance()
                    )
                """.trimIndent(),
                tierPerks = "Spark Tier: Fully includes Play Integrity checks up to 10,000 requests per day totally free of charge."
            ),

            // Web Platform
            ToolEntity(
                id = "chrome_devtools",
                name = "Chrome DevTools",
                category = "Web Platform",
                shortDescription = "Comprehensive custom web authoring and debugging tools built directly into Google Chrome.",
                fullDescription = "Enables professional developers to inspect layout elements, edit styles on-the-fly, measure core web vitals, profile network requests, analyze CPU execution bottlenecks, and debug progressive web apps.",
                docUrl = "https://developer.chrome.com/docs/devtools",
                apiDocSnippet = """
                    // Execute deep heap snapshot memory profiler
                    performance.mark("dev_audit_start");
                    console.profile("Heap Allocation Timeline");
                    // Trigger actions being debugged
                    console.profileEnd();
                """.trimIndent(),
                tierPerks = "Free tool packaged with all desktop Chromium distributions. Highly custom APIs available."
            ),
            ToolEntity(
                id = "webgpu",
                name = "WebGPU API",
                category = "Web Platform",
                shortDescription = "Modern graphics and parallel computation API on the web with native-like GPU execution.",
                fullDescription = "Next-generation API representing WebGL's successor. Delivers high-performance 3D graphics and model processing directly inside the web browser canvas, with reduced driver load and modern multi-threaded execution.",
                docUrl = "https://webgpu.dev",
                apiDocSnippet = """
                    // Setup WebGPU adapter and acquire system rendering device
                    const adapter = await navigator.gpu.requestAdapter();
                    const device = await adapter.requestDevice();
                    const context = canvas.getContext("webgpu");
                    context.configure({ device, format: "bgra8unorm" });
                """.trimIndent(),
                tierPerks = "Standardized open API. Highly optimized on Chromebooks, Android mobile containers, and macOS."
            ),

            // Maps Platform
            ToolEntity(
                id = "maps_sdk_android",
                name = "Maps SDK for Android",
                category = "Maps Platform",
                shortDescription = "Vector-rendered 3D Google Maps with custom styling and point-of-interest indicators.",
                fullDescription = "Allows integration of high-fidelity GIS features into Android. Supports multi-touch gestures, geo-coding coordinates lookup, customizable layers overlays, responsive labels, and live traffic metrics.",
                docUrl = "https://developers.google.com/maps/documentation/android-sdk",
                apiDocSnippet = """
                    // Render Map with Advanced 3D marker style in Jetpack Compose
                    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(LatLng(37.422, -122.084), 15f) }
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) { Marker(state = MarkerState(position = LatLng(37.422, -122.084)), title = "M3 Dev Center") }
                """.trimIndent(),
                tierPerks = "Includes a 200 monthly free credit automatically deposited on Google Maps API console billing setup."
            ),
            ToolEntity(
                id = "places_api",
                name = "Places API",
                category = "Maps Platform",
                shortDescription = "Rich point-of-interest details, reviews, user images, and real-time auto-complete queries.",
                fullDescription = "Enables search across 200 Million+ places globally. Supports keyword auto-complete feedback, business categories filters, contact info aggregation, and verified user coordinates indexing.",
                docUrl = "https://developers.google.com/maps/documentation/places/web-service",
                apiDocSnippet = """
                    // Search POIs matching query string and fetch phone/address data
                    val placesClient = Places.createClient(context)
                    val fields = listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PHONE_NUMBER)
                    val request = FindCurrentPlaceRequest.newInstance(fields)
                    placesClient.findCurrentPlace(request).addOnSuccessListener { response -> }
                """.trimIndent(),
                tierPerks = "Provides free, tiered quotas. Completely integrates with custom Android app billing thresholds."
            ),

            // Workspace & Colab
            ToolEntity(
                id = "google_apps_script",
                name = "Google Apps Script",
                category = "Workspace & Productivity",
                shortDescription = "Rapid developer-friendly scripting platform to customize, automate, and extend Google Workspace.",
                fullDescription = "Requires zero setup. Writes lightweight JavaScript macros, automates Sheets parsing, triggers calendar invites from incoming Gmail documents, and publishes quick microservice web endpoints with zero hosting costs.",
                docUrl = "https://developers.google.com/apps-script",
                apiDocSnippet = """
                    // Script to parse Sheet content and trigger bulk HTML email alerts
                    function sendAlerts() {
                      var sheet = SpreadsheetApp.getActiveSheet();
                      var data = sheet.getDataRange().getValues();
                      data.forEach(function(row) {
                        MailApp.sendEmail(row[0], "Enterprise Dev Alert", "Details: " + row[1]);
                      });
                    }
                """.trimIndent(),
                tierPerks = "100% Free inside any personal or business Google Workspace account. Custom triggers up to 20,000 steps/day."
            ),
            ToolEntity(
                id = "google_colab",
                name = "Google Colab",
                category = "Workspace & Productivity",
                shortDescription = "Interactive Jupyter Jupyter notebook execution platform with free GPU runtime access.",
                fullDescription = "Allows developers, researchers, and startups to compile Python neural networks, run complex SQL analytics in BigQuery, visualize media, and document math models in clean Markdowns without local dependencies.",
                docUrl = "https://colab.google",
                apiDocSnippet = """
                    # Import BigQuery module and read data in standard Python dataframe
                    from google.colab import auth
                    from google.cloud import bigquery
                    auth.authenticate_user()
                    df = bigquery.Client().query("SELECT * FROM market_trends LIMIT 100").to_dataframe()
                """.trimIndent(),
                tierPerks = "Free Tier includes access to standard CPU runtimes and K80/T4 GPUs. Pro tiers scale with higher RAM quotas."
            )
        )
    }

    private fun generateInitialUpdates(): List<DailyUpdateEntity> {
        return listOf(
            DailyUpdateEntity(
                title = "Vertex AI Studio: NVidia Blackwell Hardware Pool Released",
                category = "Beta Launch",
                description = "Google Cloud Vertex AI introduces high-performance NVidia Blackwell instance arrays. Enterprise-tier teams can now configure custom training pipelines with 3.2x faster FP8 training parameters and redundant mesh interconnects.",
                techImpact = "Enables massive scaling of local models. Reduces direct machine training hours from 7 days to 24 hours.",
                releaseDate = "2026-05-21 14:30"
            ),
            DailyUpdateEntity(
                title = "Gecko Embedding-2 Model: Multimodal Context Scaling",
                category = "AI Tool Release",
                description = "Google releases the Gecko Embedding-2 endpoint, a lightweight API designed specifically for low-latency retrieval-augmented generation (RAG). Supports 64k token size and parses visual files along with unstructured tech documents.",
                techImpact = "Ideal for context lookup engines. API response latency trimmed down to < 45ms.",
                releaseDate = "2026-05-20 09:15"
            ),
            DailyUpdateEntity(
                title = "Firebase App Check Key Attestation updates for Jetpack apps",
                category = "Major Security Update",
                description = "Firebase releases updated Attestation frameworks natively using Android's remote KeyStore hardware verification. Securely flags rooted devices, debug frameworks, and emulator configurations.",
                techImpact = "Provides unbreakable security validation against API credentials scanners and reverse-engineering.",
                releaseDate = "2026-05-18 11:00"
            ),
            DailyUpdateEntity(
                title = "Chrome DevTools WebGPU Live Context Shaders debugging",
                category = "Upcoming Feature",
                description = "Google announces WebGPU developer updates. Developers can now trace live, multi-threaded shader pipelines, watch execution registers on-the-fly, and spot sub-canvas pixel rendering race condition leaks directly in the tools page.",
                techImpact = "Accelerates execution of hybrid web-based neural layers. Solves GPU memory alignment challenges easily.",
                releaseDate = "2026-05-15 17:00"
            )
        )
    }

    private fun generateInitialPerks(): List<PerkEntity> {
        return listOf(
            PerkEntity(
                id = "perk_gcp_startup",
                title = "Google Cloud for Startups Tiered Credit Pool",
                provider = "Google Cloud for Startups",
                valueDescription = "Up to $200k in credits over 2 Years",
                eligibilityCriteria = "Technology startups founded within the last 5 years, with less than $5M in total funding.",
                benefitDetails = "Provides complete, free coverage of compute instances, BigQuery clusters, safe Spanner nodes, and Vertex AI fine-tuning runs. Includes architectural reviews, priority chat support, and GCP sandbox vouchers.",
                trialPeriod = "24 Months",
                claimedStatus = "ELIGIBLE",
                docUrl = "https://startup.google.com/programs"
            ),
            PerkEntity(
                id = "perk_firebase_spark",
                title = "Firebase Premium Spark Tier Multi-Allocation",
                provider = "Firebase Ecosystem",
                valueDescription = "Fully Free Basic Database & Analytics quota",
                eligibilityCriteria = "Professional developers, testing teams, and early-stage startup prototypes.",
                benefitDetails = "Allocates 1GB Firestore database storage, 10GB Firebase Cloud Storage, 125,000 hourly Cloud Functions execution loops, and unlimited social auth tokens. No credit card required of any type.",
                trialPeriod = "Permanent Free Tier",
                claimedStatus = "ELIGIBLE",
                docUrl = "https://firebase.google.com/pricing"
            ),
            PerkEntity(
                id = "perk_gemini_trial",
                title = "Google AI Studio Developer API Premium Access",
                provider = "Google AI Studio",
                valueDescription = "1.5 Million TPM Free Gemini Rate Quota",
                eligibilityCriteria = "Verified active professional software developers using custom domain emails.",
                benefitDetails = "Provides zero-cost development endpoints for Gemini 3.5 Flash and 3.1 Pro. Includes complete systems instructions support, json response formatting, structure verification, and API key management panel integration.",
                trialPeriod = "During API Prototyping",
                claimedStatus = "ELIGIBLE",
                docUrl = "https://aistudio.google.com"
            ),
            PerkEntity(
                id = "perk_maps_credits",
                title = "Google Maps Platform Developer Monthly Grant",
                provider = "Google Maps",
                valueDescription = "$200 Monthly Recurring Billing Voucher",
                eligibilityCriteria = "All developers holding a verified billing accounts console setup.",
                benefitDetails = "Automatically credits $200 every month on your map services balance. Covers static web views, location routing, geocoding lookups, andplaces auto-complere metrics.",
                trialPeriod = "Monthly Recurring",
                claimedStatus = "ELIGIBLE",
                docUrl = "https://mapsplatform.google.com"
            ),
            PerkEntity(
                id = "perk_cloud_boost",
                title = "Google Cloud Skills Boost 30-Day Training Pass",
                provider = "Google Cloud Skills Boost",
                valueDescription = "All-Access Lab training (worth $55/month)",
                eligibilityCriteria = "Any professional software developer looking to learn GCP scaling models.",
                benefitDetails = "Grants 30 days of unlimited access to actual cloud sandbox labs covering Vertex AI, Kubernetes clusters, Docker orchestration, and security attestation.",
                trialPeriod = "30 Days Free Access",
                claimedStatus = "ELIGIBLE",
                docUrl = "https://www.cloudskillsboost.google"
            )
        )
    }
}
