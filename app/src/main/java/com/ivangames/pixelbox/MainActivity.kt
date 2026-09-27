package com.ivangames.pixelbox

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var pixelCanvas: PixelCanvasView
    private lateinit var paletteContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pixelCanvas = findViewById(R.id.pixelCanvas)
        paletteContainer = findViewById(R.id.paletteContainer)

        val templatePath = intent.getStringExtra("template") ?: "templates/sunset.png"

        // Загружаем картинку и её палитру
        val loaded = ImageLoader.loadTemplate(this, templatePath, maxColors = 12)

        // Передаём шаблон в Canvas
        pixelCanvas.loadTemplate(loaded.grid)

        // Строим палитру ИЗ ЦВЕТОВ ЭТОЙ КАРТИНКИ
        buildPalette(loaded.colors)
    }

    private fun buildPalette(colors: List<Int>) {
        val sizePx = (52 * resources.displayMetrics.density).toInt()
        val marginPx = (6 * resources.displayMetrics.density).toInt()

        colors.forEachIndexed { index, color ->
            val frame = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(sizePx, sizePx).apply {
                    marginStart = marginPx
                    marginEnd = marginPx
                }
            }

            // Кружок с цветом
            val colorView = View(this).apply {
                layoutParams = FrameLayout.LayoutParams(sizePx, sizePx)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                    setStroke(4, Color.parseColor("#333344"))
                }
            }
            frame.addView(colorView)

            // Номер поверх кружка
            val numberText = TextView(this).apply {
                text = (index + 1).toString()
                setTextColor(if (isLightColor(color)) Color.BLACK else Color.WHITE)
                textSize = 16f
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
