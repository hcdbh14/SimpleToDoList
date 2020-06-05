package com.example.simpletodolist

import androidx.room.*

@Entity(
    tableName = "tasks",
    indices = [Index("task")]
)

data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val task: String,
    val color: Long
)

@Dao
interface writeTasks {

    @Query("SELECT * from tasks order by id")
    suspend fun getTasks(): Array<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun writeTask(task: Task)

    @Delete
    suspend fun removeTask(task: Task)
}