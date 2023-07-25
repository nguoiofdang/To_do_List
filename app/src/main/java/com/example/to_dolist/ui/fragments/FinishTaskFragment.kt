package com.example.to_dolist.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_dolist.R
import com.example.to_dolist.adapters.AdapterRecycleView
import com.example.to_dolist.databinding.ActivityMainBinding
import com.example.to_dolist.databinding.FragmentFinishTaskBinding
import com.example.to_dolist.ui.MainActivity
import com.example.to_dolist.utils.Constance.TAG_INTENT_TASK
import com.example.to_dolist.viewmodel.TaskViewModel

class FinishTaskFragment : Fragment(R.layout.fragment_finish_task) {

    private var _binding: FragmentFinishTaskBinding? = null
    private val binding get() = _binding!!
    private var _activityMainBinding: ActivityMainBinding? = null
    private val activityMainBinding get() = _activityMainBinding!!
    private lateinit var viewModel: TaskViewModel
    private lateinit var adapterRecycleView: AdapterRecycleView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFinishTaskBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel
        _activityMainBinding = (activity as MainActivity).binding
        activityMainBinding.bottomNavigation.visibility = View.GONE
        setupRecycleView()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getUnFinishTask(true).observe(viewLifecycleOwner, Observer {
            adapterRecycleView.submitList(it)
        })

        adapterRecycleView.setOnClickFinishListener { task->
            viewModel.updateTask(task.copy(
                finish = !task.finish
            ))
        }

        adapterRecycleView.setOnClickItemListener { task ->
            val bundle = Bundle()
            bundle.putSerializable(TAG_INTENT_TASK, task)
            bundle.putBoolean("isFinish", true)
            findNavController().navigate(R.id.action_finishTaskFragment_to_itemTask, bundle)
        }

        adapterRecycleView.setOnClickStarListener { task ->
            viewModel.updateTask(task.copy(
                star = !task.star
            ))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityMainBinding.bottomNavigation.visibility = View.VISIBLE
        _binding = null
        _activityMainBinding = null
    }

    private fun setupRecycleView() {
        adapterRecycleView = AdapterRecycleView(requireContext())
        binding.rvFinishTask.apply {
            adapter = adapterRecycleView
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
    }

}