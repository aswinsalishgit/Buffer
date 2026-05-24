package com.aswinsalish.buffer

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aswinsalish.buffer.core.theme.BufferTheme
import com.aswinsalish.buffer.core.ui.SplashScreen
import com.aswinsalish.buffer.menu.ui.MainMenuScreen
import com.aswinsalish.buffer.onboarding.ui.OnboardingScreen

@Composable
fun BufferApp() {
    BufferTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") {
                SplashScreen(
                    onNavigateToOnboarding = {
                        navController.navigate("onboarding") {
                            popUpTo("splash") { inclusive = true }
                        }
                    },
                    onNavigateToGame = {
                        navController.navigate("main_menu") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }
            composable("onboarding") {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate("main_menu") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }
            composable("main_menu") {
                MainMenuScreen(
                    onPlayClick = {
                        navController.navigate("game")
                    }
                )
            }
            composable("game") {
                com.aswinsalish.buffer.game.ui.GameScreen(
                    onExitPlay = {
                        navController.navigate("main_menu") {
                            popUpTo("main_menu") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
