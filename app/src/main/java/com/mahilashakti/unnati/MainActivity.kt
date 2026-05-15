package com.mahilashakti.unnati

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.mahilashakti.unnati.database.AppDatabase
import com.mahilashakti.unnati.firebase.FirebaseManager
import com.mahilashakti.unnati.navigation.AppNavigation
import com.mahilashakti.unnati.repository.*
import com.mahilashakti.unnati.ui.theme.MahilaShaktiUnnatiTheme
import com.mahilashakti.unnati.viewmodel.*
import com.mahilashakti.unnati.ai.GeminiService
import com.mahilashakti.unnati.utils.*
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle results
    }

    private val securityUtils by lazy { SecurityUtils(this) }
    private val biometricHelper by lazy { BiometricHelper(this) }

    private val viewModelFactory: ViewModelFactory by lazy {
        val database = AppDatabase.getDatabase(this)
        val firebaseManager = FirebaseManager(this)
        val geminiService = GeminiService()
        val voiceParser = VoiceToTextParser(this)
        val pdfGenerator = PdfGenerator(this)
        val firestore = FirebaseFirestore.getInstance()
        val reminderScheduler = ReminderScheduler(this)
        
        val userRepository = UserRepository(database, firebaseManager)
        val memberRepository = MemberRepository(database.memberDao())
        val savingsRepository = SavingsRepository(database.savingsDao())
        val loanRepository = LoanRepository(database.loanDao())
        val attendanceRepository = AttendanceRepository(database.attendanceDao())
        val analyticsRepository = AnalyticsRepository(database.savingsDao(), database.loanDao(), database.attendanceDao())
        val aiRepository = AiRepository(geminiService, database.savingsDao(), database.loanDao(), database.attendanceDao())
        val exportRepository = ExportRepository(pdfGenerator, database.savingsDao(), database.loanDao())
        
        ViewModelFactory(
            this,
            userRepository, 
            memberRepository, 
            savingsRepository, 
            loanRepository, 
            attendanceRepository, 
            analyticsRepository,
            aiRepository,
            voiceParser,
            exportRepository,
            reminderScheduler,
            securityUtils
        )
    }

    private val authViewModel: AuthViewModel by viewModels { viewModelFactory }
    private val memberViewModel: MemberViewModel by viewModels { viewModelFactory }
    private val savingsViewModel: SavingsViewModel by viewModels { viewModelFactory }
    private val loanViewModel: LoanViewModel by viewModels { viewModelFactory }
    private val attendanceViewModel: AttendanceViewModel by viewModels { viewModelFactory }
    private val analyticsViewModel: AnalyticsViewModel by viewModels { viewModelFactory }
    private val aiViewModel: AiViewModel by viewModels { viewModelFactory }
    private val reportViewModel: ReportViewModel by viewModels { viewModelFactory }
    private val syncViewModel: SyncViewModel by viewModels { viewModelFactory }
    private val notificationViewModel: NotificationViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()
        
        // Initialize Notifications
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannels()
        
        // Schedule periodic cloud sync
        syncViewModel.schedulePeriodicSync()

        setContent {
            MahilaShaktiUnnatiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        authViewModel = authViewModel, 
                        memberViewModel = memberViewModel,
                        savingsViewModel = savingsViewModel,
                        loanViewModel = loanViewModel,
                        attendanceViewModel = attendanceViewModel,
                        analyticsViewModel = analyticsViewModel,
                        aiViewModel = aiViewModel,
                        reportViewModel = reportViewModel,
                        syncViewModel = syncViewModel,
                        notificationViewModel = notificationViewModel,
                        securityUtils = securityUtils,
                        onBiometricAuthRequest = { onResult ->
                            if (biometricHelper.isBiometricAvailable()) {
                                biometricHelper.showBiometricPrompt(
                                    title = "Security Verification",
                                    subtitle = "Log in using your biometrics",
                                    onSuccess = { onResult(true) },
                                    onError = { code, msg -> 
                                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                                        onResult(false)
                                    }
                                )
                            } else {
                                Toast.makeText(this, "Biometrics not available", Toast.LENGTH_SHORT).show()
                                onResult(false)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (missingPermissions.isNotEmpty()) {
            requestPermissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }
}
