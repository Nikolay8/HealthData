package com.example.healthdata.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthdata.repository.DataRepository
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneOffset


class GraphViewModel(
    app: Application, private val dataRepository: DataRepository
) : AndroidViewModel(app) {

    // LiveData for back pressed Event
    private val _onBackPressedEvent = MutableLiveData<Unit>()
    val onBackPressedEvent: LiveData<Unit> = _onBackPressedEvent

    // Live data for converted to bed data by YearMonth
    private var _monthMapLiveData = MutableLiveData<HashMap<YearMonth, Long>>()
    val monthMapLiveData: LiveData<HashMap<YearMonth, Long>> = _monthMapLiveData

    private var timeInBedMap = HashMap<Instant, Long>()

    fun onBackPressed() {
        _onBackPressedEvent.value = Unit
    }

    fun getTimeInBedByMonthData() {
        timeInBedMap.clear()
        dataRepository.getTimeInBedByMonthData()?.let {
            timeInBedMap = it
        }
    }

    /**
     * Converts the time in bed data to a map with YearMonth keys and total time in bed values.
     * The resulting map is stored in the LiveData [_monthMapLiveData] for observation.
     * Each entry in the input [timeInBedMap] is mapped to the corresponding YearMonth,
     * and the total time in bed for that month is aggregated in the resulting map.
     */
    fun convertToMonthMap() {
        // Initialize a HashMap to store the aggregated time in bed data by YearMonth
        val monthMap = HashMap<YearMonth, Long>()

        timeInBedMap.forEach { (instant, value) ->
            // Extract the YearMonth from the Instant
            val yearMonth = YearMonth.from(instant.atZone(ZoneOffset.UTC))
            // Update the monthMap with the aggregated time in bed for the corresponding YearMonth
            monthMap[yearMonth] = monthMap.getOrDefault(yearMonth, 0L) + value
        }

        _monthMapLiveData.value = monthMap
    }
}
