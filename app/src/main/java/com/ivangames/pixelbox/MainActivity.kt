package com.ivangames.pixelbox

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var pixelCanvas: PixelCanvasView
    private lateinit var paletteContainer: LinearLayout
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pixelCanvas = findViewById(R.id.pixelCanvas)
        paletteContainer = findViewById(R.id.paletteContainer)
        errorText = findViewById(R.id.errorText)

        val templatePath = intent.getStringExtra("template") ?: "templates/sunset.png"

        try {
            // Загружаем картинку
            val loaded = ImageLoader.loadTemplate(this, templatePath, maxColors = 12)

            // Показываем отладку
            errorText.text = "Загружено: ${loaded.grid.size}×${loaded.grid[0].size}\nЦветов: ${loaded.colors.size}"
            errorText.visibility = View.VISIBLE

            // Передаём шаблон
            pixelCanvas.loadTemplate(loaded.grid)

            // Палитра
            buildPalette(loaded.colors)
        } catch (e: Exception) {
            // Если упало — показываем ошибку
            errorText.text = "ОШИБКА:\n${e.message}\n\n${e.stackTraceToString().take(500)}"
            errorText.visibility = View.VISIBLE
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun buildPalette(colors: List<Int>) {
        val sizePx = (48 * resources.displayMetrics.density).toInt()
        val marginPx = (6 * resources.displayMetrics.density).toInt()

        colors.forEachIndexed { index, color ->
            val frame = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(sizePx, sizePx).apply {
                    marginStart = marginPx
                    marginEnd = marginPx
                }
            }

            val colorView = View(this).apply {
                layoutParams = FrameLayout.LayoutParams(sizePx, sizePx)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                    setStroke(4, Color.parseColor("#333344"))
                }
            }
            frame.addView(colorView)

            val numberText = TextView(this).apply {
                text = (index + 1).toString()
                setTextColor(if (isLightColor(color)) Color.BLACK else Color.WHITE)
                textSize = 14f
                gravity = Gravity.CENTER
                layoutParams = FrameLayout.LayoutParams(sizePx, sizePx)
            }
            frame.addView(numberText)

            frame.setOnClickListener {
                pixelCanvas.currentColor = color
                pixelCanvas.currentColorNumber = index + 1
                highlightSelected(frame)
            }
            paletteContainer.addView(frame)
        }
    }

    private fun highlightSelected(selected: FrameLayout) {
        for (i in 0 until paletteContainer.childCount) {
            val child = paletteContainer.getChildAt(i) as FrameLayout
            val colorView = child.getChildAt(0) as View
            (colorView.background as? GradientDrawable)?.setStroke(4, Color.parseColor("#333344"))
        }
        val selectedColorView = selected.getChildAt(0) as View
        (selectedColorView.background as? GradientDrawable)?.setStroke(8, Color.WHITE)
    }

    private fun isLightColor(color: Int): Boolean {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        val brightness = (r * 299 + g * 587 + b * 114) / 1000
        return brightness > 150
    }
}
