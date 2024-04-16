package com.example.healthdata.ui

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.example.healthdata.R
import com.example.healthdata.databinding.FragmentHealthInsightsBinding
import com.example.healthdata.model.ResponseModel
import com.example.healthdata.util.factory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale


class HealthInsightsFragment : Fragment() {

    private val viewModel: HealthInsightsViewModel by viewModels { factory() }

    private lateinit var binding: FragmentHealthInsightsBinding

    private var responseModel: ResponseModel? = null

    private var averageValue: SpannableString? = null

    companion object {
        const val TAG = "HealthInsightsFragment"
        private const val RESPONSE_MODEL = "responseModel"

        fun newInstance(
            responseModel: ResponseModel
        ): HealthInsightsFragment {
            return HealthInsightsFragment().apply {
                arguments = bundleOf(RESPONSE_MODEL to responseModel)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_health_insights, container, false)
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        responseModel = requireArguments().getParcelable(RESPONSE_MODEL) as? ResponseModel

        setupObservers()
        initViews()
    }

    private fun initViews() {
        val stepsPerDay = if ((responseModel?.stepDays ?: 0) != 0) {
            (responseModel?.stepCounts ?: 0) / (responseModel?.stepDays!!)
        } else {
            0
        }

        val formatter = NumberFormat.getNumberInstance(Locale.US)
        val formattedStepsPerDay = formatter.format(stepsPerDay)
        if (formattedStepsPerDay.isNotEmpty()) {
            binding.stepsCountTextView.text = formattedStepsPerDay
        } else {
            binding.stepsCountTextView.text = getString(R.string.empty_value)
        }

        // Distance view
        val distancePerDay = if ((responseModel?.distanceDays ?: 0.0) != 0.0) {
            (responseModel?.distanceCounts ?: 0.0) / (responseModel?.distanceDays!!.toDouble())
        } else {
            0.0
        }

        if (distancePerDay.isNaN()) {
            binding.distanceTextView.text = getString(R.string.empty_value)
        } else {
            val decimalFormat = DecimalFormat("#.#")
            val formattedDistancePerDay = decimalFormat.format(distancePerDay)

            if (formattedDistancePerDay.isNotEmpty()) {
                binding.distanceTextView.text = formattedDistancePerDay
            } else {
                binding.distanceTextView.text = getString(R.string.empty_value)
            }
        }

        // Time in bed
        val formattedTime = responseModel?.sleepsMinute?.let { formatMinutes(it) }

        if (formattedTime?.isNotEmpty() == true) {
            val spannableString = formatTimeFontsSize(formattedTime.toString())
            averageValue = spannableString

            binding.timeInBedTextView.text = spannableString
        } else {
            binding.timeInBedTextView.text = getString(R.string.empty_value)
        }


        // Calories Burned
        val caloriesBurnedString = if ((responseModel?.caloriesBurnedDays ?: 0.0) != 0.0) {
            (responseModel?.caloriesBurnedCount
                ?: 0.0) / (responseModel?.caloriesBurnedDays!!.toDouble())
        } else {
            0.0
        }

        if (!caloriesBurnedString.isNaN()) {
            binding.caloriesBurnedTextView.text = caloriesBurnedString.toString()
        } else {
            binding.caloriesBurnedTextView.text = getString(R.string.empty_value)
        }

        // Total activity
        val totalActivityString = if ((responseModel?.activityDays ?: 0) != 0) {
            (responseModel?.activityMinute ?: 1L) / (responseModel?.activityDays!!)
        } else {
            1L
        }

        if (totalActivityString > 0) {
            binding.totalActivityTextView.text = totalActivityString.toString()
        } else {
            binding.totalActivityTextView.text = getString(R.string.empty_value)
        }
    }

    /**
     * Formats the given duration in minutes into a human-readable string representation
     * in hours and minutes format.
     *
     * @param minutes The duration in minutes to be formatted.
     * @return A string representing the formatted duration in the format "XhrYmin".
     */
    private fun formatMinutes(minutes: Long): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return "$hours" + "hr" + remainingMinutes + "min"
    }

    /**
     * Formats the given time string by adjusting the font size of "hr" and "min" parts.
     *
     * @param timeString The time string to be formatted.
     * @return A SpannableString with adjusted font sizes for "hr" and "min" parts.
     */
    private fun formatTimeFontsSize(timeString: String): SpannableString {
        val spannableString = SpannableString(timeString)

        val hrIndex = timeString.indexOf("hr")
        if (hrIndex != -1) {
            spannableString.setSpan(
                RelativeSizeSpan(0.5f), hrIndex, hrIndex + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val minIndex = timeString.indexOf("min")
        if (minIndex != -1) {
            spannableString.setSpan(
                RelativeSizeSpan(0.5f), minIndex, minIndex + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableString
    }

    private fun setupObservers() {
        viewModel.openTimeInBedEvent.observe(viewLifecycleOwner) {
            averageValue?.let {
                navigateToFragment(GraphFragment.newInstance(it))
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            this.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            this.add(R.id.main_fragment_container, fragment)
            this.addToBackStack(fragment.tag)
            this.commit()
        }
    }
}
