package task_complete.example.simpletodolist.model

import androidx.room.*

@Entity(
    tableName = "tasks",
    indices = [Index("task")]
)

data class Task(
    @PrimaryKey(autoGenerate = false)
    var id: Long = System.currentTimeMillis(),
    var task: String,
    val color: Long,
    var locked: Boolean
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