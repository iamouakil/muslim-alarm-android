@file:OptIn(ExperimentalFoundationApi::class)
package com.iamouakil.muslimalarm.ui.alarm

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iamouakil.muslimalarm.data.alarm.Alarm
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAlarmSheet(
    alarm: Alarm?,
    onSave: (Alarm) -> Unit,
    onDelete: (Alarm) -> Unit,
    onDismiss: () -> Unit
) {
    val now = Calendar.getInstance()
    var hour by remember { mutableStateOf(alarm?.hour ?: now.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableStateOf(alarm?.minute ?: now.get(Calendar.MINUTE)) }
    var label by remember { mutableStateOf(alarm?.label ?: "") }
    var activeDays by remember { mutableStateOf(alarm?.days?.toSet() ?: setOf()) }
    var isSleepAlarm by remember { mutableStateOf(alarm?.isSleepAlarm ?: false) }
    var progressiveWakeup by remember { mutableStateOf(alarm?.progressiveWakeup ?: false) }
    var vibration by remember { mutableStateOf(alarm?.vibration ?: true) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = BgColor,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) }
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(700.dp)) {
                AuroraBackground()
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                    Text(
                        text = if (alarm == null) "إضافة منبه" else "تعديل المنبه",
                        color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Light,
                        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Time Picker
                    Row(modifier = Modifier.fillMaxWidth().height(160.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        WheelPicker(range = 0..23, currentValue = hour, onValueChange = { hour = it })
                        Text(text = ":", color = PrimaryColor.copy(alpha = 0.7f), fontSize = 56.sp, modifier = Modifier.padding(horizontal = 16.dp))
                        WheelPicker(range = 0..59, currentValue = minute, onValueChange = { minute = it })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = label, onValueChange = { label = it },
                        placeholder = { Text("اسم المنبه", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedIndicatorColor = PrimaryColor, unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val daysAr = listOf("ح", "ن", "ث", "ر", "خ", "ج", "س")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        daysAr.forEachIndexed { index, day ->
                            val isActive = activeDays.contains(index)
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape)
                                    .background(if (isActive) PrimaryColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                                    .border(1.dp, if (isActive) PrimaryColor else Color.White.copy(alpha = 0.1f), CircleShape)
                                    .then(androidx.compose.foundation.clickable { activeDays = if (isActive) activeDays - index else activeDays + index }),
                                contentAlignment = Alignment.Center
                            ) { Text(day, color = if (isActive) PrimaryColor else Color.Gray, fontSize = 14.sp) }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingRowToggle("منبه النوم", isSleepAlarm) { isSleepAlarm = it }
                    SettingRowToggle("إيقاظ تدريجي", progressiveWakeup) { progressiveWakeup = it }
                    SettingRowToggle("اهتزاز", vibration) { vibration = it }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            val saved = (alarm ?: Alarm(hour = 0, minute = 0)).copy(
                                hour = hour, minute = minute, label = label,
                                days = activeDays.sorted(), isSleepAlarm = isSleepAlarm,
                                progressiveWakeup = progressiveWakeup, vibration = vibration,
                                isEnabled = alarm?.isEnabled ?: true
                            )
                            onSave(saved)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerColor),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("حفظ المنبه", color = PrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold) }

                    if (alarm != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = { onDelete(alarm) }, modifier = Modifier.fillMaxWidth()) {
                            Text("حذف المنبه", color = Color.Red.copy(alpha = 0.8f), fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun SettingRowToggle(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).glassmorphism().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 16.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor, checkedThumbColor = BgColor))
    }
}

@Composable
fun WheelPicker(range: IntRange, currentValue: Int, onValueChange: (Int) -> Unit) {
    val items = range.toList()
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (items.indexOf(currentValue)).coerceAtLeast(0))
    val fling = rememberSnapFlingBehavior(lazyListState = listState)
    val currentIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    LaunchedEffect(currentIndex) { if (currentIndex in items.indices) onValueChange(items[currentIndex]) }

    Box(modifier = Modifier.width(100.dp).height(160.dp), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.08f)).border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)))
        LazyColumn(state = listState, flingBehavior = fling, horizontalAlignment = Alignment.CenterHorizontally, contentPadding = PaddingValues(vertical = 50.dp), modifier = Modifier.fillMaxSize()) {
            items(items.size) { index ->
                val isSelected = index == currentIndex
                Text(
                    text = String.format("%02d", items[index]),
                    fontSize = if (isSelected) 48.sp else 28.sp,
                    fontWeight = FontWeight(200),
                    color = if (isSelected) PrimaryColor else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}





