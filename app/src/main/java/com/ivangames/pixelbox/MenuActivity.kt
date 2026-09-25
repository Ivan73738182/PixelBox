package com.ivangames.pixelbox

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val templates = listOf(
            Template("Сердечко", "heart"),
            Template("Смайлик", "smile"),
            Template("Котик", "cat"),
            Template("Домик", "house")
        )

        val recycler = findViewById<RecyclerView>(R.id.templatesList)
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = TemplateAdapter(templates) { template ->
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("template", template.id)
            startActivity(intent)
        }
    }
}

data class Template(val name: String, val id: String)
