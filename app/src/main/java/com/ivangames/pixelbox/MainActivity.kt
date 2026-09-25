package com.ivangames.pixelbox

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var pixelCanvas: PixelCanvasView
    private lateinit var paletteContainer: LinearLayout

    private val colors = listOf(
        Color.RED,
        Color.parseColor("#FF9800"),
        Color.YELLOW,
        Color.GREEN,
        Color.parseColor("#00BCD4"),
        Color.BLUE,
        Color.parseColor("#9C27B0"),
        Color.BLACK,
        Color.GRAY,
        Color.WHITE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pixelCanvas = findViewById(R.id.pixelCanvas)
        paletteContainer = findViewById(R.id.paletteContainer)

        buildPalette()
    }

    private fun buildPalette() {
        val sizePx = (48 * resources.displayMetrics.density).toInt()
        val marginPx = (6 * resources.displayMetrics.density).toInt()

        colors.forEach { color ->
            val colorView = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(sizePx, sizePx).apply {
                    marginStart = marginPx
                    marginEnd = marginPx
                }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                    setStroke(4, Color.parseColor("#333344"))
                }
            }
            colorView.setOnClickListener {
                pixelCanvas.currentColor = color
                highlightSelected(colorView)
            }
            paletteContainer.addView(colorView)
        }
    }

    private fun highlightSelected(selected: View) {
        for (i in 0 until paletteContainer.childCount) {
            val child = paletteContainer.getChildAt(i)
            (child.background as? GradientDrawable)?.setStroke(4, Color.parseColor("#333344"))
        }
        (selected.background as? GradientDrawable)?.setStroke(8, Color.WHITE)
    }
}
