package com.example.healthdata.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.health.connect.client.PermissionController
import com.example.healthdata.R
import com.example.healthdata.databinding.FragmentInitialScreenBinding
import com.example.healthdata.di.module.HealthConnectModule
import com.example.healthdata.util.SimpleDialog
import com.example.healthdata.util.factory
import com.google.android.material.snackbar.Snackbar
import kotlin.system.exitProcess


class InitialScreenFragment : Fragment() {

    private val viewModel: InitialScreenViewModel by viewModels { factory() }

    private lateinit var binding: FragmentInitialScreenBinding

    companion object {
        const val TAG = "InitialScreenFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_initial_screen, container, false)
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        // Observe on permissions btn click
        viewModel.openPermissionsSettingsEvent.observe(viewLifecycleOwner) {
            viewModel.checkPermissions()
        }

        // Observe on install health connect app
        viewModel.installHealthConnectEvent.observe(viewLifecycleOwner) {
            activity?.let { it1 ->
                SimpleDialog.showDialog(
                    it1,
                    title = getString(R.string.install_app_title),
                    message = getString(R.string.install_app_message)
                ) {
                    installHealthConnect()
                }
            }
        }

        // Observer check permissons live data
        viewModel.requestPermissionsEvent.observe(viewLifecycleOwner) {
            showSnackbar("Lack of required permissions")
            requestPermissions.launch(viewModel.permissionsSet)
        }

        // Observer permissions granted
        viewModel.permissionsGrantedEvent.observe(viewLifecycleOwner) {
            onPermissionGranted()
        }
    }

    // Create the permissions launcher
    private val requestPermissionActivityContract =
        PermissionController.createRequestPermissionResultContract()

    private val requestPermissions =
        registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(viewModel.permissionsSet)) {
                // Permissions successfully granted
                showSnackbar("Permissions successfully granted")
                navigateToFragment(ImportDataFragment())
            } else {
                // Lack of required permissions
                showSnackbar("Lack of required permissions")
            }
        }

    /**
     * Installs HealthConnect by opening the Google Play Store to the HealthConnect provider's page
     * with the specified URI string. It then exits the current process after the installation process.
     */
    private fun installHealthConnect() {
        val uriString =
            "market://details?id=${HealthConnectModule.PROVIDER_PACKAGE_NAME}&url=healthconnect%3A%2F%2Fonboarding"
        startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setPackage("com.android.vending")
                data = Uri.parse(uriString)
                putExtra("overlay", true)
                putExtra("callerId", context?.packageName)
            }.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        )
        // Finish app and clear dagger
        exitProcess(0)
    }

    private fun onPermissionGranted() {
        showSnackbar("Permissions successfully granted")
        navigateToFragment(ImportDataFragment())
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.root, message, Snackbar.LENGTH_SHORT
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
