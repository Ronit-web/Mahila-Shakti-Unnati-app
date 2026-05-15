package com.mahilashakti.unnati.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mahilashakti.unnati.ui.screens.dashboard.DashboardScreen
import com.mahilashakti.unnati.ui.screens.login.ForgotPasswordScreen
import com.mahilashakti.unnati.ui.screens.login.LoginScreen
import com.mahilashakti.unnati.ui.screens.login.RegisterScreen
import com.mahilashakti.unnati.ui.screens.login.RoleSelectionScreen
import com.mahilashakti.unnati.ui.screens.splash.SplashScreen
import com.mahilashakti.unnati.viewmodel.*
import com.mahilashakti.unnati.ui.screens.member.MemberListScreen
import com.mahilashakti.unnati.ui.screens.member.MemberProfileScreen
import com.mahilashakti.unnati.ui.screens.member.AddEditMemberScreen
import com.mahilashakti.unnati.ui.screens.savings.SavingsDashboardScreen
import com.mahilashakti.unnati.ui.screens.savings.RecordSavingsScreen
import com.mahilashakti.unnati.ui.screens.savings.MemberSavingsHistoryScreen
import com.mahilashakti.unnati.ui.screens.loan.LoanDashboardScreen
import com.mahilashakti.unnati.ui.screens.loan.RequestLoanScreen
import com.mahilashakti.unnati.ui.screens.loan.LoanDetailsScreen
import com.mahilashakti.unnati.ui.screens.attendance.AttendanceDashboardScreen
import com.mahilashakti.unnati.ui.screens.attendance.RecordAttendanceScreen
import com.mahilashakti.unnati.ui.screens.attendance.MemberAttendanceHistoryScreen
import com.mahilashakti.unnati.ui.screens.analytics.AnalyticsDashboardScreen
import com.mahilashakti.unnati.ui.screens.ai.ChatbotScreen
import com.mahilashakti.unnati.ui.screens.reports.ReportDashboardScreen
import com.mahilashakti.unnati.ui.screens.settings.SettingsScreen
import com.mahilashakti.unnati.utils.SecurityUtils
import com.mahilashakti.unnati.data.model.Role
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    memberViewModel: MemberViewModel,
    savingsViewModel: SavingsViewModel,
    loanViewModel: LoanViewModel,
    attendanceViewModel: AttendanceViewModel,
    analyticsViewModel: AnalyticsViewModel,
    aiViewModel: AiViewModel,
    reportViewModel: ReportViewModel,
    syncViewModel: SyncViewModel,
    notificationViewModel: NotificationViewModel,
    securityUtils: SecurityUtils,
    onBiometricAuthRequest: ((Boolean) -> Unit) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                isBiometricEnabled = securityUtils.isBiometricEnabled(),
                onBiometricClick = {
                    onBiometricAuthRequest { success ->
                        if (success) {
                            // In a real app, we would use a saved token. 
                            // Here we navigate if biometric is successful.
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToRoleSelection = { navController.navigate(Screen.RoleSelection.route) },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(route = Screen.RoleSelection.route) {
            RoleSelectionScreen(
                viewModel = authViewModel,
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToMembers = { navController.navigate(Screen.MemberList.route) },
                onNavigateToSavings = { navController.navigate(Screen.SavingsDashboard.route) },
                onNavigateToLoans = { navController.navigate(Screen.LoanDashboard.route) },
                onNavigateToAttendance = { navController.navigate(Screen.AttendanceDashboard.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                onNavigateToAiAssistant = { navController.navigate(Screen.Chatbot.route) },
                onNavigateToReports = { navController.navigate(Screen.ReportsDashboard.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                syncViewModel = syncViewModel,
                authViewModel = authViewModel
            )
        }

        composable(route = Screen.MemberList.route) {
            MemberListScreen(
                viewModel = memberViewModel,
                onNavigateToAddMember = { navController.navigate(Screen.AddEditMember.createRoute(null)) },
                onNavigateToProfile = { memberId -> navController.navigate(Screen.MemberProfile.createRoute(memberId)) }
            )
        }

        composable(
            route = Screen.MemberProfile.route,
            arguments = listOf(navArgument("memberId") { type = NavType.StringType })
        ) { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId") ?: return@composable
            MemberProfileScreen(
                memberId = memberId,
                viewModel = memberViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.AddEditMember.createRoute(id)) },
                onNavigateToSavingsHistory = { navController.navigate(Screen.MemberSavingsHistory.createRoute(memberId)) },
                onNavigateToRequestLoan = { id -> navController.navigate(Screen.RequestLoan.createRoute(id)) },
                onNavigateToAttendanceHistory = { id -> navController.navigate(Screen.MemberAttendanceHistory.createRoute(id)) },
                onExportPassbook = {
                    memberViewModel.membersList.value.find { it.id == memberId }?.let {
                        reportViewModel.generateMemberPassbook(it)
                    }
                }
            )
        }

        composable(
            route = Screen.AddEditMember.route,
            arguments = listOf(navArgument("memberId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId")
            AddEditMemberScreen(
                memberId = memberId,
                viewModel = memberViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.SavingsDashboard.route) {
            SavingsDashboardScreen(
                viewModel = savingsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecordSavings = { navController.navigate(Screen.RecordSavings.route) }
            )
        }

        composable(route = Screen.RecordSavings.route) {
            RecordSavingsScreen(
                viewModel = savingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.MemberSavingsHistory.route,
            arguments = listOf(navArgument("memberId") { type = NavType.StringType })
        ) { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId") ?: return@composable
            MemberSavingsHistoryScreen(
                memberId = memberId,
                viewModel = savingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.LoanDashboard.route) {
            LoanDashboardScreen(
                viewModel = loanViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetails = { loanId -> navController.navigate(Screen.LoanDetails.createRoute(loanId)) }
            )
        }

        composable(
            route = Screen.RequestLoan.route,
            arguments = listOf(navArgument("memberId") { type = NavType.StringType })
        ) { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId") ?: return@composable
            RequestLoanScreen(
                memberId = memberId,
                viewModel = loanViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LoanDetails.route,
            arguments = listOf(navArgument("loanId") { type = NavType.StringType })
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getString("loanId") ?: return@composable
            LoanDetailsScreen(
                loanId = loanId,
                loanViewModel = loanViewModel,
                aiViewModel = aiViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.AttendanceDashboard.route) {
            AttendanceDashboardScreen(
                viewModel = attendanceViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecordAttendance = { navController.navigate(Screen.RecordAttendance.route) }
            )
        }

        composable(route = Screen.RecordAttendance.route) {
            RecordAttendanceScreen(
                viewModel = attendanceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.MemberAttendanceHistory.route,
            arguments = listOf(navArgument("memberId") { type = NavType.StringType })
        ) { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId") ?: return@composable
            MemberAttendanceHistoryScreen(
                memberId = memberId,
                viewModel = attendanceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Analytics.route) {
            val userRole by authViewModel.userRole.collectAsState()
            RoleGuard(userRole = userRole, allowedRoles = listOf(Role.Admin, Role.Treasurer)) {
                AnalyticsDashboardScreen(
                    viewModel = analyticsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(route = Screen.Chatbot.route) {
            ChatbotScreen(
                viewModel = aiViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.ReportsDashboard.route) {
            val userRole by authViewModel.userRole.collectAsState()
            RoleGuard(userRole = userRole, allowedRoles = listOf(Role.Admin, Role.Treasurer)) {
                ReportDashboardScreen(
                    viewModel = reportViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                viewModel = notificationViewModel,
                securityUtils = securityUtils,
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }
    }
}
