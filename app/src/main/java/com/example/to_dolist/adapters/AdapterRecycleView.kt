package com.example.to_dolist.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.databinding.ItemRecylcleViewBinding
import com.example.to_dolist.models.Task
import com.example.to_dolist.utils.Constance.IMAGE_FINISH_TASK
import com.example.to_dolist.utils.Constance.IC_STAR_IMPORTANT
import com.example.to_dolist.utils.Constance.IMAGE_UN_FINISH_TASK
import com.example.to_dolist.utils.Constance.IC_STAR_NOT_IMPORTANT
import com.example.to_dolist.utils.Constance.VALUE_ALPHA_FINISH_TASK
import com.example.to_dolist.utils.Constance.VALUE_ALPHA_UN_FINISH_TASK

class AdapterRecycleView(private val context: Context) :
    ListAdapter<Task, AdapterRecycleView.TaskViewHolder>(DifferCallBack()) {

    private lateinit var binding: ItemRecylcleViewBinding

    inner class TaskViewHolder(private val binding: ItemRecylcleViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(task: Task) {

            if (task.finish) {
                val imageFinishResource = context.resources.getIdentifier(
                    IMAGE_FINISH_TASK,
                    "drawable",
                    context.packageName
                )
                binding.apply {
                    ivFinishTask.alpha = VALUE_ALPHA_FINISH_TASK
                    ivStar.alpha = VALUE_ALPHA_FINISH_TASK
                    ivFinishTask.setImageResource(imageFinishResource)
                    tvTitleTask.alpha = VALUE_ALPHA_FINISH_TASK
                }
            } else {
                val imageFinishResource = context.resources.getIdentifier(
                    IMAGE_UN_FINISH_TASK,
                    "drawable",
                    context.packageName
                )
                binding.apply {
                    ivStar.alpha = VALUE_ALPHA_UN_FINISH_TASK
                    ivFinishTask.alpha = VALUE_ALPHA_UN_FINISH_TASK
                    ivFinishTask.setImageResource(imageFinishResource)
                    tvTitleTask.alpha = VALUE_ALPHA_UN_FINISH_TASK
                }
            }
            if (task.star) {
                val imageStarResource = context.resources.getIdentifier(
                    IC_STAR_IMPORTANT,
                    "drawable",
                    context.packageName
                )
                binding.ivStar.setImageResource(imageStarResource)
            } else {
                val imageStarResource = context.resources.getIdentifier(
                    IC_STAR_NOT_IMPORTANT,
                    "drawable",
                    context.packageName
                )
                binding.ivStar.setImageResource(imageStarResource)
            }

            if(task.alarm) {
                binding.tvTime.visibility = View.VISIBLE
            } else {
                binding.tvTime.visibility = View.GONE
            }

            binding.apply {
                tvTitleTask.text = task.titleTask
                tvTitleTask.paint.isStrikeThruText = task.finish
                tvTime.text = "${task.hour}:${task.minute}"
                tvDate.text = "${task.day}/${task.month}/${task.year}"
            }

            binding.ivFinishTask.setOnClickListener {
                onClickFinishListener?.invoke(task)
            }
            binding.layoutItem.setOnClickListener {
                onItemClickListener?.invoke(task)
            }
            binding.ivStar.setOnClickListener {
                onClickStarListener?.invoke(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        binding = ItemRecylcleViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.onBind(task)
    }

    private var onItemClickListener: ((Task) -> Unit)? = null

    fun setOnClickItemListener(listener: (Task) -> Unit) {
        onItemClickListener = listener
    }

    private var onClickFinishListener: ((Task) -> Unit)? = null

    fun setOnClickFinishListener(listener: (Task) -> Unit) {
        onClickFinishListener = listener
    }

    private var onClickStarListener: ((Task) -> Unit)? = null

    fun setOnClickStarListener(listener: (Task) -> Unit) {
        onClickStarListener = listener
    }

    class DifferCallBack : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}