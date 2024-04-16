package com.example.healthdata.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.health.connect.client.HealthConnectClient
import com.example.healthdata.R
import com.example.healthdata.application.HealthDataApplication
import java.util.Optional
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var healthConnectClient: Optional<HealthConnectClient>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HealthDataApplication.applicationComponent.inject(this)

        // Show initial fragment at start
        replaceFragment(InitialScreenFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_fragment_container, fragment)
        transaction.commit()
    }
}
