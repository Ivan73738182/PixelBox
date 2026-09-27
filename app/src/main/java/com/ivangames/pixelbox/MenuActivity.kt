package com.ivangames.pixelbox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Список картинок из папки assets/templates/
        val templates = listTemplates()

        val recycler = findViewById<RecyclerView>(R.id.templatesList)
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = TemplateAdapter(templates) { template ->
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("template", template.assetPath)
            startActivity(intent)
        }
    }

    // Список картинок в assets/templates/
    private fun listTemplates(): List<Template> {
        val list = mutableListOf<Template>()
        try {
            val files = assets.list("templates") ?: emptyArray()
            for (file in files) {
                if (file.endsWith(".png", ignoreCase = true)) {
                    val name = file.removeSuffix(".png").replaceFirstChar { it.uppercase() }
                    list.add(Template(name, "templates/$file"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}

data class Template(val name: String, val assetPath: String)
