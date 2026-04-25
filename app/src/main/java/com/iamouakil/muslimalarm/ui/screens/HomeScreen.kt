package com.iamouakil.muslimalarm.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iamouakil.muslimalarm.ui.prayer.PrayerTimesViewModel
import com.iamouakil.muslimalarm.ui.theme.AuroraBackground
import com.iamouakil.muslimalarm.ui.theme.PrimaryColor
import com.iamouakil.muslimalarm.ui.theme.SecondaryColor
import com.iamouakil.muslimalarm.ui.theme.glassmorphism
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: PrayerTimesViewModel = hiltViewModel()) {
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val nextPrayer by viewModel.nextPrayer.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val searchResults by viewModel.citySearchResults.collectAsState()

    var showCitySheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            viewModel.enableGps()
        }
    }

    AuroraBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clickable { viewModel.searchCities(""); showCitySheet = true }
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(selectedCity, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier.fillMaxWidth().glassmorphism().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("الصلاة القادمة", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = nextPrayer?.first ?: "--",
                        color = SecondaryColor,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(countdown, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = nextPrayer?.second?.let { formatEpoch(it) } ?: "--:--",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth().glassmorphism().padding(16.dp)) {
                Column {
                    val prayers = listOf(
                        "الفجر" to prayerTimes?.fajr,
                        "الشروق" to prayerTimes?.sunrise,
                        "الظهر" to prayerTimes?.dhuhr,
                        "العصر" to prayerTimes?.asr,
                        "المغرب" to prayerTimes?.maghrib,
                        "العشاء" to prayerTimes?.isha
                    )
                    prayers.forEach { (name, time) ->
                        val isNext = nextPrayer?.first == name
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, color = if (isNext) SecondaryColor else Color.White, fontSize = 16.sp, fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal)
                            Text(time?.let { formatEpoch(it) } ?: "--:--", color = if (isNext) SecondaryColor else Color.White, fontSize = 16.sp, fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }
    }

    if (showCitySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCitySheet = false },
            containerColor = Color(0xFF1C211B)
        ) {
            Column(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it; viewModel.searchCities(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("ابحث عن مدينة...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Spacer(Modifier.height(12.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                    item {
                        TextButton(
                            onClick = {
                                locationLauncher.launch(arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ))
                                showCitySheet = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryColor)
                            Spacer(Modifier.width(8.dp))
                            Text("استخدام الموقع الحالي (GPS)", color = PrimaryColor, modifier = Modifier.weight(1f))
                        }
                        HorizontalDivider(color = Color.DarkGray)
                    }
                    items(searchResults) { city ->
                        Text(
                            text = "${city.nameAr} - ${city.countryAr}",
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                                .clickable { viewModel.selectCity(city); showCitySheet = false }
                                .padding(16.dp)
                        )
                        HorizontalDivider(color = Color.DarkGray)
                    }
                }
            }
        }
    }
}

private fun formatEpoch(epochMillis: Long): String =
    SimpleDateFormat("HH:mm", Locale.US).format(Date(epochMillis))
