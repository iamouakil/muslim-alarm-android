package com.iamouakil.muslimalarm.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iamouakil.muslimalarm.ui.theme.AuroraBackground
import com.iamouakil.muslimalarm.ui.theme.PrimaryColor
import com.iamouakil.muslimalarm.ui.theme.SecondaryColor
import androidx.compose.ui.graphics.Color

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AuroraBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "منبه المسلم",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "تطبيق إسلامي للمنبه ومواقيت الصلاة والنوم والقيام",
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = {
                        viewModel.setOnboardingCompleted()
                        onFinish()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ابدأ", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}
