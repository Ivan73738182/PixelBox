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
            rebuildArrays()
            invalidate()
        }

    var currentColor: Int = Color.RED
    var currentColorNumber: Int = 1

    private var template: Array<IntArray> = Array(gridSize) { IntArray(gridSize) { 0 } }
    private var pixels: Array<IntArray> = Array(gridSize) { IntArray(gridSize) { Color.WHITE } }

    private val cellPaint = Paint().apply { style = Paint.Style.FILL }
    private val gridPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#333344")
        strokeWidth = 2f
    }
    private val numberPaint = Paint().apply {
        color = Color.parseColor("#666666")
        textSize = 24f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private fun rebuildArrays() {
        template = Array(gridSize) { IntArray(gridSize) { 0 } }
        pixels = Array(gridSize) { IntArray(gridSize) { Color.WHITE } }
    }

    fun loadTemplate(newTemplate: Array<IntArray>) {
        gridSize = newTemplate.size
        template = newTemplate
        pixels = Array(gridSize) { IntArray(gridSize) { Color.WHITE } }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellW = width.toFloat() / gridSize
        val cellH = height.toFloat() / gridSize

        // Пиксели
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                cellPaint.color = pixels[row][col]
                canvas.drawRect(
                    col * cellW, row * cellH,
                    (col + 1) * cellW, (row + 1) * cellH,
                    cellPaint
                )
            }
        }

        // Сетка
        for (i in 0..gridSize) {
            canvas.drawLine(i * cellW, 0f, i * cellW, height.toFloat(), gridPaint)
            canvas.drawLine(0f, i * cellH, width.toFloat(), i * cellH, gridPaint)
        }

        // Цифры на клетках, где шаблон требует цвет
        numberPaint.textSize = cellH * 0.5f
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val number = template[row][col]
                if (number > 0 && pixels[row][col] == Color.WHITE) {
                    val cx = col * cellW + cellW / 2
                    val cy = row * cellH + cellH / 2 - (numberPaint.descent() + numberPaint.ascent()) / 2
                    canvas.drawText(number.toString(), cx, cy, numberPaint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            val cellW = width.toFloat() / gridSize
            val cellH = height.toFloat() / gridSize
            val col = (event.x / cellW).toInt()
            val row = (event.y / cellH).toInt()
            if (row in 0 until gridSize && col in 0 until gridSize) {
                val expected = template[row][col]
                if (expected > 0 && expected == currentColorNumber) {
                    pixels[row][col] = currentColor
                    invalidate()
                }
            }
        }
        return true
    }
}
