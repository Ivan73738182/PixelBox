package com.ivangames.pixelbox

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import kotlin.math.abs

object ImageLoader {

    private const val TAG = "ImageLoader"

    class LoadedTemplate(
        val grid: Array<IntArray>,
        val colors: List<Int>
    )

    fun loadTemplate(context: Context, assetPath: String, maxColors: Int = 12): LoadedTemplate {
        Log.d(TAG, "=== Старт загрузки: $assetPath ===")
        try {
            val inputStream = context.assets.open(assetPath)
            Log.d(TAG, "Файл открыт")
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap == null) {
                Log.e(TAG, "Bitmap == null")
                return fallback()
            }

            Log.d(TAG, "Bitmap: ${bitmap.width}×${bitmap.height}")

            val width = bitmap.width
            val height = bitmap.height

            val rawPixels = Array(height) { IntArray(width) }
            for (y in 0 until height) {
                for (x in 0 until width) {
                    rawPixels[y][x] = bitmap.getPixel(x, y)
                }
            }
            Log.d(TAG, "Пиксели считаны")

            val uniqueColors = mutableSetOf<Int>()
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val c = rawPixels[y][x]
                    val alpha = (c ushr 24) and 0xFF
                    if (alpha > 128) uniqueColors.add(c)
                }
            }
            Log.d(TAG, "Уникальных цветов: ${uniqueColors.size}")

            val palette = quantize(uniqueColors.toList(), maxColors)
            Log.d(TAG, "После квантования: ${palette.size}")

            val grid = Array(height) { IntArray(width) { 0 } }
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val c = rawPixels[y][x]
                    val alpha = (c ushr 24) and 0xFF
                    if (alpha > 128) {
                        grid[y][x] = findClosestColorIndex(c, palette) + 1
                    }
                }
            }
            Log.d(TAG, "=== Готово: ${grid.size}×${grid[0].size}, цветов ${palette.size} ===")

            return LoadedTemplate(grid, palette)
        } catch (e: Exception) {
            Log.e(TAG, "ОШИБКА: ${e.message}", e)
            return fallback()
        }
    }

    private fun fallback(): LoadedTemplate {
        Log.d(TAG, "Использую fallback (16×16 красный)")
        val grid = Array(16) { IntArray(16) { 1 } }
        return LoadedTemplate(grid, listOf(Color.RED))
    }

    private fun quantize(colors: List<Int>, maxColors: Int): List<Int> {
        if (colors.size <= maxColors) return colors

        val result = mutableListOf<Int>()
        val used = mutableListOf<Int>()

        for (color in colors.sortedByDescending { Color.red(it) + Color.green(it) + Color.blue(it) }) {
            var merged = false
            for (u in used) {
                if (colorDistance(color, u) < 3000) {
                    merged = true
                    break
                }
            }
            if (!merged) {
                used.add(color)
                result.add(color)
                if (result.size >= maxColors) break
            }
        }
        return result
    }

    private fun findClosestColorIndex(color: Int, palette: List<Int>): Int {
        var bestIndex = 0
        var bestDist = Int.MAX_VALUE
        for (i in palette.indices) {
            val d = colorDistance(color, palette[i])
            if (d < bestDist) {
                bestDist = d
                bestIndex = i
            }
        }
        return bestIndex
    }

    private fun colorDistance(c1: Int, c2: Int): Int {
        val r1 = (c1 shr 16) and 0xFF
        val g1 = (c1 shr 8) and 0xFF
        val b1 = c1 and 0xFF
        val r2 = (c2 shr 16) and 0xFF
        val g2 = (c2 shr 8) and 0xFF
        val b2 = c2 and 0xFF
        return abs(r1 - r2) + abs(g1 - g2) + abs(b1 - b2)
    }
}
