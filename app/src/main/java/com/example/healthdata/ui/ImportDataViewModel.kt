package com.example.healthdata.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthdata.model.ResponseModel
import com.example.healthdata.repository.DataRepository
import com.example.healthdata.repository.DataState
import kotlinx.coroutines.launch


class ImportDataViewModel(
    app: Application, private val dataRepository: DataRepository
) : AndroidViewModel(app) {

    // LiveData response with ResponseModel
    var responseAllData = MutableLiveData<ResponseModel>()

    // LiveData for open Health Insights fragment
    private val _openHealthInsightsEvent = MutableLiveData<Unit>()
    val openHealthInsightsEvent: LiveData<Unit> = _openHealthInsightsEvent


    /**
     * Returns an observable [DataState]
     * -    UNAVAILABLE (initial state)
     * -    LOADING (loading)
     * -    READY (loaded)
     * -    FAILED (failed to load)
     *
     * Please note it doesn't include all the different data states like Refreshing/Stale
     */
    var dataState = MutableLiveData(DataState.UNAVAILABLE)


    fun openHealthInsights() {
        _openHealthInsightsEvent.value = Unit
    }

    fun importAllData() {
        viewModelScope.launch {
            // Fetch all need data from Health Connect
            dataRepository.importAllData(dataState)
        }
        dataRepository.getAllData(responseAllData)
    }
}
