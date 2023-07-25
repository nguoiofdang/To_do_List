package com.example.to_dolist.ui.fragments

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.R
import com.example.to_dolist.adapters.AdapterRecycleView
import com.example.to_dolist.broadcast.NotificationBroadcastReceiver
import com.example.to_dolist.databinding.FragmentTaskBinding
import com.example.to_dolist.databinding.*
import com.example.to_dolist.models.Notification
import com.example.to_dolist.models.Task
import com.example.to_dolist.ui.MainActivity
import com.example.to_dolist.utils.Constance.KEY_NOTIFICATION_TO_BROADCAST_RECEIVER
import com.example.to_dolist.utils.Constance.TAG_INTENT_TASK
import com.example.to_dolist.viewmodel.TaskViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class TaskFragment : Fragment(R.layout.fragment_task) {

    private lateinit var viewModel: TaskViewModel
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvTaskAdapter: AdapterRecycleView
    private var newTask: Task = Task()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(TAG_INTENT_TASK, Task::class.java)
        } else {
            arguments?.getSerializable(TAG_INTENT_TASK) as? Task
        }
        if(task != null) {
            val bundle = Bundle().apply {
                putSerializable(TAG_INTENT_TASK, task)
            }
            findNavController().navigate(R.id.action_taskFragment_to_itemTask, bundle)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTaskBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel
        setupRecycleView()
        swipeDeleteTask(view)

        viewModel.getUnFinishTask(false).observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.ivEmptyTask.visibility = View.VISIBLE
            } else {
                /*binding.rvTaskFragment.visibility = View.VISIBLE*/
                binding.ivEmptyTask.visibility = View.GONE
            }
            rvTaskAdapter.submitList(it)
        }
        Log.e("pass", "onViewCreated Fragment")
    }

    override fun onStart() {
        super.onStart()

        binding.fabAddTaskFromTaskFragment.setOnClickListener {
            currentDate()
            openAddTaskDialog()
        }

        rvTaskAdapter.setOnClickItemListener { task ->
            val bundle = Bundle()
            bundle.putSerializable(TAG_INTENT_TASK, task)
            findNavController().navigate(R.id.action_taskFragment_to_itemTask, bundle)
        }

        rvTaskAdapter.setOnClickFinishListener { task ->
            viewModel.updateTask(
                task.copy(
                    finish = !task.finish
                )
            )
        }

        rvTaskAdapter.setOnClickStarListener { task ->
            viewModel.updateTask(
                task.copy(
                    star = !task.star
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun openAddTaskDialog() {
        val dialogBinding = AddTaskDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(dialogBinding.root)
            setCancelable(true)
        }

        val window = dialog.window ?: return
        window.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val windowAttribute = window.attributes
        windowAttribute.gravity = Gravity.BOTTOM
        window.attributes = windowAttribute

        dialogBinding.apply {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, newTask.year!!)
            calendar.set(Calendar.MONTH, newTask.month!! - 1)
            calendar.set(Calendar.DAY_OF_MONTH, newTask.day!!)
            /*calendar.set(Calendar.HOUR_OF_DAY, newTask.hour!!)
            calendar.set(Calendar.MINUTE, newTask.minute!!)*/

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDateString = dateFormat.format(calendar.time)
            tvDate.text = selectedDateString

            /*val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val selectedTimeString = timeFormat.format(calendar.time)
            tvTime.text = selectedTimeString*/
        }

        dialogBinding.tvDate.setOnClickListener {
            setDate(dialogBinding)
        }

        dialogBinding.btnDoneAddTask.setOnClickListener {

            if (dialogBinding.edtTitleTask.text.toString().isNotEmpty()) {
                newTask.titleTask = dialogBinding.edtTitleTask.text.toString().trim()
                viewModel.insertTask(newTask)
                dialogBinding.edtTitleTask.text.clear()
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập công việc", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dialog.show()
    }

    private fun setupRecycleView() {
        rvTaskAdapter = AdapterRecycleView(requireContext())
        binding.rvTaskFragment.apply {
            adapter = rvTaskAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
    }

    private fun currentDate() {
        val calendar = Calendar.getInstance()
        newTask.apply {
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH) + 1
            day = calendar.get(Calendar.DAY_OF_MONTH)
        }
    }

    private fun setDate(dialogBinding: AddTaskDialogBinding) {
        val calendar = Calendar.getInstance()
        val year = newTask.year
        val month = newTask.month!! - 1
        val day = newTask.day

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, yearSelect, monthSelect, dayOfMonthSelect ->
                newTask.year = yearSelect
                newTask.month = monthSelect + 1
                newTask.day = dayOfMonthSelect
                calendar.set(Calendar.YEAR, yearSelect)
                calendar.set(Calendar.MONTH, monthSelect)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonthSelect)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val selectedDateString = dateFormat.format(calendar.time)
                dialogBinding.tvDate.text = selectedDateString
            }, year!!, month, day!!
        )
        datePickerDialog.show()
    }

    private fun swipeDeleteTask(view: View) {
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.layoutPosition
                val task = rvTaskAdapter.currentList[position]
                viewModel.deleteTask(task)
                Snackbar.make(view, "Xoá nhiệm vụ thành công", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.insertTask(task)
                    }
                }.show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(binding.rvTaskFragment)
    }
}