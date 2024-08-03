package com.rbs.workout.freak.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rbs.workout.freak.R

class CompletedDayStatusAdapter(val context: Context, val NumDayName: Int) : RecyclerView.Adapter<CompletedDayStatusAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val convertView = LayoutInflater.from(context).inflate(R.layout.row_completed_week_day, viewGroup, false)
        convertView.layoutParams.width = viewGroup.measuredWidth / 8
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (NumDayName == position && position == 7) {

            holder.imgCompleteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_week_winner_cup))
            holder.imgIndicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_completed_line_red))

            holder.imgCompleteImage.visibility = View.VISIBLE
            holder.tvDay.visibility = View.GONE
            holder.imgIndicator.visibility = View.INVISIBLE

        } else if (position == 7) {

            holder.imgCompleteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_week_winner_cup_gray))

            holder.imgCompleteImage.visibility = View.VISIBLE
            holder.tvDay.visibility = View.GONE
            holder.imgIndicator.visibility = View.INVISIBLE

        } else {
            if (NumDayName < (position + 1)) {
                holder.tvDay.text = "".plus(position + 1)
                holder.imgIndicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_completed_line_gray))

                holder.imgCompleteImage.visibility = View.GONE
                holder.tvDay.visibility = View.VISIBLE

            } else {

                if (NumDayName == (position+1) && position != 6) {
                    holder.imgIndicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_completed_line_gray))
                } else {
                    holder.imgIndicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_completed_line_red))
                }

                holder.imgCompleteImage.visibility = View.VISIBLE
                holder.tvDay.visibility = View.GONE

            }

        }
    }

    override fun getItemCount(): Int {
        return 8
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        val imgIndicator: ImageView = itemView.findViewById(R.id.imgIndicator)
        val imgCompleteImage: ImageView = itemView.findViewById(R.id.imgCompleteImage)

        override fun onClick(v: View?) {

        }

    }
}
