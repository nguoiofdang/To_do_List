package com.example.to_dolist.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.example.to_dolist.databinding.AddTaskDialogBinding
import com.example.to_dolist.models.Task

class CreateTaskDialog(private val context: Context) {

    fun openAddTaskDialog() {
        val dialogBinding = AddTaskDialogBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(dialogBinding.root)
            setCancelable(true)
        }

        val window = dialog.window ?: return
        window.apply {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val windowAttribute = window.attributes
        windowAttribute.gravity = Gravity.BOTTOM
        window.attributes = windowAttribute
        dialog.show()
    }

}