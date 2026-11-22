package com.raxi.analyser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private val Bg = Color(0xFF090909)
private val Panel = Color(0xFF171111)
private val Neon = Color(0xFFFF2929)
private val Muted = Color(0xFFB9A6A6)
private val Green = Color(0xFF20B86A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Neon, background = Bg, surface = Panel)) {
                FlashyApp()
            }
        }
    }
}

@Composable
fun FlashyApp() {
    var number by remember { mutableStateOf<Int?>(null) }
    val n = number
    val sizeLabel = n?.let { if (it <= 4) "SMALL" else "BIG" } ?: "—"
    val colourLabel = n?.let {
        when (it) {
            0 -> "RED + VIOLET"
            5 -> "GREEN + VIOLET"
            1, 3, 7, 9 -> "GREEN"
            else -> "RED"
        }
    } ?: "—"
    val resultColour = n?.let { if (it % 2 == 0) Neon else Green } ?: Color.White

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF280707), Bg, Color(0xFF120707))))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF301010), Color(0xFF100909))), RoundedCornerShape(26.dp))
                .padding(2.dp)
                .background(Panel, RoundedCornerShape(24.dp))
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(9.dp).background(Green, CircleShape))
                Spacer(Modifier.width(9.dp))
                Text("FLASHY ADMIN PANEL", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text("RAXI WIN  •  WINGO 1 MIN", color = Neon, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(26.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.radialGradient(listOf(Color(0xFF5B1111), Color(0xFF170909))), RoundedCornerShape(22.dp))
                    .padding(vertical = 26.dp, horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("GENERATED NUMBER", color = Muted, fontSize = 11.sp, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(8.dp))
                Text(n?.toString() ?: "—", color = resultColour, fontSize = 76.sp, fontWeight = FontWeight.Black)
                Text(if (n == null) "PRESS EXECUTE TO GENERATE" else "$sizeLabel  •  $colourLabel",
                    color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(22.dp))
            Button(onClick = { number = Random.nextInt(0, 10) },
                modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Neon)) {
                Text("EXECUTE", color = Color.White, fontWeight = FontWeight.Black, letterSpacing = 2.sp, fontSize = 16.sp)
            }
            Spacer(Modifier.height(18.dp))
            HorizontalDivider(color = Color(0xFF492222))
            Spacer(Modifier.height(12.dp))
            Text("RANDOM DEMO GENERATOR", color = Neon, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            Spacer(Modifier.height(6.dp))
            Text("This app generates a random number from 0 to 9. It does not read, predict, or control RAXI Win results.",
                color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 16.sp)
        }
    }
}
