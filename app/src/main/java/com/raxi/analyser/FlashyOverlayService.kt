package com.raxi.analyser

import android.app.Service
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class FlashyOverlayService : Service() {
    private var wm: WindowManager? = null
    private var currentView: View? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private val handler = Handler(Looper.getMainLooper())
    private var result: TextView? = null
    private var status: TextView? = null
    private var execute: Button? = null
    private var expanded = false
    private var busy = false

    override fun onCreate() {
        super.onCreate()
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return }
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        showIcon()
    }

    private fun showIcon() {
        val icon = FlashyLogoView(this).apply {
            setOnClickListener { if (expanded) showIcon() else showPanel() }
            setOnTouchListener(DragTouch())
        }
        val lp = WindowManager.LayoutParams(
            dp(60), dp(60), WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.END; x = dp(16); y = dp(180) }
        replaceView(icon, lp)
        expanded = false
    }

    private fun showPanel() {
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = rounded(Color.rgb(23, 13, 13), dp(20), Color.rgb(115, 25, 25))
        }
        val header = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        header.addView(FlashyLogoView(this), LinearLayout.LayoutParams(dp(38), dp(38)))
        header.addView(TextView(this).apply {
            text = "  FLASHY ADMIN PANEL"; textSize = 16f; setTextColor(Color.WHITE); typeface = Typeface.DEFAULT_BOLD
        }, LinearLayout.LayoutParams(0, dp(44), 1f))
        header.addView(TextView(this).apply {
            text = "×"; textSize = 28f; setTextColor(Color.RED); setOnClickListener { showIcon() }
        })
        panel.addView(header)
        panel.addView(text("RAXI WIN • RANDOM DEMO", Color.rgb(255, 55, 55), 11f, true).apply {
            gravity = Gravity.CENTER; setPadding(0, dp(8), 0, dp(10))
        })
        result = text("—", Color.rgb(30, 190, 105), 58f, true).apply { gravity = Gravity.CENTER }
        panel.addView(result)
        status = text("PRESS EXECUTE", Color.LTGRAY, 12f, true).apply {
            gravity = Gravity.CENTER; setPadding(0, 0, 0, dp(12))
        }
        panel.addView(status)
        execute = Button(this).apply {
            text = "EXECUTE"; textSize = 16f; setTextColor(Color.WHITE)
            background = rounded(Color.rgb(255, 38, 38), dp(14), Color.TRANSPARENT)
            setOnClickListener { runDemo() }
        }
        panel.addView(execute, LinearLayout.LayoutParams(-1, dp(52)))
        panel.addView(text("2-second demo animation • Random output only", Color.LTGRAY, 10f, false).apply {
            gravity = Gravity.CENTER; setPadding(0, dp(10), 0, 0)
        })
        val lp = WindowManager.LayoutParams(
            dp(290), WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.END; x = dp(12); y = dp(150) }
        replaceView(panel, lp)
        expanded = true
    }

    private fun runDemo() {
        if (busy) return
        busy = true
        result?.text = "•••"
        status?.text = "VERIFYING DEMO…"
        execute?.isEnabled = false
        execute?.text = "PLEASE WAIT"
        handler.postDelayed({
            val n = (0..9).random()
            result?.text = n.toString()
            result?.setTextColor(if (n % 2 == 0) Color.rgb(255, 55, 55) else Color.rgb(30, 190, 105))
            status?.text = "${if (n <= 4) "SMALL" else "BIG"} • RANDOM DEMO"
            execute?.isEnabled = true
            execute?.text = "EXECUTE"
            busy = false
        }, 2000)
    }

    private fun replaceView(view: View, lp: WindowManager.LayoutParams) {
        currentView?.let { old -> try { wm?.removeView(old) } catch (_: Exception) {} }
        currentView = view
        layoutParams = lp
        try { wm?.addView(view, lp) } catch (e: Exception) {
            Toast.makeText(this, "FLASHY overlay error: ${e.message}", Toast.LENGTH_LONG).show()
            stopSelf()
        }
    }

    private fun text(value: String, color: Int, size: Float, bold: Boolean) = TextView(this).apply {
        text = value; setTextColor(color); textSize = size
        if (bold) typeface = Typeface.DEFAULT_BOLD
    }

    private fun rounded(color: Int, radius: Int, stroke: Int) = GradientDrawable().apply {
        setColor(color); cornerRadius = radius.toFloat()
        if (stroke != Color.TRANSPARENT) setStroke(dp(1), stroke)
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private inner class DragTouch : View.OnTouchListener {
        private var sx = 0; private var sy = 0; private var dx = 0f; private var dy = 0f
        override fun onTouch(v: View, e: MotionEvent): Boolean {
            val p = layoutParams ?: return false
            when (e.action) {
                MotionEvent.ACTION_DOWN -> { sx = p.x; sy = p.y; dx = e.rawX; dy = e.rawY; return false }
                MotionEvent.ACTION_MOVE -> {
                    p.x = sx - (e.rawX - dx).toInt(); p.y = sy + (e.rawY - dy).toInt()
                    try { wm?.updateViewLayout(v, p) } catch (_: Exception) {}
                    return true
                }
            }
            return false
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        currentView?.let { try { wm?.removeView(it) } catch (_: Exception) {} }
        currentView = null
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}

private class FlashyLogoView(context: android.content.Context) : View(context) {
    private val boltPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(255, 48, 48); style = Paint.Style.FILL }
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(130, 20, 20); style = Paint.Style.STROKE; strokeWidth = 4f }
    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        val s = minOf(width, height).toFloat(); val x = width / 2f; val y = height / 2f
        c.drawCircle(x, y, s * .47f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(35, 5, 5) })
        c.drawCircle(x, y, s * .44f, ringPaint)
        val p = Path().apply {
            moveTo(x + s*.08f, y - s*.32f); lineTo(x - s*.22f, y + s*.02f)
            lineTo(x - s*.02f, y + s*.02f); lineTo(x - s*.12f, y + s*.34f)
            lineTo(x + s*.24f, y - s*.08f); lineTo(x + s*.04f, y - s*.08f); close()
        }
        c.drawPath(p, boltPaint)
    }
}