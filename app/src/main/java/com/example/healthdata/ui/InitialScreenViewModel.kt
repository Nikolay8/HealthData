package com.example.healthdata.ui

import android.app.Application
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Optional


class InitialScreenViewModel(
    app: Application, private val healthConnectClient: Optional<HealthConnectClient>
) : AndroidViewModel(app) {

    // LiveData for open permission settings event
    private val _openPermissionsSettingsEvent = MutableLiveData<Unit>()
    val openPermissionsSettingsEvent: LiveData<Unit> = _openPermissionsSettingsEvent

    // LiveData for install health connect app event
    private val _installHealthConnectEvent = MutableLiveData<Unit>()
    val installHealthConnectEvent: LiveData<Unit> = _installHealthConnectEvent

    // LiveData for request permission
    private val _requestPermissionsEvent = MutableLiveData<Unit>()
    val requestPermissionsEvent: LiveData<Unit> = _requestPermissionsEvent

    // LiveData on permission granted
    private val _permissionsGrantedEvent = MutableLiveData<Unit>()
    val permissionsGrantedEvent: LiveData<Unit> = _permissionsGrantedEvent

    fun openPermissionsSettings() {
        _openPermissionsSettingsEvent.value = Unit
    }

    // Set of permissions for required data types
    val permissionsSet =
        setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class)
        )

    /**
     * Checks if the necessary permissions are granted for accessing HealthConnect data.
     * If the HealthConnect client is present, it checks permissions and runs the appropriate action.
     * Otherwise, it triggers an event to install HealthConnect.
     */
    fun checkPermissions() {
        viewModelScope.launch {
            if (healthConnectClient.isPresent) {
                checkPermissionsAndRun(healthConnectClient.get())
            } else {
                _installHealthConnectEvent.value = Unit
            }
        }
    }

    private suspend fun checkPermissionsAndRun(healthConnectClient: HealthConnectClient) {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (granted.containsAll(permissionsSet)) {
            _permissionsGrantedEvent.value = Unit
        } else {
            _requestPermissionsEvent.value = Unit
        }
    }
}
