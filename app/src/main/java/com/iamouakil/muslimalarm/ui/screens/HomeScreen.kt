package com.iamouakil.muslimalarm.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iamouakil.muslimalarm.data.hijri.HijriDate
import com.iamouakil.muslimalarm.ui.prayer.PrayerTimesViewModel
import com.iamouakil.muslimalarm.ui.theme.*
import androidx.compose.ui.graphics.Color
import java.time.LocalTime

@Composable
fun HomeScreen(viewModel: PrayerTimesViewModel = hiltViewModel()) {
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val nextPrayer by viewModel.nextPrayer.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()

    val greeting = when (LocalTime.now().hour) {
        in 6..11 -> "صباح الخير"
        in 12..17 -> "السلام عليكم"
        else -> "مساء الخير"
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AuroraBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(greeting, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                Text(HijriDate.getTodayHijriString(), fontSize = 16.sp, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                if (prayerTimes != null) {
                    Text("المدينة: $selectedCity", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("الصلاة القادمة: ${nextPrayer?.first ?: ""}", color = SecondaryColor, fontSize = 20.sp)
                    Text("الوقت المتبقي: $countdown", color = Color.White, fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    PrayerTimeRow("الفجر", prayerTimes!!.fajr)
                    PrayerTimeRow("الشروق", prayerTimes!!.sunrise)
                    PrayerTimeRow("الظهر", prayerTimes!!.dhuhr)
                    PrayerTimeRow("العصر", prayerTimes!!.asr)
                    PrayerTimeRow("المغرب", prayerTimes!!.maghrib)
                    PrayerTimeRow("العشاء", prayerTimes!!.isha)
                } else {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            }
        }
    }
}

@Composable
fun PrayerTimeRow(name: String, timeMillis: Long) {
    val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(timeMillis))
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, color = PrimaryColor, fontWeight = FontWeight.Medium)
        Text(time, color = Color.White)
    }
}
