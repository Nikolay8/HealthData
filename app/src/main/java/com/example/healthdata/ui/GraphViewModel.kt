package com.example.healthdata.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthdata.repository.DataRepository
import java.time.Month


class GraphViewModel(
    app: Application, private val dataRepository: DataRepository
) : AndroidViewModel(app) {

    // LiveData for back pressed Event
    private val _onBackPressedEvent = MutableLiveData<Unit>()
    val onBackPressedEvent: LiveData<Unit> = _onBackPressedEvent

    // Live data for converted to bed data by YearMonth
    private var _monthMapLiveData = MutableLiveData<MutableMap<Month, Long>>()
    val monthMapLiveData: LiveData<MutableMap<Month, Long>> = _monthMapLiveData

    fun onBackPressed() {
        _onBackPressedEvent.value = Unit
    }

    fun getTimeInBedByMonthData() {
        dataRepository.getTimeInBedByMonthData()?.let {
            _monthMapLiveData.value = it
        }
    }
}
