package com.example.healthdata.ui

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthdata.R
import com.example.healthdata.databinding.FragmentGraphBinding
import com.example.healthdata.util.factory
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class GraphFragment : Fragment() {

    private val viewModel: GraphViewModel by viewModels { factory() }

    private lateinit var binding: FragmentGraphBinding

    private var averageValue: CharSequence? = null


    companion object {
        const val TAG = "GraphFragment"
        private const val AVERAGE_VALUE = "average_value"

        fun newInstance(
            averageValue: SpannableString
        ): GraphFragment {
            return GraphFragment().apply {
                arguments = bundleOf(AVERAGE_VALUE to averageValue)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_graph, container, false)
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        averageValue = requireArguments().getCharSequence(AVERAGE_VALUE)

        setupObservers()
        initViews()

        // Get time in bed from repository
        viewModel.getTimeInBedByMonthData()
    }

    private fun initViews() {
        if (averageValue?.isNotEmpty() == true) {
            binding.timeInBedTextView.text = averageValue
        } else {
            binding.timeInBedTextView.text = getString(R.string.empty_value)
        }
    }

    private fun setupObservers() {
        viewModel.onBackPressedEvent.observe(viewLifecycleOwner) {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        // Observe time sleep by month map
        viewModel.monthMapLiveData.observe(viewLifecycleOwner) { responseMap ->
            val entryArray = ArrayList<Entry>()

            responseMap.forEach {
                val monthFloat = it.key.value.toFloat()
                val valueFloat = (it.value / 60).toFloat()

                val entry = Entry(monthFloat, valueFloat)
                entryArray.add(entry)
            }

            // MOCK DATA FOR GRAPH // TODO Fix graph
            entryArray.add(Entry(1.0F, 8.0F))
            entryArray.add(Entry(2.0F, 8.0F))
            entryArray.add(Entry(3.0F, 8.5F))
            entryArray.add(Entry(4.0F, 7.0F))
            entryArray.add(Entry(5.0F, 6.0F))
            entryArray.add(Entry(6.0F, 9.0F))

            val dataSet = LineDataSet(entryArray, getString(R.string.time_in_bed))
            dataSet.setColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.graph_line_color
                )
            )

            val lineData = LineData(dataSet)
            binding.chart.setData(lineData)
            binding.chart.invalidate() // refresh

        }
    }
}
