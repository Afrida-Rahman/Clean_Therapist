package org.mmh.emmavirtualtherapistv2.android.core.util

const val ROOT_ROUTE = "root"
const val AUTHENTICATION_ROUTE = "authentication"

sealed class Screen(val route: String) {
    object SplashScreen : Screen("splash_screen")
    object WalkThroughScreen : Screen("walk_through_screen")
    object WelcomeScreen : Screen("welcome_screen")
    object SignInScreen : Screen("sign_in_screen")
    object SignUpScreen : Screen("sign_up_screen")
    object ResetPasswordScreen : Screen("reset_password_screen")
    object RecoveryPasswordScreen : Screen("recovery_password_screen")
    object DashboardScreen : Screen("dashboard_screen")
    object NotificationScreen : Screen("notification_screen")
    object SettingsScreen : Screen("settings_screen")
    object AssessmentListScreen : Screen("assessment_list_screen")
    object ExerciseListScreen : Screen("exercise_list_screen")
    object GuidelineScreen : Screen("guideline_screen")
    object ExerciseScreen : Screen("exercise_screen")
    object BotListScreen : Screen("bot_list_screen")
    object ChatScreen : Screen("chat_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}
