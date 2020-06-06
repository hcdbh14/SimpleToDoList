package com.example.simpletodolist

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.wiseassblog.jetpacknotesmvvmkotlin.model.RoomNoteDatabase
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_cell.view.*
import kotlinx.android.synthetic.main.task_view.view.*
import kotlinx.coroutines.runBlocking


class TaskAdapter(private var cellList: Array<Task>, private val view: View) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var taskID = 0
    private  var typedText = ""
    private val colors = RandomColors()
    private val db = RoomNoteDatabase.getInstance(AppCompatActivity())
    override fun getItemCount() = cellList.size
    override fun getItemViewType(position: Int): Int { return if (position == cellList.lastIndex) 1 else 2 }
    private var isOpened=false

    private fun listenKeyboard() {
        val activityRootView: View=view
        activityRootView.viewTreeObserver
            .addOnGlobalLayoutListener {
                val heightDiff: Int=activityRootView.rootView.height - activityRootView.height
                if (heightDiff > 1000) {
                    isOpened=true
                    println("open")
                } else if (isOpened) {
                    isOpened=false
                    println("closed")
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        listenKeyboard()
        return if (viewType == 1) {
            val addView = LayoutInflater.from(parent.context).inflate(R.layout.add_cell, parent, false)
            TaskViewHolder(addView, 0)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_view, parent, false)
            TaskViewHolder(itemView, 1)
        }
    }


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        if (position != cellList.lastIndex) {
            val currentItem=cellList[position]
            holder.editText.isEnabled = false
            holder.itemView.background=roundCorners(currentItem.color)
            holder.editText.setText(currentItem.task)

            if (!currentItem.locked && currentItem.task =="") {
                holder.editText.isEnabled = true
            }

            holder.editText.doAfterTextChanged {

                taskID = currentItem.id
                typedText = holder.editText.text.toString()
                runBlocking {
                    if(!cellList[taskID - 1].locked && typedText != "" && isOpened) {
                db.roomNoteDao().writeTask(Task(taskID, typedText, cellList[taskID - 1].color, locked = true))
                    }
            }
                taskID = 0
                typedText = ""
            }


        } else {
            holder.itemView.setOnClickListener {
                runBlocking {
                    db.roomNoteDao().writeTask(Task(cellList.size + 1, "", colors.getRandomColor(), locked = false))
                }
                runBlocking { loadData(db) }
                view.recycler_view.scrollToPosition(cellList.lastIndex)
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