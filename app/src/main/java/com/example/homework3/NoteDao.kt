package com.example.homework3

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAll(): List<Note>

    @Insert
    fun insertAll(vararg notes: Note)

    @Delete
    fun delete(note: Note)

    @Insert
    suspend fun insert(note: Note)

    @Query("SELECT * FROM note ORDER BY id DESC LIMIT 1")
    suspend fun getLastNote(): Note?
}
