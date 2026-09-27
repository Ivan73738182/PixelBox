package com.ivangames.pixelbox

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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

    // template[row][col]: 0 = пусто (не красить), 1..N = цвет по номеру
    private var template: Array<IntArray> = Array(gridSize) { IntArray(gridSize) { 0 } }
    // pixels[row][col]: 0 = не покрашено, иначе ARGB
    private var pixels: Array<IntArray> = Array(gridSize) { IntArray(gridSize) { 0 } }

    private val cellPaint = Paint().apply { style = Paint.Style.FILL }
    private val gridPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#333344")
        strokeWidth = 1.5f
    }
    private val numberPaint = Paint().apply {
        color = Color.parseColor("#888888")
        textSize = 24f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private var cellSize = 0f
    private var boardLeft = 0f
    private var boardTop = 0f

    private fun rebuildArrays() {
        template = Array(gridSize) { IntArray(gridSize) { 0 } }
        pixels = Array(gridSize) { IntArray(gridSize) { 0 } }
    }

    fun loadTemplate(newTemplate: Array<IntArray>) {
        gridSize = newTemplate.size
        template = newTemplate
        pixels = Array(gridSize) { IntArray(gridSize) { 0 } }
        post { fitToScreen() }
        invalidate()
    }

    private fun fitToScreen() {
        if (width == 0 || height == 0) return
        val size = minOf(width, height).toFloat() * 0.95f
        cellSize = size / gridSize
        boardLeft = (width - cellSize * gridSize) / 2f
        boardTop = (height - cellSize * gridSize) / 2f
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        fitToScreen()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Белый фон всего экрана
        canvas.drawColor(Color.WHITE)

        // Пиксели
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val expected = template[row][col]
                val drawn = pixels[row][col]

                val color = when {
                    drawn != 0 -> drawn                         // закрашено
                    expected == 0 -> Color.parseColor("#CCCCCC") // фон (не красить)
                    else -> Color.WHITE                         // ждёт раскраски
                }

                cellPaint.color = color
                canvas.drawRect(
                    boardLeft + col * cellSize,
                    boardTop + row * cellSize,
                    boardLeft + (col + 1) * cellSize,
                    boardTop + (row + 1) * cellSize,
                    cellPaint
                )
            }
        }

        // Сетка
        for (i in 0..gridSize) {
            canvas.drawLine(
                boardLeft + i * cellSize, boardTop,
                boardLeft + i * cellSize, boardTop + gridSize * cellSize,
                gridPaint
            )
            canvas.drawLine(
                boardLeft, boardTop + i * cellSize,
                boardLeft + gridSize * cellSize, boardTop + i * cellSize,
                gridPaint
            )
        }

        // Цифры на клетках, где надо красить и ещё не покрашено
        numberPaint.textSize = cellSize * 0.5f
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val number = template[row][col]
                if (number > 0 && pixels[row][col] == 0) {
                    val cx = boardLeft + col * cellSize + cellSize / 2
                    val cy = boardTop + row * cellSize + cellSize / 2 -
                            (numberPaint.descent() + numberPaint.ascent()) / 2
                    canvas.drawText(number.toString(), cx, cy, numberPaint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                handleTouch(event.x, event.y)
            }
        }
        return true
    }

    private fun handleTouch(x: Float, y: Float) {
        val col = ((x - boardLeft) / cellSize).toInt()
        val row = ((y - boardTop) / cellSize).toInt()

        if (row in 0 until gridSize && col in 0 until gridSize) {
            val expected = template[row][col]
            // Красим только те клетки, которые предназначены для раскраски
            if (expected > 0) {
                // Если цвет не меняется — не перерисовываем
                if (pixels[row][col] != currentColor) {
                    pixels[row][col] = currentColor
                    invalidate()
                }
            }
        }
    }
}
