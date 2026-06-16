package com.iamouakil.muslimalarm.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iamouakil.muslimalarm.ui.streak.StreakViewModel
import com.iamouakil.muslimalarm.ui.theme.*

@Composable
fun SleepScreen(
    streakViewModel: StreakViewModel = hiltViewModel()
) {
    val currentStreak by streakViewModel.currentStreak.collectAsState()
    val bestStreak by streakViewModel.bestStreak.collectAsState()
    val canLogExcuse by streakViewModel.canLogExcuse.collectAsState()
    val context = LocalContext.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AuroraBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "النوم والقيام",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    ExpandableCard(title = "سلسلة الاستمرارية") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("السلسلة الحالية: $currentStreak", fontSize = 18.sp, color = SurfaceContainerColor)
                            Text("أفضل سلسلة: $bestStreak", fontSize = 18.sp, color = SurfaceContainerColor)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        streakViewModel.logExcuse()
                                        Toast.makeText(context, "تم تسجيل العذر", Toast.LENGTH_SHORT).show()
                                    },
                                    enabled = canLogExcuse,
                                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
                                ) {
                                    Text("تسجيل عذر", color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        streakViewModel.recordWakeup()
                                        Toast.makeText(context, "تم محاكاة استيقاظ", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                                ) {
                                    Text("محاكاة استيقاظ", color = Color.White)
                                }
                            }
                        }
                    }
                }

                item {
                    ExpandableCard(title = "خطة النوم") {
                        var bedTimeHour by remember { mutableStateOf("") }
                        var bedTimeMin by remember { mutableStateOf("") }
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = bedTimeHour,
                                    onValueChange = { bedTimeHour = it },
                                    label = { Text("ساعة النوم") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = bedTimeMin,
                                    onValueChange = { bedTimeMin = it },
                                    label = { Text("دقيقة النوم") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Button(
                                onClick = {
                                    Toast.makeText(context, "تم حفظ منبه الخطة", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("حفظ الخطة", color = Color.White)
                            }
                        }
                    }
                }

                item {
                    ExpandableCard(title = "القيلولة") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf(20, 60, 90).forEach { duration ->
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "تم ضبط قيلولة $duration دقيقة", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerColor),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("قيلولة ($duration دقيقة)", color = Color.White)
                                }
                            }
                        }
                    }
                }

                item {
                    ExpandableCard(title = "الكافيين") {
                        var cHour by remember { mutableStateOf("") }
                        var cMin by remember { mutableStateOf("") }
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = cHour,
                                    onValueChange = { cHour = it },
                                    label = { Text("ساعة آخر كافيين") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = cMin,
                                    onValueChange = { cMin = it },
                                    label = { Text("دقيقة آخر كافيين") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Button(
                                onClick = {
                                    Toast.makeText(context, "محسوب!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("احسب موعد الخروج", color = Color.White)
                            }
                        }
                    }
                }
                
                item {
                    ExpandableCard(title = "قيام الليل") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("وقت قيام الليل (الثلث الأخير): 02:30", fontSize = 16.sp, color = SurfaceContainerColor)
                            Text("الوقت المتبقي: 04:15:00", fontSize = 16.sp, color = SurfaceContainerColor)
                            Button(
                                onClick = {
                                    Toast.makeText(context, "تم ضبط منبه القيام", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("اضبط منبه القيام", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableCard(title: String, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = PrimaryColor
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    content()
                }
            }
        }
    }
}
