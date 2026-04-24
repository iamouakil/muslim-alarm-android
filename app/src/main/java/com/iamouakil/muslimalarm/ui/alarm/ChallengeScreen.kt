package com.iamouakil.muslimalarm.ui.alarm

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeScreen(challengeType: String, onSuccess: () -> Unit) {
    if (challengeType == "none") { LaunchedEffect(Unit) { onSuccess() }; return }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
            AuroraBackground()
            Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(48.dp))
                Text("تحدي الاستيقاظ", color = PrimaryColor, fontSize = 28.sp, fontWeight = FontWeight.Light, letterSpacing = 2.sp)
                Text("أثبت يقظتك لإيقاف المنبه", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(32.dp))
                when (challengeType) {
                    "math_easy" -> MathEasyChallenge(onSuccess)
                    "math_hard" -> MathHardChallenge(onSuccess, false)
                    "math_series" -> MathHardChallenge(onSuccess, true)
                    "type_phrase" -> TypePhraseChallenge(onSuccess)
                    "shake" -> ShakeChallenge(onSuccess)
                    "tap_target" -> TapTargetChallenge(onSuccess)
                    "order_words" -> OrderWordsChallenge(onSuccess)
                }
            }
        }
    }
}

@Composable
fun MathEasyChallenge(onSuccess: () -> Unit) {
    val a by remember { mutableStateOf(Random.nextInt(5, 30)) }
    val b by remember { mutableStateOf(Random.nextInt(5, 30)) }
    val answer = a + b
    val options = remember { (List(3) { var o = answer + Random.nextInt(-8, 8); if (o == answer) o++; o } + answer).shuffled() }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.glassmorphism().padding(horizontal = 32.dp, vertical = 20.dp)) {
            Text("$a + $b = ?", fontSize = 56.sp, fontWeight = FontWeight(200), color = Color.White)
        }
        Spacer(modifier = Modifier.height(32.dp))
        options.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                row.forEach { opt ->
                    Box(modifier = Modifier.size(120.dp).glassmorphism().clickable { if (opt == answer) onSuccess() }, contentAlignment = Alignment.Center) {
                        Text(opt.toString(), fontSize = 32.sp, color = Color.White, fontWeight = FontWeight(300))
                    }
                }
            }
        }
    }
}

@Composable
fun MathHardChallenge(onSuccess: () -> Unit, series: Boolean) {
    var step by remember { mutableStateOf(1) }
    val maxSteps = if (series) 5 else 1
    var a by remember { mutableStateOf(Random.nextInt(10, 99)) }
    var b by remember { mutableStateOf(Random.nextInt(10, 50)) }
    var input by remember { mutableStateOf("") }
    val answer = a + b

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        if (series) Text("$step / $maxSteps", color = SecondaryColor, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.glassmorphism().padding(24.dp)) {
            Text("$a + $b = ${input.ifEmpty { "?" }}", fontSize = 48.sp, fontWeight = FontWeight(200), color = Color.White)
        }
        Spacer(modifier = Modifier.height(24.dp))
        listOf(listOf("1","2","3"), listOf("4","5","6"), listOf("7","8","9"), listOf("C","0","OK")).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier.size(88.dp).glassmorphism()
                            .then(if (key == "OK") Modifier.background(PrimaryContainerColor.copy(alpha = 0.5f), RoundedCornerShape(24.dp)) else Modifier)
                            .clickable {
                                when (key) {
                                    "C" -> input = ""
                                    "OK" -> { if (input.toIntOrNull() == answer) { if (step >= maxSteps) onSuccess() else { step++; a = Random.nextInt(10, 99); b = Random.nextInt(10, 50); input = "" } } else input = "" }
                                    else -> if (input.length < 5) input += key
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) { Text(key, fontSize = 28.sp, color = if (key == "OK") PrimaryColor else Color.White, fontWeight = FontWeight(300)) }
                }
            }
        }
    }
}

