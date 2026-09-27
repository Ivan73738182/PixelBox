package com.ivangames.pixelbox

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.abs

object ImageLoader {

    // Результат загрузки
    class LoadedTemplate(
        val grid: Array<IntArray>,       // номер цвета в каждой клетке (0 = не красить)
        val colors: List<Int>            // палитра: colors[0] = цвет №1, colors[1] = цвет №2, и т.д.
    )

    // Основная функция: читает PNG, квантует цвета, строит шаблон
    fun loadTemplate(context: Context, assetPath: String, maxColors: Int = 12): LoadedTemplate {
        val bitmap = context.assets.open(assetPath).use { BitmapFactory.decodeStream(it) }
        val width = bitmap.width
        val height = bitmap.height

        // Считаем все пиксели
        val rawPixels = Array(height) { IntArray(width) }
        for (y in 0 until height) {
            for (x in 0 until width) {
                val argb = bitmap.getPixel(x, y)
                rawPixels[y][x] = argb
            }
        }

        // Собираем уникальные цвета (без прозрачных)
        val uniqueColors = mutableSetOf<Int>()
        for (y in 0 until height) {
            for (x in 0 until width) {
                val c = rawPixels[y][x]
                val alpha = (c ushr 24) and 0xFF
                if (alpha > 128) { // игнорируем сильно прозрачные
                    uniqueColors.add(c)
                }
            }
        }

        // Квантуем: сводим к maxColors основным
        val palette = quantize(uniqueColors.toList(), maxColors)

        // Строим сетку с номерами (1..N), 0 = прозрачный / не красить
        val grid = Array(height) { IntArray(width) { 0 } }
        for (y in 0 until height) {
            for (x in 0 until width) {
                val c = rawPixels[y][x]
                val alpha = (c ushr 24) and 0xFF
                if (alpha > 128) {
                    val index = findClosestColorIndex(c, palette)
                    grid[y][x] = index + 1 // номер цвета (1-based)
                } else {
                    grid[y][x] = 0 // прозрачный — не красить
                }
            }
        }

        return LoadedTemplate(grid, palette)
    }

    // Квантование: свести много цветов к N основным
    private fun quantize(colors: List<Int>, maxColors: Int): List<Int> {
        if (colors.size <= maxColors) return colors

        // Простой алгоритм: усредняем похожие цвета
        val result = mutableListOf<Int>()
        val used = mutableListOf<Int>()

        for (color in colors.shuffled()) {
            var merged = false
            for (i in used.indices) {
                if (colorDistance(color, used[i]) < 3000) { // похожие
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

        // Если получилось меньше maxColors — ничего страшного, добиваем
        return result
    }

    // Найти индекс ближайшего цвета в палитре
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

    // Расстояние между двумя цветами (простая метрика)
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
