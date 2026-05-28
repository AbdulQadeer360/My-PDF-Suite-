package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.PdfViewModel

@Composable
fun AppNavigation(
    viewModel: PdfViewModel,
    navController: NavHostController = rememberNavController()
) {
    val currentUserState = viewModel.currentUser.collectAsState()
    val startDestination = if (currentUserState.value == null) "onboarding" else "dashboard"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("onboarding") {
            OnboardingScreen(
                viewModel = viewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToDashboard = { navController.navigate("dashboard") }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                navController = navController,
                onNavigateToTools = { navController.navigate("tools") },
                onNavigateToPremium = { navController.navigate("premium") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToAi = { navController.navigate("ai") },
                onNavigateToOcr = { navController.navigate("ocr") }
            )
        }

        composable("tools") {
            ToolsScreen(
                viewModel = viewModel,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable("convert_text") {
            ConvertTextScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("convert_image") {
            ConvertImageScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("watermark") {
            WatermarkScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("compress") {
            CompressScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("security") {
            SecurityScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("ocr") {
            OcrScannerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("ai") {
            AiSuiteScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("premium") {
            PremiumScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToPremium = { navController.navigate("premium") }
            )
        }

        composable("annotations") {
            AnnotationScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("collaboration") {
            CollaborationScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("templates") {
            TemplateLibraryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
