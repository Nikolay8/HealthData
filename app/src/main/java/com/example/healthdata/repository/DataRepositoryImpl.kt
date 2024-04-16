package com.example.healthdata.repository

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.MutableLiveData
import com.example.healthdata.model.ResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Optional
import javax.inject.Inject
import kotlin.reflect.KClass

class DataRepositoryImpl @Inject constructor(
    private val healthConnectClient: Optional<HealthConnectClient>
) : DataRepository {

    private var _responseModelLiveData = MutableLiveData<ResponseModel>()

    private var timeInBedMapLiveData = MutableLiveData<HashMap<Instant, Long>>()

    // Create default empty model
    private var responseModel = ResponseModel(
        stepCounts = 0L,
        stepDays = 0,
        distanceCounts = 0.0,
        distanceDays = 0,
        sleepsMinute = 0L,
        sleepDays = 0,
        caloriesBurnedCount = 0.0,
        caloriesBurnedDays = 0,
        activityMinute = 0L,
        activityDays = 0
    )

    override fun getAllData(responseModelLiveData: MutableLiveData<ResponseModel>) {
        this._responseModelLiveData = responseModelLiveData
    }

    override fun getTimeInBedByMonthData(): HashMap<Instant, Long>? {
        return timeInBedMapLiveData.value
    }

    /**
     * Imports all available health data asynchronously and updates the provided LiveData with the importation state.
     *
     * If the health connect client is present, it retrieves data types including steps, distance, sleep sessions,
     * total calories burned, and active calories burned for the last three months.
     * For each data type, it invokes [readDataByTimeRange] to read data within the specified time range and updates
     * the provided [dataStateLiveData] with the importation state.
     *
     * @param dataStateLiveData The LiveData to update with the state of the data importation process.
     */
    override suspend fun importAllData(dataStateLiveData: MutableLiveData<DataState>) {
        if (healthConnectClient.isPresent) {
            val startInstant = ZonedDateTime.now(ZoneOffset.UTC).minusMonths(3).toInstant()
            val endInstant = Instant.now()


            val dataTypes = listOf(
                StepsRecord::class,
                DistanceRecord::class,
                SleepSessionRecord::class,
                TotalCaloriesBurnedRecord::class,
                ActiveCaloriesBurnedRecord::class
            )

            dataTypes.forEach {
                readDataByTimeRange(
                    healthConnectClient.get(), startInstant, endInstant, dataStateLiveData, it
                )
            }
        }
    }


    private suspend fun readDataByTimeRange(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant,
        dataStateLiveData: MutableLiveData<DataState>,
        dataTypes: KClass<out Record> // Indicates the type of health record being read
    ) {
        withContext(Dispatchers.Main) {
            dataStateLiveData.value = DataState.LOADING // Update LiveData to indicate loading state
        }

        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    dataTypes,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )

            val instantArray = arrayListOf<Instant>() // Stores start times of records

            var stepsCount = 0L // Total steps count
            var distanceCount = 0.0 // Total distance count
            var sleepSessionCount = 0L // Total sleep session duration
            val sleepByMonthMap = HashMap<Instant, Long>() // Stores sleep duration by month
            var caloriesBurnedCount = 0.0 // Total calories burned count
            var activityMinutes = 0L // Total activity duration

            response.records.forEach { record ->
                when (record) {
                    is StepsRecord -> {
                        stepsCount += record.count
                        instantArray.add(record.startTime)
                    }

                    is DistanceRecord -> {
                        distanceCount += record.distance.inKilometers
                        instantArray.add(record.startTime)
                    }

                    is SleepSessionRecord -> {
                        val instantArray = arrayListOf<Instant>()

                        val startSessionInstant = record.startTime
                        val endSessionInstant = record.endTime
                        val duration = Duration.between(startSessionInstant, endSessionInstant)
                        val minutes = duration.toMinutes()

                        sleepSessionCount += minutes
                        instantArray.add(record.startTime)
                        sleepByMonthMap[record.startTime] = minutes
                    }

                    is TotalCaloriesBurnedRecord -> {
                        caloriesBurnedCount += record.energy.inKilocalories
                        instantArray.add(record.startTime)
                    }

                    is ActiveCaloriesBurnedRecord -> {
                        val startSessionInstant = record.startTime
                        val endSessionInstant = record.endTime
                        val duration = Duration.between(startSessionInstant, endSessionInstant)
                        val minutes = duration.toMinutes()

                        activityMinutes += minutes
                        instantArray.add(record.startTime)
                    }
                }
            }

            // Update response model based on the type of health record being processed
            when (dataTypes) {
                StepsRecord::class -> {
                    val stepDays =
                        instantArray.map { it.atZone(ZoneId.systemDefault()).toLocalDate() }
                            .distinct()
                            .count()
                    responseModel = responseModel.copy(stepCounts = stepsCount, stepDays = stepDays)
                }

                DistanceRecord::class -> {
                    val distanceRecordsDays =
                        instantArray.map { it.atZone(ZoneId.systemDefault()).toLocalDate() }
                            .distinct()
                            .count()

                    responseModel = responseModel.copy(
                        distanceCounts = distanceCount, distanceDays = distanceRecordsDays
                    )

                }

                SleepSessionRecord::class -> {
                    timeInBedMapLiveData.value = sleepByMonthMap

                    val sleepRecordsDays =
                        instantArray.map { it.atZone(ZoneId.systemDefault()).toLocalDate() }
                            .distinct()
                            .count()

                    responseModel = responseModel.copy(
                        sleepsMinute = sleepSessionCount, sleepDays = sleepRecordsDays
                    )
                }

                TotalCaloriesBurnedRecord::class -> {
                    val caloriesBurnedRecordsDays =
                        instantArray.map { it.atZone(ZoneId.systemDefault()).toLocalDate() }
                            .distinct()
                            .count()

                    responseModel = responseModel.copy(
                        caloriesBurnedCount = caloriesBurnedCount,
                        caloriesBurnedDays = caloriesBurnedRecordsDays
                    )
                }

                ActiveCaloriesBurnedRecord::class -> {
                    val activityRecordsDays =
                        instantArray.map { it.atZone(ZoneId.systemDefault()).toLocalDate() }
                            .distinct()
                            .count()

                    responseModel = responseModel.copy(
                        activityMinute = activityMinutes, activityDays = activityRecordsDays
                    )
                }
            }

            // Update LiveData with ready state and response model
            withContext(Dispatchers.Main) {
                dataStateLiveData.value = DataState.READY
                _responseModelLiveData.value = responseModel
            }

        } catch (e: Exception) {
            // Handle exception and update LiveData with failed state
            withContext(Dispatchers.Main) {
                dataStateLiveData.value = DataState.FAILED
            }
        }
    }


}