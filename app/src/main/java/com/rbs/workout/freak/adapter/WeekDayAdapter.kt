package com.rbs.workout.freak.adapter

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.rbs.workout.freak.R
import com.rbs.workout.freak.activity.WorkoutListActivity
import com.rbs.workout.freak.pojo.PWorkOutCategory
import com.rbs.workout.freak.pojo.pWeekDayData
import com.rbs.workout.freak.pojo.pWeeklyDayData
import com.rbs.workout.freak.utils.ConstantString
import java.io.Serializable

class WeekDayAdapter(val context: Context, val arrDayData: ArrayList<pWeekDayData>, val arrWeeklyDayStatus: ArrayList<pWeeklyDayData>, val pos1: Int) : RecyclerView.Adapter<WeekDayAdapter.ViewHolder>() {

    var flagPrevDay = true
    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val convertView = LayoutInflater.from(context).inflate(R.layout.row_day, viewGroup, false)
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {

        if (arrDayData[pos].Day_name == "Cup") {
            holder.tvDay.tag = "n"

            if (arrDayData[pos].Is_completed == "1") {
                holder.imgCompleteImage.setImageResource(R.drawable.ic_week_winner_cup)
            } else {
                holder.imgCompleteImage.setImageResource(R.drawable.ic_week_winner_cup_gray)
            }

            holder.imgCompleteImage.visibility = View.VISIBLE
            holder.tvDay.visibility = View.GONE
            holder.imgIndicator.visibility = View.GONE

        } else {
            holder.tvDay.tag = "y"

            holder.imgCompleteImage.setImageResource(R.drawable.ic_week_done_big)

            if (flagPrevDay && pos1 != 0) {
                flagPrevDay = arrWeeklyDayStatus[pos1 - 1].Is_completed == "1"
            }

            if (arrWeeklyDayStatus[pos1].Is_completed == "1") {
                holder.imgIndicator.setImageResource(R.drawable.ic_week_arrow_next_red)
                holder.imgCompleteImage.visibility = View.VISIBLE

                holder.tvDay.visibility = View.GONE

            } else {
                if (arrDayData[pos].Is_completed == "1") {
                    holder.imgIndicator.setImageResource(R.drawable.ic_week_arrow_next_red)
                    holder.imgCompleteImage.visibility = View.VISIBLE

                    holder.tvDay.visibility = View.GONE

                    flagPrevDay = true
                } else if (flagPrevDay) {
                    holder.imgIndicator.setImageResource(R.drawable.ic_week_arrow_next_gray)
                    holder.imgCompleteImage.visibility = View.GONE

                    holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.colorButton))
                    holder.tvDay.setBackgroundResource(R.drawable.ic_week_round_dot)
                    holder.tvDay.visibility = View.VISIBLE

                    flagPrevDay = false
                } else {
                    holder.imgIndicator.setImageResource(R.drawable.ic_week_arrow_next_gray)
                    holder.imgCompleteImage.visibility = View.GONE

                    holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.colorGray))
                    holder.tvDay.setBackgroundResource(R.drawable.ic_week_round_line)
                    holder.tvDay.visibility = View.VISIBLE
                    holder.tvDay.tag = "n"

                    flagPrevDay = false
                }
            }

            if (pos == 3) {
                holder.imgIndicator.visibility = View.GONE
            } else {
                holder.imgIndicator.visibility = View.VISIBLE
            }

            holder.tvDay.text = arrDayData[pos].Day_name.replace("0", "")
        }
    }

    override fun getItemCount(): Int {
        return arrDayData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        val imgIndicator: ImageView = itemView.findViewById(R.id.imgIndicator)
        val imgCompleteImage: ImageView = itemView.findViewById(R.id.imgCompleteImage)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (arrDayData[adapterPosition].Day_name != "Cup") {
                if (tvDay.tag == "y") {
                    val workoutCategoryDetails = PWorkOutCategory()
                    if(arrWeeklyDayStatus[pos1].categoryName == ConstantString.Full_Body){
                        workoutCategoryDetails.catDefficultyLevel = ConstantString.full_body
                        workoutCategoryDetails.catName = ConstantString.Full_Body
                        workoutCategoryDetails.catSubCategory = "7 X 4 Challenge"
                        workoutCategoryDetails.catDetailsBg = 0
                        workoutCategoryDetails.catTypeImage = 0
                        workoutCategoryDetails.catImage = R.drawable.full_body
                        workoutCategoryDetails.catTableName = ConstantString.tbl_full_body_workouts_list

                        workoutCategoryDetails.dayName = arrDayData[adapterPosition].Day_name
                        workoutCategoryDetails.weekName = arrWeeklyDayStatus[pos1].Week_name
                    } else if(arrWeeklyDayStatus[pos1].categoryName == ConstantString.Lower_Body){
                        workoutCategoryDetails.catDefficultyLevel = ConstantString.full_body
                        workoutCategoryDetails.catName = ConstantString.Lower_Body
                        workoutCategoryDetails.catSubCategory = "7 X 4 Challenge"
                        workoutCategoryDetails.catDetailsBg = 0
                        workoutCategoryDetails.catTypeImage = 0
                        workoutCategoryDetails.catImage = R.drawable.lower_body
                        workoutCategoryDetails.catTableName = ConstantString.tbl_lower_body_list

                        workoutCategoryDetails.dayName = arrDayData[adapterPosition].Day_name
                        workoutCategoryDetails.weekName = arrWeeklyDayStatus[pos1].Week_name
                    }

                    val intent = Intent(context, WorkoutListActivity::class.java)
                    intent.putExtra(ConstantString.key_workout_category_item, workoutCategoryDetails as Serializable)
                    intent.putExtra(ConstantString.key_day_name, arrDayData[adapterPosition].Day_name)
                    intent.putExtra(ConstantString.key_week_name, arrWeeklyDayStatus[pos1].Week_name)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Please finish the previous challenge date first.", Toast.LENGTH_SHORT).show()
                }
            }

//            val dbHelper = DataHelper(context)
//            val workOutDetailData = dbHelper.getWeekDayExerciseData(arrDayData[adapterPosition].Day_name,arrWeeklyDayStatus[pos1].Week_name)

//            val intent = Intent(context, WorkoutActivity::class.java)
//            intent.putExtra(ConstantString.workout_list, workOutDetailData)
//            context.startActivity(intent)

        }
    }

}
