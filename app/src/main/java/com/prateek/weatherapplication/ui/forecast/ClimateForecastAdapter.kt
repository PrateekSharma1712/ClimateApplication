package com.prateek.weatherapplication.ui.forecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prateek.weatherapplication.R
import com.prateek.weatherapplication.domain.model.Climate
import com.prateek.weatherapplication.extractHourMinute
import com.prateek.weatherapplication.framework.network.services.WEATHER_ICON_URL
import kotlinx.android.synthetic.main.line_item_climate.view.*
import kotlinx.android.synthetic.main.line_item_date_time.view.*

class ClimateForecastAdapter(private val data: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(dateText: String) {
            itemView.dateTimeTextView.text = dateText
        }
    }

    inner class ClimateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(climate: Climate) {
            val iconURL = WEATHER_ICON_URL + climate.icon + "@2x.png"
            Glide.with(itemView).load(iconURL).into(itemView.weatherImageView)
            itemView.weatherDescription.text = climate.weatherDescription
            itemView.minTemperature.text = climate.minTemperature?.toString()
            itemView.maxTemperature.text = climate.maxTemperature?.toString()
            itemView.timeTextView.text = climate.dateText?.extractHourMinute()
        }
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