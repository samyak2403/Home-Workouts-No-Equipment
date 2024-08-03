package com.rbs.workout.freak.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.rbs.workout.freak.utils.LocalDB
import java.util.*

class DatePickerFragment : androidx.fragment.app.DialogFragment() {

    private lateinit var calendar: Calendar
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Initialize a calendar instance
        calendar = Calendar.getInstance()

        // Get the system current date
//        val year = calendar.get(Calendar.YEAR)
        val year = LocalDB.getBirthDate(requireActivity())!!.toInt()
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
                requireActivity(), // Context
                // Put 0 to system default theme or remove this parameter
                android.R.style.Theme_Holo_Light_Dialog_MinWidth, // Theme
                activity as (DatePickerDialog.OnDateSetListener), // DatePickerDialog.OnDateSetListener
                year, // Year
                month, // Month of year
                day // Day of month
        )

//        dialog.datePicker.calendarViewShown = false

        val viewGroup = (dialog.datePicker as ViewGroup)

        // Invisible Day and month
        (viewGroup.findViewById(activity!!.resources.getIdentifier("day", "id", "android")) as ViewGroup).visibility = View.GONE
        (viewGroup.findViewById(activity!!.resources.getIdentifier("month", "id", "android")) as ViewGroup).visibility = View.GONE

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

}
