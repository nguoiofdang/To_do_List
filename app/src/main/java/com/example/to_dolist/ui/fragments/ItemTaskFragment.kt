package com.example.to_dolist.ui.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.to_dolist.R
import com.example.to_dolist.databinding.ActivityMainBinding
import com.example.to_dolist.databinding.FragmentItemTaskBinding
import com.example.to_dolist.databinding.SetTimeDialogBinding
import com.example.to_dolist.feature.AlarmSchedulerTask
import com.example.to_dolist.models.Task
import com.example.to_dolist.ui.MainActivity
import com.example.to_dolist.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ItemTaskFragment : Fragment(R.layout.fragment_item_task) {
    private var _binding: FragmentItemTaskBinding? = null
    private val binding get() = _binding!!
    private var _bindingMainActivity: ActivityMainBinding? = null
    private val bindingMainActivity get() = _bindingMainActivity!!
    private lateinit var viewModel: TaskViewModel
    private lateinit var editTask: Task
    private lateinit var scheduler: AlarmSchedulerTask
    private val args: ItemTaskFragmentArgs by navArgs()
    private var isFinish = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemTaskBinding.bind(view)
        _bindingMainActivity = (activity as MainActivity).binding
        viewModel = (activity as MainActivity).viewModel
        bindingMainActivity.bottomNavigation.visibility = View.GONE

        /*editTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable(TAG_INTENT_TASK, Task::class.java)!!
        } else {
            requireArguments().getSerializable(TAG_INTENT_TASK) as Task
        }*/
        scheduler = AlarmSchedulerTask(requireContext())
        isFinish = requireArguments().getBoolean("isFinish", false)
        editTask = args.Task
        setDateForView()
    }

    override fun onResume() {
        super.onResume()
        binding.layoutDate.setOnClickListener {
            setDate()
        }
        binding.layoutTime.setOnClickListener {
            openSetTimeDialog()
        }
    }

    override fun onStop() {
        super.onStop()
        editTask.titleTask = binding.edtTitleTask.text.toString()
        editTask.description = binding.edtDescription.text.toString()
        viewModel.updateTask(editTask)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (editTask.alarm == true and !isFinish) {
            scheduler.scheduler(editTask)
        } else if (editTask.alarm == false and !isFinish) {
            scheduler.cancel(editTask)
        }
        bindingMainActivity.bottomNavigation.visibility = View.VISIBLE
        _binding = null
        _bindingMainActivity = null
    }

    private fun setDateForView() {
        if (isFinish) {
            binding.apply {
                edtTitleTask.isEnabled = false
                edtTitleTask.alpha = 0.5F
                edtDescription.isEnabled = false
                edtDescription.alpha = 0.5F
                layoutDate.isEnabled = false
                layoutDate.alpha = 0.5F
                layoutTime.isEnabled = false
                layoutTime.alpha = 0.5F
                layoutRepeatTask.alpha = 0.5F
            }

        }
        val calendar = Calendar.getInstance()
        calendar.set(
            editTask.year!!,
            editTask.month!! - 1,
            editTask.day!!
        )
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val selectedDateString = dateFormat.format(calendar.time)

        if (editTask.alarm) {
            calendar.set(Calendar.HOUR_OF_DAY, editTask.hour!!)
            calendar.set(Calendar.MINUTE, editTask.minute!!)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val selectedTimeString = timeFormat.format(calendar.time)
            binding.tvTime.text = selectedTimeString
        } else {
            binding.tvTime.text = "Không"
        }

        binding.apply {
            edtTitleTask.setText(editTask.titleTask)
            edtTitleTask.hint = editTask.titleTask
            edtDescription.setText(editTask.description)
            tvDate.text = selectedDateString
        }
    }

    private fun setDate() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, yearSelect, monthSelect, daySelect ->
                editTask.apply {
                    year = yearSelect
                    month = monthSelect + 1
                    day = daySelect

                    val calendar = Calendar.getInstance()
                    calendar.set(yearSelect, monthSelect, daySelect)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val selectedDateString = dateFormat.format(calendar.time)
                    binding.tvDate.text = selectedDateString

                }
            }, editTask.year!!, editTask.month!! - 1, editTask.day!!
        )
        datePickerDialog.show()
    }

    private fun setTime() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                editTask.hour = hourOfDay
                editTask.minute = minute

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val selectedTimeString = timeFormat.format(calendar.time)
                binding.tvTime.text = selectedTimeString

            }, editTask.hour!!, editTask.minute!!, true
        )
        timePickerDialog.show()
    }

    private fun openSetTimeDialog() {
        Log.e("time", editTask.toString())
        val setTimeTak = editTask.copy()
        val dialogBinding = SetTimeDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(dialogBinding.root)
            setCancelable(false)
        }

        val window = dialog.window ?: return
        window.apply {
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }
        window.attributes.gravity = Gravity.CENTER

        val calendar = Calendar.getInstance()

        dialogBinding.btnUnSendNotification.isChecked = !setTimeTak.alarm

        dialogBinding.timePicker.apply {
            setIs24HourView(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hour = setTimeTak.hour ?: calendar.get(Calendar.HOUR_OF_DAY)
                minute = setTimeTak.minute ?: calendar.get(Calendar.MINUTE)
            } else {
                currentHour = setTimeTak.hour ?: calendar.get(Calendar.HOUR_OF_DAY)
                currentMinute = setTimeTak.minute ?: calendar.get(Calendar.MINUTE)
            }

            setOnTimeChangedListener { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                setTimeTak.hour = hourOfDay
                setTimeTak.minute = minute
                setTimeTak.alarm = true
                dialogBinding.btnUnSendNotification.isChecked = !setTimeTak.alarm
                Log.e("Time", "$hourOfDay $minute")
            }
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        calendar.set(Calendar.HOUR_OF_DAY, dialogBinding.timePicker.hour)
                        calendar.set(Calendar.MINUTE, dialogBinding.timePicker.minute)
                        setTimeTak.hour = dialogBinding.timePicker.hour
                        setTimeTak.minute = dialogBinding.timePicker.hour
                    } else {
                        calendar.set(Calendar.HOUR_OF_DAY, dialogBinding.timePicker.currentHour)
                        calendar.set(Calendar.MINUTE, dialogBinding.timePicker.currentMinute)
                        setTimeTak.hour = dialogBinding.timePicker.currentHour
                        setTimeTak.minute = dialogBinding.timePicker.currentMinute
                    }
                    setTimeTak.alarm = true
                    dialogBinding.btnUnSendNotification.isChecked = !setTimeTak.alarm
                }
            }
        }

        dialogBinding.btnUnSendNotification.setOnClickListener {
            dialogBinding.btnUnSendNotification.isChecked = true
            setTimeTak.alarm = false
            setTimeTak.hour = null
            setTimeTak.minute = null
        }

        dialogBinding.btnOk.setOnClickListener {
            editTask = setTimeTak
            if (editTask.alarm) {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val selectedTimeString = timeFormat.format(calendar.time)
                binding.tvTime.text = selectedTimeString
            } else {
                binding.tvTime.text = "Không"
            }
            dialog.cancel()
        }

        dialogBinding.btnCancel.setOnClickListener {
            Log.e("time", editTask.toString())
            dialog.cancel()
        }

        dialog.show()
    }

    /*private fun scheduleNotification(task: Task) {

        val intent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
            putExtra(KEY_NOTIFICATION_TO_BROADCAST_RECEIVER, task)
        }
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                task.id,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        val alarmManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.getSystemService(AlarmManager::class.java)
        } else {
            context?.getSystemService(ALARM_SERVICE) as? AlarmManager
        }
        if (task.alarm) {
            val calendar = Calendar.getInstance()
            calendar.set(task.year!!, task.month!! - 1, task.day!!, task.hour!!, task.minute!!, 0)
            val currentCalendar = Calendar.getInstance()
            val time = calendar.timeInMillis - currentCalendar.timeInMillis
            Log.e("time", time.toString())
            if (time > 0) {
                alarmManager?.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } else {
            alarmManager?.cancel(pendingIntent)
        }
    }*/
}