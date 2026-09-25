package com.ivangames.pixelbox

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PixelCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var gridSize = 16
        set(value) {
            field = value
            pixels = Array(value) { IntArray(value) { Color.WHITE } }
            invalidate()
        }

    private var pixels: Array<IntArray> = Array(gridSize) { IntArray(gridSize) { Color.WHITE } }
    private val cellPaint = Paint().apply { style = Paint.Style.FILL }
    private val gridPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#333344")
        strokeWidth = 2f
    }

    var currentColor: Int = Color.RED

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellW = width.toFloat() / gridSize
        val cellH = height.toFloat() / gridSize

        // Рисуем пиксели
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                cellPaint.color = pixels[row][col]
                canvas.drawRect(
                    col * cellW,
                    row * cellH,
                    (col + 1) * cellW,
                    (row + 1) * cellH,
                    cellPaint
                )
            }
        }

        // Рисуем сетку
        for (i in 0..gridSize) {
            canvas.drawLine(i * cellW, 0f, i * cellW, height.toFloat(), gridPaint)
            canvas.drawLine(0f, i * cellH, width.toFloat(), i * cellH, gridPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            val cellW = width.toFloat() / gridSize
            val cellH = height.toFloat() / gridSize
            val col = (event.x / cellW).toInt()
            val row = (event.y / cellH).toInt()
            if (row in 0 until gridSize && col in 0 until gridSize) {
                pixels[row][col] = currentColor
                invalidate()
            }
        }
        return true
    }
}
