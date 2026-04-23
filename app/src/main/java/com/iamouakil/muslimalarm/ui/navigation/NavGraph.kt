package com.iamouakil.muslimalarm.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iamouakil.muslimalarm.R
import com.iamouakil.muslimalarm.ui.screens.*

sealed class Screen(val route: String, val labelRes: Int) {
    object Alarms : Screen("alarms", R.string.tab_alarms)
    object Home : Screen("home", R.string.tab_home)
    object Sleep : Screen("sleep", R.string.tab_sleep)
    object Settings : Screen("settings", R.string.tab_settings)
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(Screen.Alarms, Screen.Home, Screen.Sleep, Screen.Settings).forEach { screen ->
                    val icon = when (screen) {
                        Screen.Alarms -> Icons.Filled.Alarm
                        Screen.Home -> Icons.Filled.Home
                        Screen.Sleep -> Icons.Filled.Bedtime
                        Screen.Settings -> Icons.Filled.Settings
                    }
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = null) },
                        label = { Text(stringResource(screen.labelRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Alarms.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Alarms.route) { AlarmsScreen() }
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Sleep.route) { SleepScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
