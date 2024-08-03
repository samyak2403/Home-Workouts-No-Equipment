package com.rbs.workout.freak.report

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rbs.workout.freak.R
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.utils.CommonUtility
import java.util.*

class WeekDayReportAdapter(val mContext: Context, val isFromHome: Boolean = false) : RecyclerView.Adapter<WeekDayReportAdapter.ViewHolder>() {

    private val arrCurrentWeek = CommonUtility.getCurrentWeekByFirstDay(mContext)
    val dbHelper = DataHelper(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val convertView = LayoutInflater.from(mContext).inflate(R.layout.row_week_day, parent, false)
        convertView.layoutParams.width = parent.measuredWidth / 7
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
//        holder.txtDayName.text = CommonUtility.getDayName(pos)

        holder.txtDayDate.text = CommonUtility.convertLongToDay(arrCurrentWeek[pos])

        val dtWeek = CommonUtility.getStringToDate(arrCurrentWeek[pos])

        val calToday = Calendar.getInstance()
        calToday.time = dtWeek
        holder.txtDayName.text = CommonUtility.convertDate(calToday.timeInMillis, "E").substring(0, 1)
        if (CommonUtility.convertDate(Calendar.getInstance().timeInMillis, "dd").equals(CommonUtility.convertLongToDay(arrCurrentWeek[pos]))) {
            holder.txtDayDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorButton))
        }

        if (dbHelper.isHistoryAvailable(CommonUtility.convertFullDateToDate(arrCurrentWeek[pos]))) {
            holder.imgIsWorkoutDay.setImageResource(R.drawable.ic_cal_round_done)
        } else if (Calendar.getInstance().time.after(dtWeek)) {
            holder.imgIsWorkoutDay.setImageResource(R.drawable.ic_cal_round_fill)
        } else {
            holder.imgIsWorkoutDay.setImageResource(R.drawable.ic_cal_round)
        }

    }

    override fun getItemCount(): Int {
        return arrCurrentWeek.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val txtDayName: TextView = itemView.findViewById(R.id.txtDayName)
        val txtDayDate: TextView = itemView.findViewById(R.id.txtDayDate)
        val imgIsWorkoutDay: ImageView = itemView.findViewById(R.id.imgIsWorkoutDay)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            /*val intent = Intent(mContext, HistoryActivity::class.java)
            mContext.startActivity(intent)
            if (!isFromHome) {
                (mContext as AppCompatActivity).finish()
            }*/
        }

    }

}
