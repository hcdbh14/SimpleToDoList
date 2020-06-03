package com.example.simpletodolist

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_view.view.*


class TaskAdapter(private var cellList: Array<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.task_view, parent, false)

        return TaskViewHolder(itemView)
    }

    fun update(cellList: Array<Task>) {
        this.cellList=cellList
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem=cellList[position]
        holder.itemView.background = roundCorners(currentItem.color)
        holder.textView.text=currentItem.task
    }


    override fun getItemCount()=cellList.size

    class TaskViewHolder(TaskView: View) : RecyclerView.ViewHolder(TaskView) {
        val textView: TextView=TaskView.taskViewID
    }

    private fun roundCorners(backgroundColor: Long) : GradientDrawable {
        val shape=GradientDrawable()
        shape.shape=GradientDrawable.RECTANGLE
        shape.cornerRadii=floatArrayOf(30f, 30f, 30f, 30f, 30f, 30f, 30f, 30f)
        shape.setColor(backgroundColor.toInt())
        return shape
    }
}