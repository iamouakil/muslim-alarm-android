package com.iamouakil.muslimalarm.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iamouakil.muslimalarm.ui.onboarding.OnboardingScreen
import com.iamouakil.muslimalarm.ui.onboarding.OnboardingViewModel
import com.iamouakil.muslimalarm.ui.screens.HomeScreen
import com.iamouakil.muslimalarm.ui.alarm.AlarmsScreen
import com.iamouakil.muslimalarm.ui.screens.SleepScreen
import com.iamouakil.muslimalarm.ui.screens.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val onboardingCompleted by onboardingViewModel.isOnboardingCompleted.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        if (!onboardingCompleted) {
            OnboardingScreen(onFinish = {
                onboardingViewModel.setOnboardingCompleted()
            })
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        val items = listOf(
                            Triple("home", "الرئيسية", Icons.Filled.Home),
                            Triple("alarms", "المنبهات", Icons.Filled.Alarm),
                            Triple("sleep", "النوم", Icons.Filled.Bedtime),
                            Triple("settings", "الإعدادات", Icons.Filled.Settings)
                        )
                        items.forEach { (route, title, icon) ->
                            NavigationBarItem(
                                icon = { Icon(icon, contentDescription = title) },
                                label = { Text(title) },
                                selected = currentRoute == route,
                                onClick = {
                                    if (currentRoute != route) {
                                        navController.navigate(route) {
                                            popUpTo("home") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("home") { HomeScreen() }
                    composable("alarms") { AlarmsScreen() }
                    composable("sleep") { SleepScreen() }
                    composable("settings") { SettingsScreen() }
                }
            }
        }
    }
}
