package task_complete.example.simpletodolist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kenny.task_complete.R
import task_complete.example.simpletodolist.adapter.TaskAdapter
import task_complete.example.simpletodolist.model.RandomColors
import task_complete.example.simpletodolist.model.Task
import task_complete.example.simpletodolist.repository.RoomNoteDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val background = this.findViewById<View>(R.id.activity_intro)
        runBlocking { recycler_view.adapter=
            TaskAdapter(
                loadTasks(),
                recycler_view,
                background,
                applicationContext
            )
        }
        supportActionBar?.hide()
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

    private suspend fun loadTasks() : MutableList<Task> {
        val db=RoomNoteDatabase.getInstance(this)
        val taskList=db.roomNoteDao().getTasks().toMutableList()
        if (taskList.isEmpty()) {
            val task =Task(
                System.currentTimeMillis(),
                "",
                RandomColors()
                    .getRandomColor(),
                locked=false
            )
            db.roomNoteDao().writeTask(task)
            taskList.add(task)
        }
        return taskList
    }

}
