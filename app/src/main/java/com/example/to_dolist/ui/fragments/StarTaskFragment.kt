package com.example.to_dolist.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_dolist.R
import com.example.to_dolist.adapters.AdapterRecycleView
import com.example.to_dolist.databinding.ActivityMainBinding
import com.example.to_dolist.databinding.FragmentStarTaskBinding
import com.example.to_dolist.ui.MainActivity
import com.example.to_dolist.viewmodel.TaskViewModel

class StarTaskFragment : Fragment(R.layout.fragment_star_task) {

    private var _activityMainBinding: ActivityMainBinding? = null
    private val activityMainBinding get() = _activityMainBinding!!
    private var _binding: FragmentStarTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterRecycleView: AdapterRecycleView
    private lateinit var viewModel: TaskViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStarTaskBinding.bind(view)
        _activityMainBinding = (activity as MainActivity).binding
        viewModel = (activity as MainActivity).viewModel
        activityMainBinding.bottomNavigation.visibility = View.GONE

        setupAdapterRecycleView()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getTaskMountStar(true).observe(viewLifecycleOwner, Observer {
            adapterRecycleView.submitList(it)
        })
    }

    override fun onResume() {
        super.onResume()

        adapterRecycleView.setOnItemClickListener { task ->
            val bundle = Bundle()
            bundle.putSerializable("task", task)
        }

        adapterRecycleView.setOnClickStarListener { task ->
            viewModel.updateTask(task.copy(
                star = !task.star
            ))
        }

        adapterRecycleView.setOnClickFinishListener { task ->
            viewModel.updateTask(task.copy(
                finish = !task.finish
            ))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityMainBinding.bottomNavigation.visibility = View.VISIBLE
        _binding = null
        _activityMainBinding = null
    }

    private fun setupAdapterRecycleView() {
        adapterRecycleView = AdapterRecycleView(requireContext())
        binding.rvStarTask.apply {
            adapter = adapterRecycleView
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
    }

}