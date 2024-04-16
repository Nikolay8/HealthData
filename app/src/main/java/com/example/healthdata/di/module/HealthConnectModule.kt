package com.example.healthdata.di.module

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import dagger.Module
import dagger.Provides
import java.util.Optional

@Module
class HealthConnectModule {

    @Provides
    fun provideHealthConnectClient(context: Context): Optional<HealthConnectClient> {
        val availabilityStatus = HealthConnectClient.getSdkStatus(
            context, PROVIDER_PACKAGE_NAME
        )
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            return Optional.empty() // early return as there is no viable integration
        }
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            // Optionally redirect to package installer to find a provider:
            return Optional.empty()
        }
        return Optional.of(HealthConnectClient.getOrCreate(context))
    }

    companion object {
        const val PROVIDER_PACKAGE_NAME = "com.google.android.apps.healthdata"
    }

}
