package com.example.to_dolist.ui.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.R
import com.example.to_dolist.adapters.AdapterRecycleView
import com.example.to_dolist.databinding.AddTaskDialogBinding
import com.example.to_dolist.databinding.FragmentCalendarBinding
import com.example.to_dolist.feature.AlarmSchedulerTask
import com.example.to_dolist.models.*
import com.example.to_dolist.models.Date
import com.example.to_dolist.ui.MainActivity
import com.example.to_dolist.utils.Constance.TAG_INTENT_TASK
import com.example.to_dolist.viewmodel.TaskViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TaskViewModel
    private lateinit var rvTaskAdapter: AdapterRecycleView
    private var newTask = Task()
    private var dateSelected = Date()
    private lateinit var scheduler: AlarmSchedulerTask

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCalendarBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel

        setupRecycleView()
        swipeDeleteTask(view)
        currentCalendar()

        viewModel.getTaskOfTheDay(false, dateSelected.day, dateSelected.month, dateSelected.year)
            .observe(viewLifecycleOwner) {
                Log.e("view", "ben ngoai")
                if (it.isEmpty()) {
                    binding.tvEmptyTaskOnDay.visibility = View.VISIBLE
                    binding.rvTask.visibility = View.GONE
                } else {
                    binding.tvEmptyTaskOnDay.visibility = View.GONE
                    binding.rvTask.visibility = View.VISIBLE
                    rvTaskAdapter.submitList(it)
                }
            }
    }

    override fun onStart() {
        super.onStart()

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            viewModel.getTaskOfTheDay(false, dayOfMonth, month + 1, year)
                .observe(viewLifecycleOwner) {
                    if (it.isEmpty()) {
                        binding.tvEmptyTaskOnDay.visibility = View.VISIBLE
                        binding.rvTask.visibility = View.GONE
                    } else {
                        binding.tvEmptyTaskOnDay.visibility = View.GONE
                        binding.rvTask.visibility = View.VISIBLE
                        rvTaskAdapter.submitList(it)
                    }
                }
            dateSelected.apply {
                this.year = year
                this.month = month + 1
                this.day = dayOfMonth
            }
        }

        rvTaskAdapter.setOnClickItemListener {
            val bundle = Bundle()
            bundle.putSerializable(TAG_INTENT_TASK, it)
            findNavController().navigate(R.id.action_calendarFragment_to_itemTask, bundle)
        }

        rvTaskAdapter.setOnClickStarListener {
            viewModel.updateTask(it.copy(
                star = !it.star
            ))
        }

        binding.createTaskFromCalendarFragment.setOnClickListener {
            newTask.apply {
                day = dateSelected.day
                month = dateSelected.month
                year = dateSelected.year
            }
            openAddTaskDialog()
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

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDateString = dateFormat.format(calendar.time)
            tvDate.text = selectedDateString
        }

        dialogBinding.tvDate.setOnClickListener {
            setDate(dialogBinding)
        }

        dialogBinding.btnDoneAddTask.setOnClickListener {
            if (dialogBinding.edtTitleTask.text.toString().isNotEmpty()) {
                newTask.titleTask = dialogBinding.edtTitleTask.text.toString().trim()
                viewModel.insertTask(newTask)
                dialogBinding.edtTitleTask.text.clear()
                Toast.makeText(requireContext(), "via", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập công việc", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dialog.show()
    }

    private fun setupRecycleView() {
        rvTaskAdapter = AdapterRecycleView(requireContext())
        binding.rvTask.apply {
            adapter = rvTaskAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
    }

    private fun currentCalendar() {
        val calendar = Calendar.getInstance()
        newTask.apply {
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH) + 1
            day = calendar.get(Calendar.DAY_OF_MONTH)
            hour = calendar.get(Calendar.HOUR_OF_DAY)
            minute = calendar.get(Calendar.MINUTE)

        }
        dateSelected.apply {
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH) + 1
            day = calendar.get(Calendar.DAY_OF_MONTH)
        }

    }

    private fun setDate(dialogBinding: AddTaskDialogBinding) {
        val calendar = Calendar.getInstance()
        val year = newTask.year!!
        val month = newTask.month!! - 1
        val day = newTask.day!!

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
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun swipeDeleteTask(view: View) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
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
                if (task.alarm) {
                    scheduler.cancel(task)
                }
                Snackbar.make(view, "Xoá nhiệm vụ thành công", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        if (task.alarm) {
                            scheduler.scheduler(task)
                        }
                        viewModel.insertTask(task)
                    }
                }.show()
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvTask)
    }

}