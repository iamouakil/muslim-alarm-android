package com.iamouakil.muslimalarm.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iamouakil.muslimalarm.ui.alarm.AlarmsScreen
import com.iamouakil.muslimalarm.ui.onboarding.OnboardingScreen
import com.iamouakil.muslimalarm.ui.onboarding.OnboardingViewModel
import com.iamouakil.muslimalarm.ui.screens.HomeScreen
import com.iamouakil.muslimalarm.ui.screens.SettingsScreen
import com.iamouakil.muslimalarm.ui.screens.SleepScreen
import com.iamouakil.muslimalarm.ui.theme.BgColor
import com.iamouakil.muslimalarm.ui.theme.PrimaryColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val onboardingCompleted by onboardingViewModel.isOnboardingCompleted.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showSettings by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        if (!onboardingCompleted) {
            OnboardingScreen(onFinish = { onboardingViewModel.setOnboardingCompleted() })
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("منبه المسلم", color = PrimaryColor) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = BgColor),
                        actions = {
                            IconButton(onClick = { showSettings = true }) {
                                Icon(Icons.Filled.Settings, contentDescription = "الإعدادات", tint = PrimaryColor)
                            }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar(containerColor = BgColor) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Alarm, contentDescription = null) },
                            label = { Text("المنبهات") },
                            selected = currentRoute == "alarms",
                            onClick = {
                                navController.navigate("alarms") {
                                    popUpTo("alarms") { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryColor, selectedTextColor = PrimaryColor, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                            label = { Text("الصلاة") },
                            selected = currentRoute == "prayer",
                            onClick = {
                                navController.navigate("prayer") {
                                    popUpTo("alarms") { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryColor, selectedTextColor = PrimaryColor, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Bedtime, contentDescription = null) },
                            label = { Text("النوم") },
                            selected = currentRoute == "sleep",
                            onClick = {
                                navController.navigate("sleep") {
                                    popUpTo("alarms") { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryColor, selectedTextColor = PrimaryColor, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                        )
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "alarms",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("alarms") { AlarmsScreen() }
                    composable("prayer") { HomeScreen() }
                    composable("sleep") { SleepScreen() }
                }
            }

            if (showSettings) {
                ModalBottomSheet(
                    onDismissRequest = { showSettings = false },
                    sheetState = sheetState,
                    containerColor = BgColor
                ) {
                    SettingsScreen()
                }
            }
        }
    }
}
