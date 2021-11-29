package com.app.travel.flare

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IncidentReportTypeAdapter(private val genreList : List<IncidentType>) :
    RecyclerView.Adapter<IncidentReportTypeAdapter.IncidentTypeHolder>(){

    class IncidentTypeHolder(private val view: View): RecyclerView.ViewHolder(view){
        private val imageView : ImageView = view.findViewById(R.id.list_image_view)
        private val textView : TextView = view.findViewById(R.id.incident_grid_type)
        fun bind(oneItem: IncidentType) {
            imageView.setImageResource(oneItem.resId)
            textView.text = oneItem.type

            val context = view.context
            view.setOnClickListener {

                val intent = Intent(context, IncidentReportActivity::class.java).apply {
                    putExtra(SELECTED_INCIDENT_TYPE, oneItem.type)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): IncidentTypeHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.incident_type_grid_item, viewGroup, false)

        return IncidentTypeHolder(view)
    }

    override fun getItemCount(): Int {
        return genreList.size
    }

    override fun onBindViewHolder(holder: IncidentTypeHolder, position: Int) {
        holder.bind(genreList[position])
    }
}