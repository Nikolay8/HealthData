package com.example.healthdata.ui

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthdata.R
import com.example.healthdata.databinding.FragmentGraphBinding
import com.example.healthdata.util.factory


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
        viewModel.convertToMonthMap()
    }

    private fun initViews() {
        if (averageValue?.isNotEmpty() == true) {
            binding.timeInBedTextView.text = averageValue
        } else {
            binding.timeInBedTextView.text = getString(R.string.empty_value)
        }

//        val graph = binding.graph
//        val series: LineGraphSeries<DataPoint> = LineGraphSeries<DataPoint>(
//            arrayOf<DataPoint>(
//                DataPoint(0.0, 1.0),
//                DataPoint(1.0, 5.0),
//                DataPoint(2.0, 3.0),
//                DataPoint(3.0, 2.0),
//                DataPoint(4.0, 6.0)
//            )
//        )
//        graph.addSeries(series)

    }

    private fun setupObservers() {
        viewModel.onBackPressedEvent.observe(viewLifecycleOwner) {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        // Observe time sleep by month map
        viewModel.monthMapLiveData.observe(viewLifecycleOwner) {
            // TODO imp graph by this data
        }
    }
}
