package com.mahilashakti.unnati.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object RoleSelection : Screen("role_selection_screen")
    object ForgotPassword : Screen("forgot_password_screen")
    object Dashboard : Screen("dashboard_screen")
    object MemberList : Screen("member_list_screen")
    object MemberProfile : Screen("member_profile_screen/{memberId}") {
        fun createRoute(memberId: String) = "member_profile_screen/$memberId"
    }
    object AddEditMember : Screen("add_edit_member_screen/{memberId}") {
        fun createRoute(memberId: String?) = if (memberId != null) "add_edit_member_screen/$memberId" else "add_edit_member_screen/new"
    }
    object SavingsDashboard : Screen("savings_dashboard_screen")
    object RecordSavings : Screen("record_savings_screen")
    object MemberSavingsHistory : Screen("member_savings_history_screen/{memberId}") {
        fun createRoute(memberId: String) = "member_savings_history_screen/$memberId"
    }
    object LoanDashboard : Screen("loan_dashboard_screen")
    object RequestLoan : Screen("request_loan_screen/{memberId}") {
        fun createRoute(memberId: String) = "request_loan_screen/$memberId"
    }
    object LoanDetails : Screen("loan_details_screen/{loanId}") {
        fun createRoute(loanId: String) = "loan_details_screen/$loanId"
    }
    object AttendanceDashboard : Screen("attendance_dashboard_screen")
    object RecordAttendance : Screen("record_attendance_screen")
    object MemberAttendanceHistory : Screen("member_attendance_history_screen/{memberId}") {
        fun createRoute(memberId: String) = "member_attendance_history_screen/$memberId"
    }
    object Analytics : Screen("analytics_screen")
    object Chatbot : Screen("chatbot_screen")
    object ReportsDashboard : Screen("reports_dashboard_screen")
    object Settings : Screen("settings_screen")
}
