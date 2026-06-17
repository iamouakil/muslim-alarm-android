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
import com.iamouakil.muslimalarm.ui.sleep.SleepViewModel
import com.iamouakil.muslimalarm.ui.streak.StreakViewModel
import com.iamouakil.muslimalarm.ui.theme.*

@Composable
fun SleepScreen() {
    val sleepViewModel: SleepViewModel = hiltViewModel()
    val streakViewModel: StreakViewModel = hiltViewModel()
    val context = LocalContext.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AuroraBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { StreakSection(streakViewModel) }
                item { SleepPlanSection(sleepViewModel) }
                item { SleepCyclesSection(sleepViewModel) }
                item { NapSection(sleepViewModel) }
                item { CaffeineSection(sleepViewModel) }
                item { QiyamSection(sleepViewModel) }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun StreakSection(viewModel: StreakViewModel) {
    var expanded by remember { mutableStateOf(true) }
    val currentStreak by viewModel.currentStreak.collectAsState()
    val bestStreak by viewModel.bestStreak.collectAsState()
    val canLogExcuse by viewModel.canLogExcuse.collectAsState()
    val context = LocalContext.current

    ExpandableCard(title = "سلسلة الاستمرارية", expanded = expanded, onExpandChange = { expanded = it }) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("السلسلة الحالية: $currentStreak", fontSize = 18.sp, color = Color.White)
            Text("أفضل سلسلة: $bestStreak", fontSize = 18.sp, color = Color.White)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        viewModel.logExcuse()
                        Toast.makeText(context, "تم تسجيل العذر", Toast.LENGTH_SHORT).show()
                    },
                    enabled = canLogExcuse,
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
                ) { Text("تسجيل عذر", color = Color.White) }
                Button(
                    onClick = {
                        viewModel.recordWakeup()
                        Toast.makeText(context, "تم تسجيل استيقاظ", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) { Text("محاكاة استيقاظ", color = Color.White) }
            }
        }
    }
}

@Composable
fun SleepPlanSection(viewModel: SleepViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var bedH by remember { mutableStateOf("") }
    var bedM by remember { mutableStateOf("") }
    var wakeH by remember { mutableStateOf("") }
    var wakeM by remember { mutableStateOf("") }
    val context = LocalContext.current

    ExpandableCard(title = "خطة النوم", expanded = expanded, onExpandChange = { expanded = it }) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimeInputField(bedH, { bedH = it }, "ساعة النوم", Modifier.weight(1f))
                TimeInputField(bedM, { bedM = it }, "دقيقة النوم", Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimeInputField(wakeH, { wakeH = it }, "ساعة الاستيقاظ", Modifier.weight(1f))
                TimeInputField(wakeM, { wakeM = it }, "دقيقة الاستيقاظ", Modifier.weight(1f))
            }
            Button(
                onClick = {
                    viewModel.saveSleepPlan(
                        bedH.toIntOrNull() ?: 22, bedM.toIntOrNull() ?: 0,
                        wakeH.toIntOrNull() ?: 6, wakeM.toIntOrNull() ?: 0
                    )
                    Toast.makeText(context, "تم حفظ خطة النوم وجدولة المنبه", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) { Text("حفظ الخطة", color = Color.White) }
        }
    }
}

@Composable
fun SleepCyclesSection(viewModel: SleepViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var bedH by remember { mutableStateOf("") }
    var bedM by remember { mutableStateOf("") }
    val cycles by viewModel.sleepCycleOptions.collectAsState()
    val context = LocalContext.current

    ExpandableCard(title = "دورات النوم", expanded = expanded, onExpandChange = { expanded = it }) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimeInputField(bedH, { bedH = it }, "ساعة النوم", Modifier.weight(1f))
                TimeInputField(bedM, { bedM = it }, "دقيقة", Modifier.weight(1f))
            }
            Button(
                onClick = { viewModel.calculateSleepCycles(bedH.toIntOrNull() ?: 22, bedM.toIntOrNull() ?: 0) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
            ) { Text("احسب أفضل الأوقات", color = Color.White) }
            if (cycles.isNotEmpty()) {
                cycles.forEach { option ->
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("دورة ${option.cycleCount}", color = Color.White)
                            Text("وقت الاستيقاظ: ${option.wakeTime}", color = Color.White)
                            Text("الجودة: ${option.qualityLabelAr}", color = Color.White)
                        }
                        Button(
                            onClick = {
                                viewModel.scheduleCycleAlarm(option)
                                Toast.makeText(context, "تم ضبط منبه لدورة ${option.cycleCount}", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                        ) { Text("اضبط منبه", color = Color.White) }
                    }
                }
            }
        }
    }
}

@Composable
fun NapSection(viewModel: SleepViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val wakeTime by viewModel.napWakeTime.collectAsState()
    val context = LocalContext.current

    ExpandableCard(title = "القيلولة", expanded = expanded, onExpandChange = { expanded = it }) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(20 to "قيلولة نشاط", 60 to "قيلولة الذاكرة", 90 to "قيلولة السنة").forEach { (duration, label) ->
                Button(
                    onClick = {
                        viewModel.scheduleNap(duration)
                        Toast.makeText(context, "تم ضبط منبه القيلولة", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) { Text("$label ($duration دقيقة)", color = Color.White) }
            }
            if (wakeTime.isNotEmpty()) {
                Text("وقت الاستيقاظ: $wakeTime", color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
fun CaffeineSection(viewModel: SleepViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var hour by remember { mutableStateOf("") }
    var min by remember { mutableStateOf("") }
    val clearanceTime by viewModel.caffeineClearanceTime.collectAsState()
    val suggestedBedtime by viewModel.suggestedBedtime.collectAsState()
    val context = LocalContext.current

    ExpandableCard(title = "الكافيين", expanded = expanded, onExpandChange = { expanded = it }) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimeInputField(hour, { hour = it }, "ساعة", Modifier.weight(1f))
                TimeInputField(min, { min = it }, "دقيقة", Modifier.weight(1f))
            }
            Button(
                onClick = { viewModel.calculateCaffeine(hour.toIntOrNull() ?: 0, min.toIntOrNull() ?: 0) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
            ) { Text("احسب موعد الخروج", color = Color.White) }
            if (clearanceTime.isNotEmpty()) {
                Text("نسبة الكافيين تزول تقريباً الساعة: $clearanceTime", color = Color.White)
                Text("أنسب وقت للنوم هو: $suggestedBedtime", color = Color.White)
                Button(
                    onClick = {
                        viewModel.scheduleCaffeineSleepAlarm()
                        Toast.makeText(context, "تم ضبط منبه النوم بعد الخروج", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) { Text("اضبط منبه للنوم", color = Color.White) }
            }
        }
    }
}

@Composable
fun QiyamSection(viewModel: SleepViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val qiyamTimeText by viewModel.qiyamTimeText.collectAsState()
    val qiyamCountdown by viewModel.qiyamCountdown.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.setupQiyam() }

    ExpandableCard(title = "قيام الليل", expanded = expanded, onExpandChange = { expanded = it }) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("وقت قيام الليل (الثلث الأخير): $qiyamTimeText", color = Color.White)
            Text("الوقت المتبقي: $qiyamCountdown", color = Color.White)
            Button(
                onClick = {
                    viewModel.scheduleQiyamAlarm()
                    Toast.makeText(context, "تم ضبط منبه القيام", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) { Text("اضبط منبه القيام", color = Color.White) }
        }
    }
}

@Composable
fun TimeInputField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
    )
}

@Composable
fun ExpandableCard(
    title: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().glassmorphism(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onExpandChange(!expanded) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) { content() }
            }
        }
    }
}
