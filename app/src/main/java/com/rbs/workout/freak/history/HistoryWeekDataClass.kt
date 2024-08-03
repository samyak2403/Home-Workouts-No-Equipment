package com.rbs.workout.freak.history

import java.io.Serializable

class HistoryWeekDataClass : Serializable {

    var weekNumber = ""
    var weekStart = ""
    var weekEnd = ""
    var totTime = 0
    var totKcal = 0
    var totWorkout = 0

    var arrhistoryDetail: ArrayList<HistoryDetailsClass> = ArrayList()

}
