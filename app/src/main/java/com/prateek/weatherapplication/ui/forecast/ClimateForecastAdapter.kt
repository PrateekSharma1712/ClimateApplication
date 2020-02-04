package com.prateek.weatherapplication.ui.forecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prateek.weatherapplication.R
import com.prateek.weatherapplication.domain.model.Climate
import kotlinx.android.synthetic.main.line_item_climate.view.*
import kotlinx.android.synthetic.main.line_item_date_time.view.*

class ClimateForecastAdapter(private val data: MutableList<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(dateText: String) {
            itemView.dateTimeTextView.text = dateText
        }
    }

    inner class ClimateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(climate: Climate) {
            itemView.timeTextView.text = climate.weatherDescription + climate.dateTime
        }
    }

    fun updateData(newData: List<Any>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == DATE_VIEW_TYPE) {
            return DateViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.line_item_date_time,
                    parent,
                    false
                )
            )
        } else {
            return ClimateViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.line_item_climate,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (data[position] is Climate) CLIMATE_VIEW_TYPE else DATE_VIEW_TYPE

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DateViewHolder) {
            holder.bind(data[position] as String)
        } else {
            (holder as ClimateViewHolder).bind(data[position] as Climate)
        }
    }

    companion object {
        const val DATE_VIEW_TYPE = 1
        const val CLIMATE_VIEW_TYPE = 2
    }
}