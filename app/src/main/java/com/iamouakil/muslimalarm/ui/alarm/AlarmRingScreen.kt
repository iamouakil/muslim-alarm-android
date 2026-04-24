package com.iamouakil.muslimalarm.ui.alarm

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iamouakil.muslimalarm.data.alarm.Alarm
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmRingScreen(
    alarm: Alarm,
    alarmType: String,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit,
    onExcuse: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.25f,
        animationSpec = infiniteRepeatable(animation = tween(900, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "bellScale"
    )
    var showExcuseSheet by remember { mutableStateOf(false) }
    val crimson = Color(0xFF1A0505)
    val crimsonBlob = Color(0xFF8B0000)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(modifier = Modifier.fillMaxSize().background(crimson)) {
            // Crimson Aurora
            Box(modifier = Modifier.offset(x = (-50).dp, y = (-100).dp).size(350.dp).background(Brush.radialGradient(listOf(crimsonBlob.copy(alpha = 0.5f), Color.Transparent))).blur(120.dp))
            Box(modifier = Modifier.align(Alignment.BottomCenter).offset(y = 80.dp).size(400.dp).background(Brush.radialGradient(listOf(crimsonBlob.copy(alpha = 0.3f), Color.Transparent))).blur(130.dp))

            Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                // Pulsing bell with rings
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
                    Box(modifier = Modifier.size(220.dp * scale).clip(CircleShape).border(1.dp, Color.Red.copy(alpha = 0.1f), CircleShape))
                    Box(modifier = Modifier.size(170.dp * scale).clip(CircleShape).border(2.dp, Color.Red.copy(alpha = 0.25f), CircleShape))
                    Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.Red.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp).scale(scale))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text(text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()), fontSize = 72.sp, fontWeight = FontWeight(200), color = Color.White, letterSpacing = 4.sp)
                Text(text = "استيقظ", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = alarm.label.ifEmpty { "منبه المسلم" }, color = Color.White.copy(alpha = 0.6f), fontSize = 18.sp)
                Spacer(modifier = Modifier.height(48.dp))

                if (alarm.isSleepAlarm) {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(60.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerColor), shape = RoundedCornerShape(20.dp)) {
                            Text("نعم، استيقظت ✓", color = PrimaryColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(onClick = { showExcuseSheet = true }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B1A1A)), shape = RoundedCornerShape(20.dp)) {
                            Text("لا، أريد النوم", color = Color.White.copy(alpha = 0.9f), fontSize = 18.sp)
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(64.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerColor), shape = RoundedCornerShape(20.dp)) {
                            Text("إيقاف المنبه", color = PrimaryColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = onSnooze, modifier = Modifier.fillMaxWidth()) {
                            Text("غفوة ${alarm.snoozeMinutes} دقائق", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        if (showExcuseSheet) {
            ModalBottomSheet(onDismissRequest = { showExcuseSheet = false }, containerColor = SurfaceContainerColor) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("لماذا لا تستطيع الاستيقاظ؟", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    listOf("مريض", "مسافر", "مرهق جداً", "نسيت المنبه", "استيقظت ثم نمت", "بدون سبب").forEach { excuse ->
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).glassmorphism().clickable { showExcuseSheet = false; onExcuse(excuse) }.padding(16.dp)) {
                            Text(excuse, color = Color.White, fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
