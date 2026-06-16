package com.iamouakil.muslimalarm.ui.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iamouakil.muslimalarm.ui.settings.SettingsViewModel
import com.iamouakil.muslimalarm.ui.theme.PrimaryColor
import com.iamouakil.muslimalarm.ui.theme.AuroraBackground
import com.iamouakil.muslimalarm.ui.theme.glassmorphism

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val context = LocalContext.current
    
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AuroraBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth().glassmorphism(), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("اللغة", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedLanguage == "العربية",
                                onClick = { viewModel.setLanguage("العربية") },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor, unselectedColor = Color.White)
                            )
                            Text("العربية", color = Color.White)
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(
                                selected = selectedLanguage == "English",
                                onClick = { viewModel.setLanguage("English") },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor, unselectedColor = Color.White)
                            )
                            Text("English", color = Color.White)
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth().glassmorphism(), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("المظهر", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        val themes = listOf("الأخضر الإسلامي", "الأزرق الليلي", "الذهبي", "الرمضاني")
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(120.dp)
                        ) {
                            items(themes) { theme ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .border(
                                            width = if (selectedTheme == theme) 2.dp else 1.dp,
                                            color = if (selectedTheme == theme) PrimaryColor else Color.Gray,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .clickable { viewModel.setTheme(theme) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(theme, color = Color.White)
                                }
                            }
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth().glassmorphism(), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("البيانات", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { Toast.makeText(context, "تم التصدير بنجاح", Toast.LENGTH_SHORT).show() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                            ) {
                                Text("تصدير البيانات", color = Color.White)
                            }
                            Button(
                                onClick = { Toast.makeText(context, "تم استيراد البيانات", Toast.LENGTH_SHORT).show() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                            ) {
                                Text("استيراد", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
