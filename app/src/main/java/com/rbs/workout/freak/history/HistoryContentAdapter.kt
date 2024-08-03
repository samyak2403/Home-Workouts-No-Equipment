package com.rbs.workout.freak.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.rbs.workout.freak.R
import com.rbs.workout.freak.utils.CommonUtility
import com.rbs.workout.freak.utils.ConstantString

class HistoryContentAdapter(internal val mContext: Context, internal val arrWorkoutCategoryData: ArrayList<HistoryDetailsClass>) : androidx.recyclerview.widget.RecyclerView.Adapter<HistoryContentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val convertView = LayoutInflater.from(mContext).inflate(R.layout.row_history, parent, false)
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = getItem(pos)

        setPlanWorkoutImage(item.PlanName, holder.imgCategory)

        if (item.PlanName == ConstantString.Full_Body || item.PlanName == ConstantString.Lower_Body) {
            holder.txtWorkoutName.text = item.PlanName.plus(" Day ").plus(item.DayName)
        } else {
            holder.txtWorkoutName.text = item.PlanName.plus(" ").plus(item.LevelName)
        }

//        holder.txtWeekDate.text = CommonUtility.convertDateToDateMonthName(item.weekStart).plus(" - ").plus(CommonUtility.convertDateToDateMonthName(item.weekEnd))
        holder.txtContentDateTime.text = CommonUtility.convertDateStrToInputFormat(item.DateTime, "MMM dd, HH:mm a")      //item.totWorkout.toString().plus(" workouts")
//        holder.txtWorkoutName.text = item.PlanName.plus(" ").plus(item.LevelName).plus(" ").plus(item.DayName)     //item.totWorkout.toString().plus(" workouts")
        holder.txtContentTotalTime.text = CommonUtility.secToTime(item.CompletionTime.toInt())
        holder.txtContentTotalBurnCalories.text = item.BurnKcal.plus(" KCal")    //CommonUtility.secToTime(item.totTime)

        if((arrWorkoutCategoryData.size - 1) == pos){
            holder.btmContentLine.visibility = View.GONE
        } else{
            holder.btmContentLine.visibility = View.VISIBLE
        }

    }

    fun getItem(pos: Int): HistoryDetailsClass {
        val item: HistoryDetailsClass = arrWorkoutCategoryData[pos]
        return item
    }

    override fun getItemCount(): Int {
        return arrWorkoutCategoryData.size
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val rltHeader: LinearLayout = itemView.findViewById(R.id.rltHeader)
        val rltContent: LinearLayout = itemView.findViewById(R.id.rltContent)

        val txtContentDateTime: TextView = itemView.findViewById(R.id.txtContentDateTime)
        val txtWorkoutName: TextView = itemView.findViewById(R.id.txtWorkoutName)
        val txtContentTotalTime: TextView = itemView.findViewById(R.id.txtContentTotalTime)
        val txtContentTotalBurnCalories: TextView = itemView.findViewById(R.id.txtContentTotalBurnCalories)
        val imgCategory: ImageView = itemView.findViewById(R.id.imgCategory)
        val btmContentLine: View = itemView.findViewById(R.id.btmContentLine)

//        val rcyHistoryDetails: RecyclerView = itemView.findViewById(R.id.rcyHistoryDetails)

        init {
            rltHeader.visibility = View.GONE
            rltContent.visibility = View.VISIBLE

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

        }

    }

    fun setPlanWorkoutImage(PlanName: String, imgCategory: ImageView) {

        when (PlanName) {
            ConstantString.Build_wider -> imgCategory.setImageResource(R.drawable.ic_history_buil_wider_shoulders)
            ConstantString.Full_Body -> imgCategory.setImageResource(R.drawable.ic_history_full_body)
            ConstantString.Lower_Body -> imgCategory.setImageResource(R.drawable.ic_history_lower_body)
            ConstantString.Chest -> imgCategory.setImageResource(R.drawable.ic_history_chest)
            ConstantString.Abs -> imgCategory.setImageResource(R.drawable.ic_history_abs)
            ConstantString.Arm -> imgCategory.setImageResource(R.drawable.ic_history_arm)
            ConstantString.Leg -> imgCategory.setImageResource(R.drawable.ic_history_leg)
            ConstantString.Shoulder_and_Back -> imgCategory.setImageResource(R.drawable.ic_history_shoulder)
        }

    }

}
