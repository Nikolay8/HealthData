package com.example.healthdata.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthdata.application.HealthDataApplication
import com.example.healthdata.ui.GraphViewModel
import com.example.healthdata.ui.HealthInsightsViewModel
import com.example.healthdata.ui.ImportDataViewModel
import com.example.healthdata.ui.InitialScreenViewModel

class ViewModelFactory(
    private val app: HealthDataApplication,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {

            InitialScreenViewModel::class.java -> {
                InitialScreenViewModel(app, app.healthConnectClient)
            }

            ImportDataViewModel::class.java -> {
                ImportDataViewModel(app, app.dataRepository)
            }

            HealthInsightsViewModel::class.java -> {
                HealthInsightsViewModel(app)
            }

            GraphViewModel::class.java -> {
                GraphViewModel(app, app.dataRepository)
            }

            else -> {
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }
}

fun Fragment.factory() =
    ViewModelFactory(requireContext().applicationContext as HealthDataApplication)