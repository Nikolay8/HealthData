package com.example.healthdata.di.component


import com.example.healthdata.application.HealthDataApplication
import com.example.healthdata.di.module.ApplicationModule
import com.example.healthdata.di.module.HealthConnectModule
import com.example.healthdata.ui.ImportDataFragment
import com.example.healthdata.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        HealthConnectModule::class
    ]
)
interface ApplicationComponent {

    fun application(healthDataApplication: HealthDataApplication)

    fun inject(mainActivity: MainActivity)

    fun inject(importDataFragment: ImportDataFragment)
}
