package com.ivangames.pixelbox

object TemplateData {

    // Шаблон "Сердечко" 16x16. 0 = пусто (не красить), 1..N = номер цвета
    fun heart(): Array<IntArray> {
        // 0 - пусто, 1 - красный
        return arrayOf(
            intArrayOf(0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,0),
            intArrayOf(0,1,1,1,1,0,0,0,0,1,1,1,1,0,0,0),
            intArrayOf(1,1,1,1,1,1,0,0,1,1,1,1,1,1,0,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0),
            intArrayOf(0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0),
            intArrayOf(0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0),
            intArrayOf(0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0),
            intArrayOf(0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
            intArrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)
        )
    }

    fun getTemplate(id: String): Array<IntArray> {
        return when (id) {
            "heart" -> heart()
            else -> heart() // пока все шаблоны = сердечко
        }
    }
}
