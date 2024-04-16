package com.example.healthdata.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class HealthInsightsViewModel(
    app: Application
) : AndroidViewModel(app) {

    // LiveData for open Health Time in bed graph fragment
    private val _openTimeInBedEvent = MutableLiveData<Unit>()
    val openTimeInBedEvent: LiveData<Unit> = _openTimeInBedEvent

    fun openTimeInBedGraph() {
        _openTimeInBedEvent.value = Unit
    }
}
