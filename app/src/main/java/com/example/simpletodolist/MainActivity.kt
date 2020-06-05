package com.example.simpletodolist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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


        runBlocking { recycler_view.adapter=TaskAdapter(loadTasks()) }

        recycler_view.layoutManager=LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        recycler_view.setOnTouchListener { v, _ ->
            val imm=
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        }
        setListnerToRootView()
    }

    private suspend fun loadTasks() : Array<Task> {
        val db=RoomNoteDatabase.getInstance(this)
        var taskList=db.roomNoteDao().getTasks()
        if (taskList.isEmpty()) {
            taskList+=Task(0, "", RandomColors().getRandomColor())
        }
        return taskList
    }

    var isOpened=false

    fun setListnerToRootView() {
        val activityRootView: View=window.decorView.findViewById(android.R.id.content)
        activityRootView.getViewTreeObserver()
            .addOnGlobalLayoutListener(OnGlobalLayoutListener {
                val heightDiff: Int=
                    activityRootView.getRootView().getHeight() - activityRootView.getHeight()
                if (heightDiff > 1000) { // 99% of the time the height diff will be due to a keyboard.
                    Toast.makeText(applicationContext, "Gotcha!!! softKeyboardup", 0)
                        .show()

                    if (isOpened == false) {

                        //Do two things, make the view top visible and the editText smaller
                    }
                    isOpened=true
                } else if (isOpened == true) {

                    Toast.makeText(applicationContext, "softkeyborad Down!!!", 0).show()
                    isOpened=false
                }
            })
    }
}
