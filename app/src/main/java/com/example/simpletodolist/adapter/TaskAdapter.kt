package com.example.simpletodolist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletodolist.R
import com.example.simpletodolist.model.RandomColors
import com.example.simpletodolist.model.Task
import com.example.simpletodolist.repository.RoomNoteDatabase
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_cell.view.*
import kotlinx.android.synthetic.main.task_view.view.*
import kotlinx.coroutines.runBlocking


class TaskAdapter(private var cellList: MutableList<Task>, private val view: View) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var isOpened=false
    private var firstLaunch = true
    private val colors =RandomColors()
    private var toggleRowAnimation = false
    private var editedTask =Task(0, "", 0, false)
    private val db = RoomNoteDatabase.getInstance(AppCompatActivity())
    private var itemTouchHelperCallback: SimpleCallback=
        object : SimpleCallback(0, ItemTouchHelper.LEFT  or ItemTouchHelper.RIGHT ) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val taskToRemove = cellList[viewHolder.adapterPosition]
                runBlocking {  deleteTask(taskToRemove, viewHolder.adapterPosition) }
            }
            override fun getSwipeDirs (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                    if (viewHolder.adapterPosition == cellList.lastIndex) return 0
                return super.getSwipeDirs(recyclerView, viewHolder)
            }
        }

    init {
        listenKeyboard()
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(view.recycler_view)
    }

    override fun getItemCount() = cellList.size
    override fun getItemViewType(position: Int): Int { return if (position == cellList.lastIndex) 1 else 2 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        return if (viewType == 1) {
            val addView = LayoutInflater.from(parent.context).inflate(R.layout.add_cell, parent, false)
            TaskViewHolder(
                addView,
                0
            )
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_view, parent, false)
            TaskViewHolder(
                itemView,
                1
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        if (position != cellList.lastIndex) {
            val currentItem=cellList[position]
            holder.editText.isEnabled = false
            holder.itemView.background=roundCorners(currentItem.color)
            holder.editText.setText(currentItem.task)
            if (toggleRowAnimation && position != cellList.lastIndex - 1) {


                    val startRowAnimation=TranslateAnimation(0F, 0F, -250F, -250F)
                 startRowAnimation.fillAfter=true
                    holder.itemView.startAnimation(startRowAnimation)

                Handler().postDelayed({
                    val rowAnimation=TranslateAnimation(0F, 0F, -250F, 0F)
                    rowAnimation.fillAfter=true
                    rowAnimation.duration=400
                    holder.itemView.startAnimation(rowAnimation)
                }, ((cellList.size - position  ) * 10).toLong())


            } else if (toggleRowAnimation && position == cellList.lastIndex - 1) {

                val firstAnimation=TranslateAnimation(0F, 0F, -300F, 50F)
                firstAnimation.fillAfter=true
                holder.itemView.startAnimation(firstAnimation)
                firstAnimation.duration=250
                holder.itemView.scaleX = 0F
                holder.itemView.scaleY = 0F
                holder.itemView.animate().scaleX(1.25F).duration = 250
                holder.itemView.animate().scaleY(1.25F).duration = 250

                Handler().postDelayed({
                    val secondAnimation=TranslateAnimation(0F, 0F, 50F, -100F)
                    holder.itemView.startAnimation(secondAnimation)
                    secondAnimation.duration=250
                    holder.itemView.animate().scaleY(0.8F).duration = 250
                    holder.itemView.animate().scaleX(0.8F).duration = 250

                },  250)
                Handler().postDelayed({
                    val thirdAnimation=TranslateAnimation(0F, 0F, -100F, 25F)
                    holder.itemView.startAnimation(thirdAnimation)
                    thirdAnimation.duration=500
                    holder.itemView.animate().scaleY(1.1F).duration = 500
                    holder.itemView.animate().scaleX(1.1F).duration = 500


                },  500)
                Handler().postDelayed({
                    val forthAnimation=TranslateAnimation(0F, 0F, 25F, 0F)
                    holder.itemView.startAnimation(forthAnimation)
                    forthAnimation.duration=250
                    holder.itemView.animate().scaleY(1F).duration = 400
                    holder.itemView.animate().scaleX(1F).duration = 400

                },  1000)
            }


            if (!currentItem.locked && currentItem.task =="") {

                holder.editText.isEnabled = true
                holder.editText.setOnTouchListener { v, event ->
                    view.recycler_view.reopenKeyboard()
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN ->
                                holder.editText.doAfterTextChanged {
                                        editedTask =
                                            Task(
                                                currentItem.id,
                                                holder.editText.text.toString(),
                                                currentItem.color,
                                                locked=true
                                            )
                            }
                    }
                    v?.onTouchEvent(event) ?: true
                }
            }
        } else {

            val circleShape=GradientDrawable()
            circleShape.setColor(cellList.last().color.toInt())
            circleShape.cornerRadius=260f
            holder.itemView.imageID.background=circleShape
            if (!firstLaunch) {

                holder.itemView.scaleX = 0.1f
                holder.itemView.scaleY = 0.1f
                holder.itemView.animate().scaleXBy(0.8F).scaleYBy(0.8F).duration=1000

            }
            firstLaunch = false

            holder.itemView.imageID.setOnClickListener {
            toggleRowAnimation = true
                Handler().postDelayed({
                   toggleRowAnimation = false
                }, 1000)

                if (isOpened) {
                    runBlocking {
                        db.roomNoteDao().writeTask(editedTask) } }
                runBlocking {
                    holder.itemView.imageID.animate().start()
                    db.roomNoteDao().writeTask(
                        Task(
                            System.currentTimeMillis(),
                            "",
                            colors.getRandomColor(),
                            locked=false
                        )
                    )
                }
                runBlocking { loadData() }
                view.recycler_view.scrollToPosition(cellList.lastIndex)
            }
        }

    }

    private fun listenKeyboard() {
        val activityRootView: View=view.recycler_view

        activityRootView.viewTreeObserver
            .addOnGlobalLayoutListener {
                val heightDiff: Int=activityRootView.rootView.height - activityRootView.height
                if (heightDiff > 500) {
                    isOpened=true
                    println("open")
                } else if (isOpened) {
                    isOpened=false
                    runBlocking {  db.roomNoteDao().writeTask(editedTask) }
                    println("closed")
                }
            }
    }

    private fun View.reopenKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private suspend fun loadData() {
        val data = db.roomNoteDao().getTasks().toMutableList()
        println("saved")
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