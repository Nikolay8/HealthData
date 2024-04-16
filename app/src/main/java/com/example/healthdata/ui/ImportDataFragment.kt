package com.example.healthdata.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.example.healthdata.R
import com.example.healthdata.databinding.FragmentImportDataBinding
import com.example.healthdata.model.ResponseModel
import com.example.healthdata.repository.DataState
import com.example.healthdata.util.factory
import com.google.android.material.snackbar.Snackbar


class ImportDataFragment : Fragment() {

    private val viewModel: ImportDataViewModel by viewModels { factory() }

    private lateinit var binding: FragmentImportDataBinding

    private var responseModel: ResponseModel? = null

    companion object {
        const val TAG = "ImportDataFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_import_data, container, false)
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        initViews()

        // Entry point to import all data
        viewModel.importAllData()
    }

    private fun initViews() {
        binding.circularProgressBar.onProgressChangeListener = {
            val progressString = "${Math.round(it)} %"
            binding.progressTextView.text = progressString

            if (it == 100F) {
                binding.continueBtn.isEnabled = true
            }
        }
    }

    private fun setupObservers() {
        viewModel.responseAllData.observe(viewLifecycleOwner) { responseModel ->
            this.responseModel = responseModel
        }

        viewModel.dataState.observe(viewLifecycleOwner) { dataState ->
            if (dataState === DataState.FAILED) {
                showError()
            } else if (dataState === DataState.READY) {
                binding.circularProgressBar.setProgressWithAnimation(100f, 2500)
            } else if (dataState === DataState.LOADING) {
                binding.circularProgressBar.setProgressWithAnimation(10f, 2500)
            }
        }

        viewModel.openHealthInsightsEvent.observe(viewLifecycleOwner) {
            responseModel?.let {
                navigateToFragment(HealthInsightsFragment.newInstance(it))
            }
        }
    }

    private fun showError() {
        Snackbar.make(
            binding.root,
            getString(R.string.something_went_wrong),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun navigateToFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            this.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            this.replace(R.id.main_fragment_container, fragment)
            this.commit()
        }
    }
}
