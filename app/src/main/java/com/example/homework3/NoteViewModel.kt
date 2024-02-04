package com.example.homework3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {

    fun insertNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.insert(note)
        }
    }

    fun getLastNote(callback: (Note?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val lastNote = noteDao.getLastNote()
            withContext(Dispatchers.Main) {
                callback(lastNote)
            }
        }
    }




    // No need for fetching or observing a list of notes
    // Other methods like updateNote or deleteNote can be added if necessary
}