@Composable
fun TypePhraseChallenge(onSuccess: () -> Unit) {
    val phrases = listOf("سبحان الله وبحمده", "لا إله إلا الله", "الحمد لله رب العالمين", "الله أكبر كبيراً", "أستغفر الله العظيم")
    val target by remember { mutableStateOf(phrases.random()) }
    var input by remember { mutableStateOf("") }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("اكتب هذا الذكر:", color = Color.Gray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth().glassmorphism().padding(24.dp)) {
            Text(target, color = PrimaryColor, fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = input, onValueChange = { input = it; if (it.trim() == target) onSuccess() }, placeholder = { Text("اكتب هنا...", color = Color.Gray) }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PrimaryColor, unfocusedBorderColor = Color.White.copy(alpha = 0.3f)), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
    }
}

@Composable
fun ShakeChallenge(onSuccess: () -> Unit) {
    val context = LocalContext.current
    var shakes by remember { mutableStateOf(0) }
    val maxShakes = 30
    DisposableEffect(Unit) {
        val sm = context.getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
        val acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        var lastUpdate = 0L; var lx = 0f; var ly = 0f; var lz = 0f
        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent?) {
                if (e == null) return
                val now = System.currentTimeMillis()
                if (now - lastUpdate > 100) {
                    val dt = now - lastUpdate; lastUpdate = now
                    val x = e.values[0]; val y = e.values[1]; val z = e.values[2]
                    val speed = Math.abs(x+y+z-lx-ly-lz) / dt * 10000
                    if (speed > 600) { shakes++; if (shakes >= maxShakes) onSuccess() }
                    lx = x; ly = y; lz = z
                }
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) {}
        }
        sm.registerListener(listener, acc, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose { sm.unregisterListener(listener) }
    }
    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(200.dp)) {
            drawArc(color = Color.White.copy(alpha = 0.1f), startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(16.dp.toPx(), cap = StrokeCap.Round))
            drawArc(color = PrimaryColor, startAngle = -90f, sweepAngle = (shakes.toFloat() / maxShakes) * 360f, useCenter = false, style = Stroke(16.dp.toPx(), cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("هز الهاتف", color = Color.Gray, fontSize = 16.sp)
            Text("$shakes/$maxShakes", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TapTargetChallenge(onSuccess: () -> Unit) {
    var taps by remember { mutableStateOf(0) }
    var px by remember { mutableStateOf(200) }
    var py by remember { mutableStateOf(400) }
    Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
        Text("$taps / 10", color = Color.Gray, fontSize = 18.sp, modifier = Modifier.align(Alignment.TopCenter))
        Box(modifier = Modifier.offset { IntOffset(px, py) }.size(72.dp).clip(CircleShape).background(SecondaryColor).border(3.dp, Color.White.copy(alpha = 0.3f), CircleShape).clickable { taps++; if (taps >= 10) onSuccess() else { px = Random.nextInt(20, 700); py = Random.nextInt(60, 900) } }, contentAlignment = Alignment.Center) {
            Text("👆", fontSize = 28.sp)
        }
    }
}

@Composable
fun OrderWordsChallenge(onSuccess: () -> Unit) {
    val phrases = listOf(listOf("سبحان", "الله", "وبحمده"), listOf("لا", "إله", "إلا", "الله"), listOf("الحمد", "لله", "رب", "العالمين"))
    val original by remember { mutableStateOf(phrases.random()) }
    var shuffled by remember { mutableStateOf(original.shuffled()) }
    var selected by remember { mutableStateOf(listOf<String>()) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().height(60.dp).glassmorphism().padding(12.dp), contentAlignment = Alignment.Center) {
            if (selected.isEmpty()) Text("اضغط على الكلمات بالترتيب", color = Color.Gray, fontSize = 14.sp)
            else Text(selected.joinToString(" "), color = PrimaryColor, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth(), ) {
            shuffled.forEach { word ->
                if (!selected.contains(word)) {
                    Box(modifier = Modifier.glassmorphism().padding(horizontal = 16.dp, vertical = 10.dp).clickable {
                        val newSelected = selected + word
                        if (newSelected.size == original.size) { if (newSelected == original) onSuccess() else { selected = listOf() } }
                        else selected = newSelected
                    }) { Text(word, color = Color.White, fontSize = 18.sp) }
                }
            }
        }
        TextButton(onClick = { selected = listOf() }) { Text("إعادة", color = Color.Gray) }
    }
}
