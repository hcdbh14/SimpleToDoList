package com.example.simpletodolist

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.RecyclerView
import com.wiseassblog.jetpacknotesmvvmkotlin.model.RoomNoteDatabase
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_cell.view.*
import kotlinx.android.synthetic.main.task_view.view.*
import kotlinx.coroutines.runBlocking


class TaskAdapter(private var cellList: MutableList<Task>, private val view: View) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var itemTouchHelperCallback: SimpleCallback=
        object : SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val taskToRemove = cellList[viewHolder.adapterPosition]
                runBlocking {  deleteTask(taskToRemove, viewHolder.adapterPosition) }
            }
        }

    private var editedTask = 0
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

    init {
        listenKeyboard()
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(view.recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        return if (viewType == 1) {
            val addView = LayoutInflater.from(parent.context).inflate(R.layout.add_cell, parent, false)
            TaskViewHolder(addView, 0)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_view, parent, false)
            TaskViewHolder(itemView, 1)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        if (position != cellList.lastIndex) {
            val currentItem=cellList[position]
            holder.editText.isEnabled = false
            holder.itemView.background=roundCorners(currentItem.color)
            holder.editText.setText(currentItem.task)


            if (!currentItem.locked && currentItem.task =="") {
//                holder.editText.focusable = NOT_FOCUSABLE
//                holder.editText.focusable = FOCUSABLE
                holder.editText.isEnabled = true
                holder.editText.setOnTouchListener { v, event ->
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN ->
                                holder.editText.doAfterTextChanged {
                                    if(!cellList[position].locked && holder.editText.text.toString() != "" && isOpened) {
                                    runBlocking {
                                        db.roomNoteDao().writeTask(Task(currentItem.id, holder.editText.text.toString(), currentItem.color, locked= true))
                                    }
                                }
                            }
                    }

                    v?.onTouchEvent(event) ?: true
                }

//                if(!cellList[position].locked && holder.editText.text.toString() != "" && isOpened) {
//                holder.editText.doAfterTextChanged {
//                    runBlocking {
//                            db.roomNoteDao().writeTask(Task(currentItem.id, holder.editText.text.toString(), currentItem.color, locked= true))
//                        }
//                    }
//                }
            }




        } else {
            holder.itemView.setOnClickListener {
                runBlocking {
                    db.roomNoteDao().writeTask(Task(System.currentTimeMillis(), "", colors.getRandomColor(), locked= false))
                }
                for (i in cellList){
                    println(i)
                }
                runBlocking { loadData() }
                view.recycler_view.scrollToPosition(cellList.lastIndex)
            }
        }

    }


    private suspend fun loadData() {
        val data = db.roomNoteDao().getTasks().toMutableList()
        this.cellList=data
        this.notifyDataSetChanged()
    }


    private suspend fun deleteTask(taskToRemove: Task, position: Int) {
        db.roomNoteDao().removeTask(taskToRemove)
        cellList.removeAt(position)
        this.notifyItemRemoved(position)
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