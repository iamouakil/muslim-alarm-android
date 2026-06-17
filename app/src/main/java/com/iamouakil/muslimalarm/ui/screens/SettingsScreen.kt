package com.iamouakil.muslimalarm.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iamouakil.muslimalarm.ui.settings.SettingsViewModel
import com.iamouakil.muslimalarm.ui.theme.*

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel = hiltViewModel()) {
    val language by settingsViewModel.selectedLanguage.collectAsState()
    val theme by settingsViewModel.selectedTheme.collectAsState()
    val context = LocalContext.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AuroraBackground {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("الإعدادات", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                }
                item {
                    SettingsCard("اللغة") {
                        Column {
                            LanguageRow("العربية", language == "العربية") { settingsViewModel.setLanguage("العربية") }
                            LanguageRow("English", language == "English") { settingsViewModel.setLanguage("English") }
                        }
                    }
                }
                item {
                    SettingsCard("المظهر") {
                        val themes = listOf("الأخضر الإسلامي", "الأزرق الليلي", "الذهبي", "الرمضاني")
                        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.height(140.dp)) {
                            items(themes) { t ->
                                val isSelected = t == theme
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable { settingsViewModel.setTheme(t) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = if (isSelected) PrimaryContainerColor else Color.Transparent),
                                    border = if (isSelected) BorderStroke(2.dp, PrimaryColor) else BorderStroke(1.dp, Color.Gray)
                                ) {
                                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                                        Text(t, color = Color.White, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    SettingsCard("البيانات") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(onClick = { Toast.makeText(context, "تم التصدير", Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)) {
                                Text("تصدير البيانات", color = Color.White)
                            }
                            Button(onClick = { Toast.makeText(context, "تم الاستيراد", Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerColor)) {
                                Text("استيراد البيانات", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageRow(language: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor))
        Spacer(modifier = Modifier.width(8.dp))
        Text(language, fontSize = 16.sp, color = Color.White)
    }
}

@Composable
fun SettingsCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().glassmorphism(), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = PrimaryColor, modifier = Modifier.padding(bottom = 16.dp))
            content()
        }
    }
}
