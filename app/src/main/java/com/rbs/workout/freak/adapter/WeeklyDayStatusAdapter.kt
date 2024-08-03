package com.rbs.workout.freak.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.rbs.workout.freak.R
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.pojo.pWeeklyDayData

class WeeklyDayStatusAdapter(val context: Context, val arrWeeklyDayStatus: ArrayList<pWeeklyDayData>) : RecyclerView.Adapter<WeeklyDayStatusAdapter.ViewHolder>() {

    var dbHelper = DataHelper(context)
    var boolFlagWeekComplete = false
    override fun getItemCount(): Int {
        return arrWeeklyDayStatus.size
//        return 1
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val convertView = LayoutInflater.from(context).inflate(R.layout.row_day_status, viewGroup, false)
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {

        if (!boolFlagWeekComplete && arrWeeklyDayStatus[pos].Is_completed == "0") {
            holder.weekIcon.setImageResource(R.drawable.ic_week_light_done)
            holder.vWeekProgressLine.setBackgroundColor(ContextCompat.getColor(context, R.color.colorButton))

            boolFlagWeekComplete = true
            holder.tvWeekProgressTxt.visibility = View.VISIBLE

            var count = 0
            for (i in 0 until arrWeeklyDayStatus[pos].arrWeekDayData.size) {
                if (arrWeeklyDayStatus[pos].arrWeekDayData[i].Is_completed == "1") {
                    count++
                }
            }

            holder.tvWeekProgressTxt.text = HtmlCompat.fromHtml("<font color='${ContextCompat.getColor(context,R.color.colorWhite)}'>$count</font>/7", HtmlCompat.FROM_HTML_MODE_LEGACY)

        } else if (arrWeeklyDayStatus[pos].Is_completed == "1") {
            holder.weekIcon.setImageResource(R.drawable.ic_week_done_small)
            holder.vWeekProgressLine.setBackgroundColor(ContextCompat.getColor(context, R.color.colorButton))
        } else {
            holder.weekIcon.setImageResource(R.drawable.ic_week_light)
        }

        if (arrWeeklyDayStatus[pos].Week_name == "04") {
            holder.vWeekProgressLine.visibility = View.INVISIBLE
        } else {
            holder.vWeekProgressLine.visibility = View.VISIBLE
        }

        holder.tvCurWeektxt.text = "WEEK ".plus(arrWeeklyDayStatus[pos].Week_name.replace("0", ""))

        holder.gvDayItem.adapter = WeekDayAdapter(context, arrWeeklyDayStatus[pos].arrWeekDayData, arrWeeklyDayStatus, pos)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        //val days_layout: LinearLayout = itemView.findViewById(R.id.days_layout)
        val weekIcon: ImageView = itemView.findViewById(R.id.weekIcon)

        val tvCurWeektxt: TextView = itemView.findViewById(R.id.tvCurWeektxt)
        val tvWeekProgressTxt: TextView = itemView.findViewById(R.id.tvWeekProgressTxt)
        val vWeekProgressLine: View = itemView.findViewById(R.id.vWeekProgressLine)

        val gvDayItem: RecyclerView = itemView.findViewById(R.id.gvDayItem)

        init {
            gvDayItem.layoutManager = GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)
//            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

        }
    }

}
