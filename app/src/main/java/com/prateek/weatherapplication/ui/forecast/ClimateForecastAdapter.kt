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
import kotlin.math.roundToInt

class ClimateForecastAdapter(private val data: List<Any>, private val isMultipleCityAdapter: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(headerText: String) {
            if (isMultipleCityAdapter) {
                itemView.timeLabelTextView.text = headerText
            } else {
                itemView.dateTimeTextView.text = headerText
            }
        }
    }

    inner class ClimateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(climate: Climate) {
            val iconURL = WEATHER_ICON_URL + climate.icon + "@2x.png"
            Glide.with(itemView).load(iconURL).into(itemView.weatherImageView)
            itemView.timeTextView.text = if (isMultipleCityAdapter) climate.city else climate.dateText?.extractHourMinute()
            itemView.weatherDescriptionTextView.text = climate.weatherDescription
            itemView.minTemperatureTextView.text = climate.minTemperature?.roundToInt()?.toString()
            itemView.maxTemperatureTextView.text = climate.maxTemperature?.roundToInt()?.toString()
            itemView.windSpeedTextView.text = climate.windSpeed?.roundToInt()?.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == DATE_VIEW_TYPE) {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.line_item_date_time,
                parent,
                false
            )

            if (isMultipleCityAdapter) {
                view.dateTimeTextView.visibility = View.GONE
                view.horizontalDivider.visibility = View.GONE
            }

            return HeaderViewHolder(
                view
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
        if (holder is HeaderViewHolder) {
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