package com.example.simpletodolist

import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.wiseassblog.jetpacknotesmvvmkotlin.model.RoomNoteDatabase
import kotlinx.android.synthetic.main.add_cell.view.*
import kotlinx.android.synthetic.main.task_view.view.*
import kotlinx.coroutines.runBlocking
import java.util.*


class TaskAdapter(private var cellList: Array<Task>, view: View) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var timer = Timer()
    private var DELAY:Long = 2000
    private val view = view
    private val colors = RandomColors()
    private val db = RoomNoteDatabase.getInstance(AppCompatActivity())

    override fun getItemCount() = cellList.size
    override fun getItemViewType(position: Int): Int { return if (position == 0) 1 else 2 }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        if (viewType == 1) {
                val addView = LayoutInflater.from(parent.context).inflate(R.layout.add_cell, parent, false)
                return TaskViewHolder(addView, 0)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_view, parent, false)
            return TaskViewHolder(itemView, 1)
        }
    }


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        println(position)
        if (position != 0) {
            val currentItem=cellList[position]
            holder.itemView.background=roundCorners(currentItem.color)
            holder.editText.setText(currentItem.task)


            holder.editText.doAfterTextChanged {
                runBlocking {
                db.roomNoteDao().writeTask(Task(position + 1, holder.editText.text.toString(), currentItem.color))
            }}

        } else {
            holder.itemView.setOnClickListener() {
                runBlocking { loadData(db) }
                runBlocking {
                    db.roomNoteDao().writeTask(Task(cellList.size + 1, "", colors.getRandomColor()))
                }
                runBlocking { loadData(db) }
            }
        }
    }

    private suspend fun loadData(reference: RoomNoteDatabase) {
        val data=reference.roomNoteDao().getTasks()
        this.cellList=data
        this.notifyDataSetChanged()
    }


     class TaskViewHolder(TaskView: View, type: Int) : RecyclerView.ViewHolder(TaskView) {

         lateinit var editText: EditText
         private lateinit var imageView: ImageView

        init {
            if (type == 1) {
                this.editText = TaskView.editTextID
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