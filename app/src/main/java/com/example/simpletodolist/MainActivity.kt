package com.example.simpletodolist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wiseassblog.jetpacknotesmvvmkotlin.model.RoomNoteDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        runBlocking { recycler_view.adapter=TaskAdapter(loadTasks(), recycler_view) }

        val linearLayoutManager=LinearLayoutManager(this)
        linearLayoutManager.reverseLayout=true
        linearLayoutManager.stackFromEnd=true
        recycler_view.layoutManager=linearLayoutManager
        recycler_view.setHasFixedSize(true)
        recycler_view.setOnTouchListener { v, _ ->
            val imm=
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        }
    }

    private suspend fun loadTasks() : Array<Task> {
        val db=RoomNoteDatabase.getInstance(this)
        var taskList=db.roomNoteDao().getTasks()
        if (taskList.isEmpty()) {
            val task = Task(0, "", RandomColors().getRandomColor(), locked = false)
            db.roomNoteDao().writeTask(task)
            taskList+=task
        }
        return taskList
    }

}
