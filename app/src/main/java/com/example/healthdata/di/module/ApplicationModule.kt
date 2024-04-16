package com.example.healthdata.di.module

import android.app.Application
import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import com.example.healthdata.repository.DataRepository
import com.example.healthdata.repository.DataRepositoryImpl
import dagger.Module
import dagger.Provides
import java.util.Optional
import javax.inject.Singleton

@Module
class ApplicationModule constructor(
    private val application: Application
) {

    @Provides
    fun provideApplication(): Application = application

    @Provides
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    fun provideDataRepository(healthConnectClient: Optional<HealthConnectClient>): DataRepository {
        return DataRepositoryImpl(healthConnectClient)
    }
}
