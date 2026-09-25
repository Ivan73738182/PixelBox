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

    private val colors = listOf(
        0xFFFF0000.toInt(), // 1 - красный
        0xFFFF9800.toInt(), // 2 - оранжевый
        0xFFFFEB3B.toInt(), // 3 - жёлтый
        0xFF4CAF50.toInt(), // 4 - зелёный
        0xFF00BCD4.toInt(), // 5 - голубой
        0xFF2196F3.toInt(), // 6 - синий
        0xFF9C27B0.toInt(), // 7 - фиолетовый
        0xFFFF69B4.toInt(), // 8 - розовый
        0xFF795548.toInt(), // 9 - коричневый
        0xFF000000.toInt(), // 10 - чёрный
        0xFF9E9E9E.toInt(), // 11 - серый
        0xFFFFFFFF.toInt(), // 12 - белый
        0xFF00FF00.toInt(), // 13 - лайм
        0xFF00FFFF.toInt(), // 14 - циан
        0xFFFF00FF.toInt(), // 15 - маджента
        0xFF8B0000.toInt()  // 16 - тёмно-красный
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

pixelCanvas = findViewById(R.id.pixelCanvas)
paletteContainer = findViewById(R.id.paletteContainer)

val templateId = intent.getStringExtra("template") ?: "heart"
val template = TemplateData.getTemplate(templateId)
pixelCanvas.loadTemplate(template)

buildPalette()
    }

    private fun buildPalette() {
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

            // Цифра поверх кружка
            val numberText = TextView(this).apply {
                text = (index + 1).toString()
                setTextColor(if (color == 0xFFFFFFFF.toInt() || color == 0xFFFFEB3B.toInt()) Color.BLACK else Color.WHITE)
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
}
