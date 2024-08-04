package com.arrowwould.workout.man.reminder

interface ReminderInterface {

    fun editDays(Id: String, isEdit: Boolean, arrOfDaysArgs: ArrayList<String>)

    fun editTime(Id: String, isEdit: Boolean, arrOfDaysArgs: ArrayList<String>,hour:Int,minute:Int)
}