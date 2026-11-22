package com.raxi.analyser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
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
    private var settingsOpened = false
    private var overlayLaunched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Neon, background = Bg, surface = Panel)) {
                MainScreen(
                    onFloatingClick = {
                        if (Settings.canDrawOverlays(this)) launchOverlayAndClose() else openOverlaySettings()
                    }
                )
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this)) launchOverlayAndClose() else if (!settingsOpened) openOverlaySettings()
    }

    private fun openOverlaySettings() {
        settingsOpened = true
        startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
    }

    private fun launchOverlayAndClose() {
        if (overlayLaunched) return
        overlayLaunched = true
        startService(Intent(this, FlashyOverlayService::class.java))
        finish()
    }
}

@Composable
private fun FlashyMark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(Color(0xFF240909), radius = size.minDimension / 2)
        drawCircle(Neon, radius = size.minDimension / 2, style = androidx.compose.ui.graphics.drawscope.Stroke(width = size.minDimension * .055f))
        val bolt = Path().apply {
            moveTo(w * .56f, h * .13f)
            lineTo(w * .31f, h * .55f)
            lineTo(w * .49f, h * .55f)
            lineTo(w * .40f, h * .87f)
            lineTo(w * .72f, h * .43f)
            lineTo(w * .54f, h * .43f)
            close()
        }
        drawPath(bolt, Brush.linearGradient(listOf(Color(0xFFFF7777), Neon, Color(0xFF9E0000))))
        drawCircle(Color.White.copy(alpha = .8f), radius = w * .035f, center = androidx.compose.ui.geometry.Offset(w * .76f, h * .24f))
    }
}

@Composable
private fun MainScreen(onFloatingClick: () -> Unit) {
    var number by remember { mutableStateOf<Int?>(null) }
    var verifying by remember { mutableStateOf(false) }
    LaunchedEffect(verifying) {
        if (verifying) {
            kotlinx.coroutines.delay(2000)
            number = Random.nextInt(0, 10)
            verifying = false
        }
    }
    var session by remember { mutableStateOf(System.currentTimeMillis().toString().takeLast(8)) }
    val n = number
    val sizeLabel = n?.let { if (it <= 4) "SMALL" else "BIG" } ?: "—"
    val colourLabel = n?.let {
        when (it) { 0 -> "RED + VIOLET"; 5 -> "GREEN + VIOLET"; 1, 3, 7, 9 -> "GREEN"; else -> "RED" }
    } ?: "—"
    val resultColour = n?.let { if (it % 2 == 0) Neon else Green } ?: Color.White

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF280707), Bg, Color(0xFF120707)))).padding(18.dp), contentAlignment = Alignment.Center) {
        Column(
            Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0xFF301010), Color(0xFF100909))), RoundedCornerShape(28.dp)).padding(2.dp)
                .background(Panel, RoundedCornerShape(26.dp)).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FlashyMark(Modifier.size(38.dp))
                Spacer(Modifier.width(10.dp))
                Text("FLASHY ADMIN PANEL", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            }
            Spacer(Modifier.height(7.dp))
            Text("RAXI WIN  •  WINGO 1 MIN", color = Neon, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth().background(Color(0xFF241010), RoundedCornerShape(12.dp)).padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("DEMO SESSION ID", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(session, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))
            Column(Modifier.fillMaxWidth().background(Brush.radialGradient(listOf(Color(0xFF5B1111), Color(0xFF170909))), RoundedCornerShape(22.dp)).padding(vertical = 24.dp, horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("RANDOMLY GENERATED", color = Muted, fontSize = 11.sp, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(8.dp))
                Text(if (verifying) "•••" else (n?.toString() ?: "—"), color = resultColour, fontSize = 76.sp, fontWeight = FontWeight.Black)
                Text(if (n == null) "PRESS EXECUTE" else "$sizeLabel  •  $colourLabel", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(18.dp))
            Button(onClick = { if (!verifying) { number = null; verifying = true; session = System.currentTimeMillis().toString().takeLast(8) } },
                modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Neon)) {
                Text(if (verifying) "VERIFYING DEMO • 2 SEC" else "EXECUTE", color = Color.White, fontWeight = FontWeight.Black, letterSpacing = 2.sp, fontSize = 17.sp)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onFloatingClick, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
                FlashyMark(Modifier.size(25.dp)); Spacer(Modifier.width(9.dp)); Text("ENABLE FLOATING FLASHY ICON", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFF492222))
            Spacer(Modifier.height(10.dp))
            Text("DEMO MODE", color = Neon, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            Text("Numbers are generated randomly. This app does not access, predict, or control RAXI Win results.", color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 16.sp)
        }
    }
}

@Composable
fun FloatingPanel(onClose: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var number by remember { mutableStateOf<Int?>(null) }
    val n = number
    Column(horizontalAlignment = Alignment.End) {
        if (expanded) {
            Column(Modifier.width(270.dp).background(Color(0xFF171111), RoundedCornerShape(20.dp)).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("FLASHY PANEL", color = Color.White, fontWeight = FontWeight.Black)
                    Text("×", color = Neon, fontSize = 25.sp, modifier = Modifier.clickable { expanded = false })
                }
                Text("RAXI WIN • DEMO ONLY", color = Neon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Text(n?.toString() ?: "—", color = if ((n ?: 0) % 2 == 0) Neon else Green, fontSize = 54.sp, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.CenterHorizontally))
                Text(n?.let { "${if (it <= 4) "SMALL" else "BIG"} • ${when(it) { 0 -> "RED + VIOLET"; 5 -> "GREEN + VIOLET"; 1,3,7,9 -> "GREEN"; else -> "RED" }}" } ?: "Tap execute to generate", color = Color.White, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(10.dp))
                Button(onClick = { number = Random.nextInt(0, 10) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Neon)) { Text("EXECUTE") }
                Text("Random demo output — not a game prediction.", color = Muted, fontSize = 9.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }
            Spacer(Modifier.height(10.dp))
        }
        Box(Modifier.size(58.dp).background(Color(0xFF210707), CircleShape).clickable { expanded = !expanded }.padding(4.dp), contentAlignment = Alignment.Center) {
            FlashyMark(Modifier.fillMaxSize())
        }
    }
}
