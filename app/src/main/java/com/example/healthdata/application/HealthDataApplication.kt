package com.example.healthdata.application

import android.app.Application
import androidx.health.connect.client.HealthConnectClient
import com.example.healthdata.di.component.ApplicationComponent
import com.example.healthdata.di.component.DaggerApplicationComponent
import com.example.healthdata.di.module.ApplicationModule
import com.example.healthdata.di.module.HealthConnectModule
import com.example.healthdata.repository.DataRepositoryImpl
import java.util.Optional
import javax.inject.Inject

class HealthDataApplication : Application() {

    companion object {
        private const val TAG = "HealthDataApplication"

        @JvmStatic
        lateinit var applicationComponent: ApplicationComponent
    }

    @Inject
    lateinit var healthConnectClient: Optional<HealthConnectClient>

    @Inject
    lateinit var dataRepository: DataRepositoryImpl

    override fun onCreate() {
        super.onCreate()

        // Inject applicationComponent with modules
        applicationComponent =
            DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this))
                .healthConnectModule(
                    HealthConnectModule()
                ).build()
        applicationComponent.application(this)
    }
}