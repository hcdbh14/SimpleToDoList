package com.example.simpletodolist

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.wiseassblog.jetpacknotesmvvmkotlin.model.RoomNoteDatabase
import kotlinx.android.synthetic.main.add_cell.view.*
import kotlinx.android.synthetic.main.task_view.view.*
import kotlinx.coroutines.runBlocking



class TaskAdapter(private var cellList: Array<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val colors = RandomColors()
    private var dataList = emptyArray<Task>()
    private val db = RoomNoteDatabase.getInstance(AppCompatActivity())

    override fun getItemCount() = cellList.size

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
            holder.editText.isEnabled = false
            if (holder.textView.text == "") {
                holder.editText.isEnabled = true
            }
        } else {
            holder.itemView.setOnClickListener() {
                runBlocking {
                    db.roomNoteDao().writeTask(Task(cellList.size + 1, "", colors.getRandomColor()))
                    println(db.roomNoteDao().getTasks().size)
                }
                runBlocking { loadData(db) }
                this.update(this.dataList)
                this.dataList.reverse()
            }
        }
    }





     class TaskViewHolder(TaskView: View, type: Int) : RecyclerView.ViewHolder(TaskView) {
         lateinit var editText: EditText
         lateinit var textView: TextView
         private lateinit var imageView: ImageView

        init {
            if (type == 1) {
                this.editText = TaskView.editTextID
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
    private suspend fun loadData(reference: RoomNoteDatabase) {
        val data=reference.roomNoteDao().getTasks()
        this.dataList=data
    }
}