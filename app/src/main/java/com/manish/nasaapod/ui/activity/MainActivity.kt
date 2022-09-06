package com.manish.nasaapod.ui.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.manish.common_network.APODResponse
import com.manish.nasaapod.R
import com.manish.nasaapod.databinding.ActivityMainBinding
import com.manish.nasaapod.intent.MainIntent
import com.manish.nasaapod.state.MainState
import com.manish.nasaapod.ui.dialog.CommonProgress
import com.manish.nasaapod.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val mainViewModel : MainViewModel by viewModels()
    private lateinit var mCommonProgress: CommonProgress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        observeViewModel()
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

        datePicker.addOnPositiveButtonClickListener { millis ->
            // Respond to positive button click.
            binding.tvDateToday.text = DateUtils.formatDateTime(this, millis, DateUtils.FORMAT_ABBREV_ALL)
            val selectedDateTwo = convertMillisToDate("yyyy-MM-dd", millis)
            Log.v("MainActivity", "selectedDate is  calling api function")
            callAPODApi(selectedDateTwo)
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

    private fun callAPODApi(date: String){
        val queryMap = HashMap<String, String>()
        queryMap["api_key"] = getAPIKey()
        queryMap["date"] = date
        lifecycleScope.launch {
            mainViewModel.mainIntent.send(MainIntent.FetchNasaAPOD(queryMap))
        }
        Log.v("MainActivity", "apiKey is ${getAPIKey()} and date in $date api called")
    }

    private fun getAPIKey() : String {
        val applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        return applicationInfo.metaData["apiKey"].toString()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            mainViewModel.state.collect {
                when (it) {
                    is MainState.Loading -> {
                        showProgress("Loading...")
                    }
                    is MainState.FetchNasaAPODSuccess -> {
                        updateUI(it.result)
                        hideProgress()
                    }
                    is MainState.FetchNasaAPODApiError -> {
                        updateUIWhenAPIFails()
                        hideProgress()
                    }
                    is MainState.FetchNasaAPODNetworkError -> {
                        hideProgress()
                        Toast.makeText(this@MainActivity, "Network issue", Toast.LENGTH_LONG).show()
                    }
                    is MainState.FetchNasaAPODUnknownError -> {
                        hideProgress()
                        Toast.makeText(this@MainActivity, "Unknown issue", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateUI(result: APODResponse) {
        binding.tvDateToday.text = result.date

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.image_loader_progress_animation)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform()

        Glide.with(this).load(result.url).apply(options).into(binding.ivAPOD)

        binding.tvTitle.text = result.title
        binding.tvExplanation.text = result.explanation
        binding.tvExplanation.visibility = View.VISIBLE
        binding.ivAPOD.visibility = View.VISIBLE
    }

    private fun updateUIWhenAPIFails(){
        binding.tvExplanation.visibility = View.GONE
        binding.ivAPOD.visibility = View.GONE
        binding.tvTitle.text = "Something went wrong"
    }

    private fun convertMillisToDate(dateFormat: String, dateInMilliseconds: Long): String {
        return DateFormat.format(dateFormat, dateInMilliseconds).toString()
    }

    private fun showProgress(message: String) {
        if(this::mCommonProgress.isInitialized && mCommonProgress.isShowing) return
        mCommonProgress = CommonProgress.show(this, message, false, null)
    }

    private fun hideProgress() {
        if(this::mCommonProgress.isInitialized && mCommonProgress.isShowing) mCommonProgress.dismiss()
    }


}