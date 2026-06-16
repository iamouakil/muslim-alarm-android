package com.iamouakil.muslimalarm.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iamouakil.muslimalarm.ui.sleep.SleepViewModel
import com.iamouakil.muslimalarm.ui.theme.Color as AppColors
import com.iamouakil.muslimalarm.ui.theme.SharedComponents

@Composable
fun SleepScreen(viewModel: SleepViewModel = hiltViewModel()) {
    val context = LocalContext.current
    
    val bedtimeHourPref by viewModel.bedtimeHour.collectAsState()
    val bedtimeMinutePref by viewModel.bedtimeMinute.collectAsState()
    val wakeupHourPref by viewModel.wakeupHour.collectAsState()
    val wakeupMinutePref by viewModel.wakeupMinute.collectAsState()
    
    val sleepCycleOptions by viewModel.sleepCycleOptions.collectAsState()
    val napWakeTime by viewModel.napWakeTime.collectAsState()
    val caffeineClearTime by viewModel.caffeineClearanceTime.collectAsState()
    val suggestedBed by viewModel.suggestedBedtime.collectAsState()
    val qiyamTime by viewModel.qiyamTimeText.collectAsState()
    val qiyamCountdown by viewModel.qiyamCountdown.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setupQiyam()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        SharedComponents.AuroraBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ExpandableCard(title = "خطة النوم") {
                        var bedH by remember { mutableStateOf(bedtimeHourPref.toString()) }
                        var bedM by remember { mutableStateOf(bedtimeMinutePref.toString()) }
                        var wakeH by remember { mutableStateOf(wakeupHourPref.toString()) }
                        var wakeM by remember { mutableStateOf(wakeupMinutePref.toString()) }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = bedH,
                                    onValueChange = { bedH = it },
                                    label = { Text("ساعة النوم") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = bedM,
                                    onValueChange = { bedM = it },
                                    label = { Text("دقيقة النوم") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = wakeH,
                                    onValueChange = { wakeH = it },
                                    label = { Text("ساعة الاستيقاظ") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = wakeM,
                                    onValueChange = { wakeM = it },
                                    label = { Text("دقيقة الاستيقاظ") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Button(
                                onClick = {
                                    val bH = bedH.toIntOrNull() ?: 22
                                    val bM = bedM.toIntOrNull() ?: 0
                                    val wH = wakeH.toIntOrNull() ?: 6
                                    val wM = wakeM.toIntOrNull() ?: 0
                                    viewModel.saveSleepPlan(bH, bM, wH, wM)
                                    Toast.makeText(context, "تم حفظ خطة النوم وضبط المنبه", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryColor)
                            ) {
                                Text("حفظ الخطة", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }

                item {
                    ExpandableCard(title = "دورات النوم") {
                        var cBedH by remember { mutableStateOf("22") }
                        var cBedM by remember { mutableStateOf("00") }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = cBedH,
                                    onValueChange = { cBedH = it },
                                    label = { Text("ساعة النوم المتوقعة") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = cBedM,
                                    onValueChange = { cBedM = it },
                                    label = { Text("الدقيقة") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Button(
                                onClick = {
                                    val h = cBedH.toIntOrNull() ?: 22
                                    val m = cBedM.toIntOrNull() ?: 0
                                    viewModel.calculateSleepCycles(h, m)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.SecondaryColor)
                            ) {
                                Text("احسب أفضل الأوقات")
                            }

                            if (sleepCycleOptions.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    sleepCycleOptions.forEach { option ->
                                        Divider()
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = "دورة ${option.cycleCount} (الجودة: ${option.qualityLabelAr})",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = "وقت الاستيقاظ: ${option.wakeTime}",
                                                    color = AppColors.PrimaryColor,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    viewModel.scheduleCycleAlarm(option)
                                                    Toast.makeText(context, "تم ضبط منبه الدورة ${option.cycleCount}", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Icon(Icons.Filled.AlarmAdd, contentDescription = "اضبط منبه", tint = AppColors.PrimaryColor)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    ExpandableCard(title = "القيلولة") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            NapButton("قيلولة نشاط (20 دقيقة)") {
                                viewModel.scheduleNap(20)
                                Toast.makeText(context, "تم ضبط منبه لقيلولة 20 دقيقة", Toast.LENGTH_SHORT).show()
                            }
                            NapButton("قيلولة الذاكرة (60 دقيقة)") {
                                viewModel.scheduleNap(60)
                                Toast.makeText(context, "تم ضبط منبه لقيلولة 60 دقيقة", Toast.LENGTH_SHORT).show()
                            }
                            NapButton("قيلولة السنة (90 دقيقة)") {
                                viewModel.scheduleNap(90)
                                Toast.makeText(context, "تم ضبط منبه لقيلولة 90 دقيقة", Toast.LENGTH_SHORT).show()
                            }

                            if (napWakeTime.isNotEmpty()) {
                                Text(
                                    text = "وقت الاستيقاظ: $napWakeTime",
                                    color = AppColors.PrimaryColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    ExpandableCard(title = "الكافيين") {
                        var cafH by remember { mutableStateOf("16") }
                        var cafM by remember { mutableStateOf("00") }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = cafH,
                                    onValueChange = { cafH = it },
                                    label = { Text("ساعة آخر كافيين") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = cafM,
                                    onValueChange = { cafM = it },
                                    label = { Text("الدقيقة") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Button(
                                onClick = {
                                    val h = cafH.toIntOrNull() ?: 16
                                    val m = cafM.toIntOrNull() ?: 0
                                    viewModel.calculateCaffeine(h, m)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.SleepBadgeColor)
                            ) {
                                Text("احسب موعد الخروج")
                            }

                            if (caffeineClearTime.isNotEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("يخرج الكافيين من جسمك تقريباً الساعة: $caffeineClearTime")
                                    Text("أنسب وقت للنوم هو: $suggestedBed", fontWeight = FontWeight.Bold)
                                    
                                    Button(
                                        onClick = {
                                            viewModel.scheduleCaffeineSleepAlarm()
                                            Toast.makeText(context, "تم ضبط المنبه لوقت النوم المقترح", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.PrimaryColor)
                                    ) {
                                        Text("اضبط منبه للنوم")
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    ExpandableCard(title = "قيام الليل", defaultExpanded = true) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "وقت قيام الليل (الثلث الأخير): $qiyamTime",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            Text(
                                text = "الوقت المتبقي: $qiyamCountdown",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.PrimaryColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    viewModel.scheduleQiyamAlarm()
                                    Toast.makeText(context, "تم ضبط منبه القيام بنجاح", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryColor)
                            ) {
                                Text("اضبط منبه القيام", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun NapButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.PrimaryColor)
    ) {
        Text(text)
    }
}

@Composable
fun ExpandableCard(
    title: String,
    defaultExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(defaultExpanded) }
    val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "ExpandRotation")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(SharedComponents.glassmorphism()),
        colors = CardDefaults.cardColors(containerColor = AppColors.BgColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "طي" else "توسيع",
                    modifier = Modifier.rotate(rotationState)
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    content()
                }
            }
        }
    }
}
