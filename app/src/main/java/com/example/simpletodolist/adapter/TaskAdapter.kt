package com.example.simpletodolist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
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


class TaskAdapter(private var cellList: MutableList<Task>, private val view: View, private val background: View,private val context: Context) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var featherEdit: Long = 0
    private var infoOn = false
    private var isOpened=false
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
                    if (viewHolder.adapterPosition == cellList.lastIndex || infoOn) return 0
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

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        if (position != cellList.lastIndex) {


            if(cellList.size > 2 && !infoOn) {
                background.alpha = 0F
            } else {
                background.alpha = 1F
            }
            val currentItem=cellList[position]
            if (currentItem.id != featherEdit) {
                holder.editText.isEnabled = false
            }
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



            if (featherEdit == currentItem.id) {

                holder.editText.isEnabled = true
                holder.editText.setOnTouchListener { v, event ->
                    view.recycler_view.closeKeyboard()
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN ->
                                holder.editText.doAfterTextChanged {
                                    if (featherEdit == currentItem.id && holder.editText.text.toString() != "") {
                                        editedTask =
                                            Task(
                                                currentItem.id,
                                                holder.editText.text.toString(),
                                                currentItem.color,
                                                locked=true
                                            )
                                        println(holder.editText.text.toString())
                                    }
                            }
                    }
                    v?.onTouchEvent(event) ?: true
                }
            }

            holder.itemView.featherButton.setOnTouchListener {  v, event ->
                view.recycler_view.closeKeyboard()

                            featherEdit = currentItem.id
                            notifyItemChanged(cellList.indexOf(currentItem))


                v?.onTouchEvent(event) ?: true
            }




            holder.itemView.alarmButton.setOnClickListener {
                runBlocking {   view.closeKeyboard() }
                val intent: Intent=Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, holder.editText.text.toString())
                println(cellList[position].task)
                intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }

            if (infoOn) {
                holder.itemView.visibility=View.GONE
            } else {
                    holder.itemView.visibility=View.VISIBLE
            }

        } else {

            val circleShape=GradientDrawable()
            circleShape.setColor(cellList.last().color.toInt())
            circleShape.cornerRadius=360f
            circleShape.setStroke(10, Color.DKGRAY)
            holder.itemView.addButton.background=circleShape
            if (toggleRowAnimation) {
                holder.itemView.addButton.scaleX = 0.1f
                holder.itemView.addButton.scaleY = 0.1f
                holder.itemView.addButton.animate().scaleXBy(0.8F).scaleYBy(0.8F).duration=1000
            }

            holder.itemView.addButton.setOnClickListener {
                view.closeKeyboard()
                infoOn = false
            toggleRowAnimation = true
                Handler().postDelayed({
                   toggleRowAnimation = false
                }, 1000)
                runBlocking {
                    holder.itemView.addButton.animate().start()
                    db.roomNoteDao().writeTask(
                        Task(
                            System.currentTimeMillis(),
                            "",
                            colors.getRandomColor(),
                            locked=false
                        )
                    )
                }
                if(!isOpened) {
                    runBlocking { loadData() }
                    view.recycler_view.scrollToPosition(cellList.lastIndex)
                }
            }

            if (cellList.size > 1 || infoOn) {
                holder.itemView.infoButton.visibility=View.VISIBLE
            holder.itemView.infoButton.setOnClickListener {

                if (infoOn) {
                    infoOn=false
                    holder.itemView.infoButton.alpha=1F
                    background.alpha=0F

                    runBlocking { loadData() }
                    view.recycler_view.scrollToPosition(cellList.lastIndex)

                } else {
                    infoOn=true
                    holder.itemView.infoButton.alpha=0.3F
                    view.closeKeyboard()
                    val lastTask = cellList.last()
                    cellList.clear()
                    cellList.add(lastTask)

                    background.alpha=0F
                    val animation1=AlphaAnimation(0F, 1F)
                    animation1.duration=1000
                    background.alpha=1F
                    background.startAnimation(animation1)

                    this.notifyDataSetChanged()
                }
            }
            } else {
                holder.itemView.infoButton.visibility=View.GONE
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
                    if(featherEdit == editedTask.id) {
                        runBlocking { db.roomNoteDao().writeTask(editedTask) }
                    }
                    featherEdit=0
                    runBlocking { loadData() }
                    println("closed")
                }
            }
    }

    private fun View.closeKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private suspend fun loadData() {
        val data = db.roomNoteDao().getTasks().toMutableList()
        println("saved")
        this.cellList=data
        this.notifyDataSetChanged()
        view.recycler_view.scrollToPosition(cellList.lastIndex)
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
                this.imageView = TaskView.addButton
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