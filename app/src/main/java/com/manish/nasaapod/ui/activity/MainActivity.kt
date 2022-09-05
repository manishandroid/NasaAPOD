package com.manish.nasaapod.ui.activity

import android.os.Bundle
import android.text.format.DateUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.manish.nasaapod.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {

        binding.floatingActionButton.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openDatePicker(){
        val constraintsBuilder = getCalendarConstraint()
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .setTitleText("Select date")
                .build()

        datePicker.addOnPositiveButtonClickListener {
            // Respond to positive button click.
            binding.tvDateToday.text = DateUtils.formatDateTime(this, it, DateUtils.FORMAT_ABBREV_ALL)
        }
        datePicker.addOnNegativeButtonClickListener {
            // Respond to negative button click.
        }
        datePicker.addOnCancelListener {
            // Respond to cancel button click.
            it.dismiss()
        }
        datePicker.addOnDismissListener {
            // Respond to dismiss events.
            it.dismiss()
        }

        datePicker.show(supportFragmentManager, "DatePicker")
    }

    private fun getCalendarConstraint(): CalendarConstraints.Builder {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        calendar.timeInMillis = today
        calendar[Calendar.MONTH] = Calendar.JANUARY
        val janThisYear = calendar.timeInMillis

        calendar.timeInMillis = today
        calendar[Calendar.MONTH] = Calendar.DECEMBER
        val decThisYear = calendar.timeInMillis

        return CalendarConstraints.Builder()
            .setStart(janThisYear)
            .setEnd(decThisYear)
    }


}