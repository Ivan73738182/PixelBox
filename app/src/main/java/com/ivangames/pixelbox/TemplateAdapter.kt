package com.ivangames.pixelbox

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TemplateAdapter(
    private val templates: List<Template>,
    private val onClick: (Template) -> Unit
) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {

    class TemplateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.templateCard)
        val image: ImageView = view.findViewById(R.id.templateImage)
        val title: TextView = view.findViewById(R.id.templateTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_template, parent, false)
        return TemplateViewHolder(view)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val template = templates[position]
        holder.title.text = template.name

        // Грузим превью из assets
        try {
            val inputStream = holder.itemView.context.assets.open(template.assetPath)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            holder.image.setImageBitmap(bitmap)
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.card.setOnClickListener { onClick(template) }
    }

    override fun getItemCount(): Int = templates.size
}
