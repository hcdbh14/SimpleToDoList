package com.example.simpletodolist

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.wiseassblog.jetpacknotesmvvmkotlin.model.RoomNoteDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db=RoomNoteDatabase.getInstance(this)
        runBlocking {  recycler_view.adapter=TaskAdapter(db.roomNoteDao().getTasks()) }
        recycler_view.layoutManager=LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        recycler_view.setOnTouchListener(this)
        recycler_view.setOnTouchListener(object:View.OnTouchListener {

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    hideSoftKeyboard()
                return true
            }
        })
    }


    fun Activity.hideSoftKeyboard() {
        currentFocus?.let {
            val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

}
