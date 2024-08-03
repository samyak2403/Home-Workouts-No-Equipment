package com.rbs.workout.freak.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.gson.Gson
import com.rbs.workout.freak.R
import com.rbs.workout.freak.activity.DaysStatusActivity
import com.rbs.workout.freak.activity.WorkoutListActivity
import com.rbs.workout.freak.bw.BuildWiderActivity
import com.rbs.workout.freak.pojo.PWorkOutCategory
import com.rbs.workout.freak.utils.CommonUtility
import com.rbs.workout.freak.utils.ConstantString
import java.io.Serializable

class WorkoutCategoryAdapter(internal val mContext: Context, internal val arrWorkoutCategoryData: ArrayList<PWorkOutCategory>) : androidx.recyclerview.widget.RecyclerView.Adapter<WorkoutCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val convertView = LayoutInflater.from(mContext).inflate(R.layout.workout_row, parent, false)
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = getItem(pos)

        Log.e("TAG", "onBindViewHolder:All Data:::  "+ Gson().toJson(item) )
        holder.imgWorkoutDeificulty.visibility = View.VISIBLE

        when {
            item.catDefficultyLevel == ConstantString.biginner -> holder.imgWorkoutDeificulty.setImageResource(R.drawable.ic_beginner_level)
            item.catDefficultyLevel == ConstantString.intermediate -> holder.imgWorkoutDeificulty.setImageResource(R.drawable.ic_intermediate_level)
            item.catDefficultyLevel == ConstantString.advance -> holder.imgWorkoutDeificulty.setImageResource(R.drawable.ic_advanced_level)
            else -> holder.imgWorkoutDeificulty.visibility = View.GONE
        }

        when {
            item.catDefficultyLevel == ConstantString.main -> {
                holder.rltWorkOutTitle.visibility = View.VISIBLE
                holder.RltWorkOutDetails.visibility = View.GONE
                holder.txtWorkoutTitle.text = item.catName
                holder.rltProgress.visibility = View.VISIBLE

                holder.rltBuildWider.visibility = View.GONE
            }
            item.catDefficultyLevel == ConstantString.Build_wider -> {
                holder.rltWorkOutTitle.visibility = View.GONE
                holder.RltWorkOutDetails.visibility = View.GONE
                holder.rltBuildWider.visibility = View.VISIBLE

            }
            else -> {
                holder.rltWorkOutTitle.visibility = View.GONE
                holder.RltWorkOutDetails.visibility = View.VISIBLE
                holder.txtWorkoutCategoryTitle.text = item.catName
                holder.txtWorkoutDetails.text = item.catSubCategory

                if (item.catDetailsBg == 0) {
                    holder.txtWorkoutDetails.background = null
                } else {
                    Log.e("TAG", "onBindViewHolder:::: item.catDetailsBg  ${item.catDetailsBg}" )
                    if (item.catDetailsBg == ConstantString.beginnerColor){
                        holder.txtWorkoutDetails.setBackgroundResource(R.drawable.bg_beginner_color)
                    }else if (item.catDetailsBg == ConstantString.intermediateColor){
                        holder.txtWorkoutDetails.setBackgroundResource(R.drawable.bg_intermediate_color)
                    }else if (item.catDetailsBg == ConstantString.advanceColor){
                        holder.txtWorkoutDetails.setBackgroundResource(R.drawable.bg_advance_color)
                    }
//                    holder.txtWorkoutDetails.setBackgroundResource(item.catDetailsBg)
                }

                if (item.catImage != 0) {
                    holder.imgWorkoutRow.setImageResource(item.catImage)
                }

                holder.rltBuildWider.visibility = View.GONE

            }
        }

        when {
            item.catName == ConstantString.Full_Body -> {
                holder.rltProgress.visibility = View.VISIBLE
                CommonUtility.setDayProgressData(mContext, item.catName, holder.txtDayLeft, holder.txtDayPer, holder.pbDay)
            }
            item.catName == ConstantString.Lower_Body -> {
                holder.rltProgress.visibility = View.VISIBLE
                CommonUtility.setDayProgressData(mContext, item.catName, holder.txtDayLeft, holder.txtDayPer, holder.pbDay)
            }
            else -> {
                holder.rltProgress.visibility = View.GONE
            }
        }
    }

    fun getItem(pos: Int): PWorkOutCategory {
        val item: PWorkOutCategory = arrWorkoutCategoryData[pos]
        return item
    }

    override fun getItemCount(): Int {
        return arrWorkoutCategoryData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val rltWorkOutTitle: RelativeLayout = itemView.findViewById(R.id.rltWorkOutTitle)
        val RltWorkOutDetails: CardView = itemView.findViewById(R.id.RltWorkOutDetails)
        val rltProgress: RelativeLayout = itemView.findViewById(R.id.rltProgress)
        val rltBuildWider: CardView = itemView.findViewById(R.id.rltBuildWider)

        val txtWorkoutTitle: TextView = itemView.findViewById(R.id.txtWorkoutTitle)
        val txtWorkoutDetails: TextView = itemView.findViewById(R.id.txtWorkoutDetails)
        val txtWorkoutCategoryTitle: TextView = itemView.findViewById(R.id.txtWorkoutCategoryTitle)
        val txtDayLeft: TextView = itemView.findViewById(R.id.txtDayLeft)
        val txtDayPer: TextView = itemView.findViewById(R.id.txtDayPer)

        val imgWorkoutRow: ImageView = itemView.findViewById(R.id.imgWorkoutRow)
        val imgWorkoutDeificulty: ImageView = itemView.findViewById(R.id.imgWorkoutDeificulty)

        val pbDay: ProgressBar = itemView.findViewById(R.id.pbDay)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val item = getItem(adapterPosition)
            if (item.catDefficultyLevel != ConstantString.main) {
                if (item.catName == ConstantString.Full_Body || item.catName == ConstantString.Lower_Body) {
                    val intent = Intent(mContext, DaysStatusActivity::class.java)
                    intent.putExtra(ConstantString.key_workout_category_item, item as Serializable)
                    intent.putExtra(ConstantString.key_category_name, item.catName)
                    mContext.startActivity(intent)
                } else if (item.catName == ConstantString.Build_wider) {
                    val intent = Intent(mContext, BuildWiderActivity::class.java)
                    intent.putExtra(ConstantString.key_workout_category_item, item as Serializable)
                    mContext.startActivity(intent)
                } else {
                    val intent = Intent(mContext, WorkoutListActivity::class.java)
                    intent.putExtra(ConstantString.key_workout_category_item, item as Serializable)
                    mContext.startActivity(intent)
                }
            }
        }
    }

}
