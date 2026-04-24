package com.iamouakil.muslimalarm.ui.alarm

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

val BgColor = Color(0xFF0A0F0A)
val PrimaryColor = Color(0xFF95D4B3)
val PrimaryContainerColor = Color(0xFF2D6A4F)
val SecondaryColor = Color(0xFFE6C364)
val SurfaceContainerColor = Color(0xFF1C211B)
val SleepBadgeColor = Color(0xFF3F51B5)

fun Modifier.glassmorphism() = this
    .clip(RoundedCornerShape(24.dp))
    .background(Color.White.copy(alpha = 0.05f))
    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))

@Composable
fun AuroraBackground(baseColor: Color = PrimaryContainerColor) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset(x = (-80).dp, y = (-60).dp)
                .size(320.dp)
                .background(Brush.radialGradient(listOf(baseColor.copy(alpha = 0.35f), Color.Transparent)))
                .blur(90.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .size(400.dp)
                .background(Brush.radialGradient(listOf(baseColor.copy(alpha = 0.2f), Color.Transparent)))
                .blur(110.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmsScreen(
    viewModel: AlarmViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit = {}
) {
    val alarms by viewModel.alarms.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    var editingAlarm by remember { mutableStateOf<com.iamouakil.muslimalarm.data.alarm.Alarm?>(null) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = BgColor,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { editingAlarm = null; showAddSheet = true },
                    containerColor = PrimaryContainerColor,
                    contentColor = PrimaryColor,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة منبه", modifier = Modifier.size(32.dp))
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                AuroraBackground()
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                    Spacer(modifier = Modifier.height(48.dp))
                    Text(text = "صباح الخير", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Light)
                    Text(text = "منبه المسلم", fontSize = 14.sp, color = PrimaryColor, letterSpacing = 3.sp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // Next Prayer Banner
                    Box(modifier = Modifier.fillMaxWidth().glassmorphism().padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🕌", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "الصلاة القادمة", color = Color.Gray, fontSize = 12.sp)
                                    Text(text = "متبقي 02:14:33", color = PrimaryColor, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                            Text(text = "04:30", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Light)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (alarms.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🔔", fontSize = 64.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = "لا توجد منبهات", color = Color.Gray, fontSize = 18.sp)
                                Text(text = "اضغط + لإضافة منبه", color = Color.DarkGray, fontSize = 14.sp)
                            }
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(alarms, key = { it.id }) { alarm ->
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = {
                                        if (it == SwipeToDismissBoxValue.EndToStart) { viewModel.deleteAlarm(alarm); true }
                                        else false
                                    }
                                )
                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = false,
                                    backgroundContent = {
                                        Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)).background(Color.Red.copy(alpha = 0.4f)).padding(end = 24.dp), contentAlignment = Alignment.CenterEnd) {
                                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.White)
                                        }
                                    }
                                ) {
                                    AlarmCard(alarm = alarm, onToggle = { viewModel.toggleAlarm(alarm) }, onClick = { editingAlarm = alarm; showAddSheet = true })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        AddEditAlarmSheet(
            alarm = editingAlarm,
            onSave = { if (editingAlarm == null) viewModel.addAlarm(it) else viewModel.updateAlarm(it); showAddSheet = false },
            onDelete = { viewModel.deleteAlarm(it); showAddSheet = false },
            onDismiss = { showAddSheet = false }
        )
    }
}

@Composable
fun AlarmCard(alarm: com.iamouakil.muslimalarm.data.alarm.Alarm, onToggle: () -> Unit, onClick: () -> Unit) {
    val daysAr = listOf("ح", "ن", "ث", "ر", "خ", "ج", "س")
    Box(modifier = Modifier.fillMaxWidth().glassmorphism().padding(20.dp).then(Modifier.clickable { onClick() })) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${String.format("%02d", alarm.hour)}:${String.format("%02d", alarm.minute)}", fontSize = 56.sp, fontWeight = FontWeight(200), color = if (alarm.isEnabled) Color.White else Color.Gray)
                        if (alarm.isSleepAlarm) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(SleepBadgeColor.copy(alpha = 0.3f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text(text = "نوم", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                    Text(text = alarm.label.ifEmpty { "منبه" }, color = Color.LightGray, fontSize = 16.sp)
                }
                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor, checkedThumbColor = BgColor, uncheckedTrackColor = SurfaceContainerColor)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    daysAr.forEachIndexed { index, day ->
                        val isActive = alarm.days.contains(index)
                        Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(if (isActive) PrimaryColor.copy(alpha = 0.2f) else Color.Transparent).border(1.dp, if (isActive) PrimaryColor else Color.White.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                            Text(text = day, color = if (isActive) PrimaryColor else Color.Gray, fontSize = 10.sp)
                        }
                    }
                }
                if (alarm.isEnabled) Text(text = "متبقي --:--:--", color = PrimaryColor, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}





