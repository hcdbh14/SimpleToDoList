package com.example.simpletodolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wiseassblog.jetpacknotesmvvmkotlin.model.RoomNoteDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.runBlocking
import java.lang.ref.PhantomReference

class MainActivity : AppCompatActivity() {

    private var numOfTasks = 0
    private val colors = RandomColors()
    private var dataList = emptyArray<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db=RoomNoteDatabase.getInstance(this)
        runBlocking { loadData(db) }

        val addButton=findViewById<FloatingActionButton>(R.id.addButton)
        recycler_view.adapter=TaskAdapter(dataList)
        recycler_view.layoutManager=LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)



        addButton.setOnClickListener {
            runBlocking {
                db.roomNoteDao().writeTask(Task(numOfTasks + 1, "test", colors.getRandomColor()))
                println(db.roomNoteDao().getTasks().size)
            }
            runBlocking { loadData(db) }
            (recycler_view.adapter as TaskAdapter).update(this.dataList)
            this.dataList.reverse()
        }
    }


    private suspend fun loadData(reference: RoomNoteDatabase) {
        val data=reference.roomNoteDao().getTasks()
        this.dataList=data
        this.numOfTasks=data.size
    }
}
