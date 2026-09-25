package com.ivangames.pixelbox

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
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
        strokeWidth = 1.5f
    }
    private val numberPaint = Paint().apply {
        color = Color.parseColor("#888888")
        textSize = 24f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val matrix = Matrix()
    private var scaleFactor = 1f
    private var translateX = 0f
    private var translateY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isDragging = false

    private val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(0.5f, 8f)
            invalidate()
            return true
        }
    })

    private fun rebuildArrays() {
        template = Array(gridSize) { IntArray(gridSize) { 0 } }
        pixels = Array(gridSize) { IntArray(gridSize) { Color.WHITE } }
    }

    fun loadTemplate(newTemplate: Array<IntArray>) {
        gridSize = newTemplate.size
        template = newTemplate
        pixels = Array(gridSize) { IntArray(gridSize) { Color.WHITE } }
        scaleFactor = 1f
        translateX = 0f
        translateY = 0f
        post { fitToScreen() }
        invalidate()
    }

    private fun fitToScreen() {
        if (width == 0 || height == 0) return
        val size = minOf(width, height).toFloat()
        val cellSize = size / gridSize
        val totalSize = cellSize * gridSize
        scaleFactor = 1f
        translateX = (width - totalSize) / 2f
        translateY = (height - totalSize) / 2f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Белый фон для всего поля
canvas.drawColor(Color.WHITE)

canvas.save()
matrix.reset()
        canvas.save()
        matrix.reset()
        matrix.postScale(scaleFactor, scaleFactor, width / 2f, height / 2f)
        matrix.postTranslate(translateX, translateY)
        canvas.concat(matrix)

        val cellW = width.toFloat() / gridSize
        val cellH = height.toFloat() / gridSize

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

        for (i in 0..gridSize) {
            canvas.drawLine(i * cellW, 0f, i * cellW, height.toFloat(), gridPaint)
            canvas.drawLine(0f, i * cellH, width.toFloat(), i * cellH, gridPaint)
        }

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

        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 1 && !scaleDetector.isInProgress) {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY
                    isDragging = true
                    translateX += dx
                    translateY += dy
                    lastTouchX = event.x
                    lastTouchY = event.y
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isDragging && !scaleDetector.isInProgress) {
                    handleTap(event.x, event.y)
                }
                isDragging = false
            }
        }
        return true
    }

    private fun handleTap(x: Float, y: Float) {
        val inv = Matrix()
        matrix.invert(inv)
        val pts = floatArrayOf(x, y)
        inv.mapPoints(pts)

        val cellW = width.toFloat() / gridSize
        val cellH = height.toFloat() / gridSize
        val col = (pts[0] / cellW).toInt()
        val row = (pts[1] / cellH).toInt()

        if (row in 0 until gridSize && col in 0 until gridSize) {
            val expected = template[row][col]
            if (expected > 0 && expected == currentColorNumber) {
                pixels[row][col] = currentColor
                invalidate()
            }
        }
    }
}
