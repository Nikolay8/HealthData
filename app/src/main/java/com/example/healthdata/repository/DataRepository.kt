package com.example.healthdata.repository

import androidx.lifecycle.MutableLiveData
import com.example.healthdata.model.ResponseModel
import java.time.Instant
import java.time.Month

/**
 * Interface representing a data repository.
 * This repository manages the retrieval and importation of data related to health records.
 */
interface DataRepository {

    /**
     * Retrieves all health data and the provided LiveData with the response model.
     *
     * @param responseModelLiveData The LiveData to update with the response model containing all data.
     */
    fun getAllData(responseModelLiveData: MutableLiveData<ResponseModel>)

    /**
     * Retrieves the time in bed data for each month.
     *
     * @return A HashMap containing the time in bed data for each month.
     * The key is the Instant representing the start of the month, and the value is the total time in bed in minutes.
     * Returns null if no data is available.
     */
    fun getTimeInBedByMonthData(): MutableMap<Month, Long>?

    /**
     * Imports all available data and updates the LiveData with the state of the importation process.
     * This method is executed asynchronously.
     *
     * @param dataStateLiveData The LiveData to update with the state of the data importation process.
     */
    suspend fun importAllData(dataStateLiveData: MutableLiveData<DataState>)

}