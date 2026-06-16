package com.iamouakil.muslimalarm.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BgColor = Color(0xFF0A0F0A)
val PrimaryColor = Color(0xFF95D4B3)
val PrimaryContainerColor = Color(0xFF2D6A4F)
val SecondaryColor = Color(0xFFE6C364)
val SurfaceContainerColor = Color(0xFF1C211B)
val SleepBadgeColor = Color(0xFF3F51B5)

val NightBluePrimary = Color(0xFF1A237E)
val NightBlueSecondary = Color(0xFF64B5F6)
val NightBlueBg = Color(0xFF0D1B2A)

val GoldenPrimary = Color(0xFFD4AF37)
val GoldenSecondary = Color(0xFFFFF8E1)
val GoldenBg = Color(0xFF1C1C1C)

val RamadanPrimary = Color(0xFF8D6E63)
val RamadanSecondary = Color(0xFFFFCA28)
val RamadanBg = Color(0xFF1B0000)

val IslamicGreenColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = BgColor,
    surface = SurfaceContainerColor,
    primaryContainer = PrimaryContainerColor,
)

val NightBlueColorScheme = darkColorScheme(
    primary = NightBluePrimary,
    secondary = NightBlueSecondary,
    background = NightBlueBg,
    surface = NightBlueBg,
    primaryContainer = NightBluePrimary,
)

val GoldenColorScheme = darkColorScheme(
    primary = GoldenPrimary,
    secondary = GoldenSecondary,
    background = GoldenBg,
    surface = GoldenBg,
    primaryContainer = GoldenPrimary,
)

val RamadanColorScheme = darkColorScheme(
    primary = RamadanPrimary,
    secondary = RamadanSecondary,
    background = RamadanBg,
    surface = RamadanBg,
    primaryContainer = RamadanPrimary,
)

@Composable
fun MuslimAlarmTheme(
    theme: String = "الأخضر الإسلامي",
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        "الأزرق الليلي" -> NightBlueColorScheme
        "الذهبي" -> GoldenColorScheme
        "الرمضاني" -> RamadanColorScheme
        else -> IslamicGreenColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
