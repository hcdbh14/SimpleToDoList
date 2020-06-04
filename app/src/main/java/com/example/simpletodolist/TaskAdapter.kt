package com.example.simpletodolist

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.add_cell.view.*
import kotlinx.android.synthetic.main.task_view.view.*
import org.w3c.dom.Text


class TaskAdapter(private var cellList: Array<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 1 else 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        if (viewType == 1) {
                val addView = LayoutInflater.from(parent.context).inflate(R.layout.add_cell, parent, false)
                return TaskViewHolder(addView, 0)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_view, parent, false)
            return TaskViewHolder(itemView, 1)
        }
    }

    fun update(cellList: Array<Task>) {
        this.cellList = cellList
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        if (position != 0) {
            val currentItem=cellList[position]
            holder.itemView.background=roundCorners(currentItem.color)
            holder.textView.text=currentItem.task
        }
    }


    override fun getItemCount() = cellList.size




     class TaskViewHolder(TaskView: View, type: Int) : RecyclerView.ViewHolder(TaskView) {
         lateinit var textView: TextView
         private lateinit var imageView: ImageView

        init {
            if (type == 1) {
                 this.textView = TaskView.taskViewID
            } else {
                this.imageView = TaskView.imageID
            }
        }

    }

    private fun roundCorners(backgroundColor: Long) : GradientDrawable {
        val shape=GradientDrawable()
        shape.shape=GradientDrawable.RECTANGLE
        shape.cornerRadii=floatArrayOf(30f, 30f, 30f, 30f, 30f, 30f, 30f, 30f)
        shape.setColor(backgroundColor.toInt())
        return shape
    }
}