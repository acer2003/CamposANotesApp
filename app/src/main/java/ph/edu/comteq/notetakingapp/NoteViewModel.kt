package ph.edu.comteq.notetakingapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class NoteViewModel(application: Application): AndroidViewModel(application){
    private val noteDao: NoteDao = AppDatabase.getDatabase(application).noteDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    //val allNotes: Flow<List<Note>> = noteDao.getAllNotes()
    @OptIn(ExperimentalCoroutinesApi::class)
    val allNotes: Flow<List<Note>> = searchQuery.flatMapLatest { query ->
        if(query.isEmpty()){ // or isBlank()
            noteDao.getAllNotes() //show all notes
        }else{
            noteDao.searchNotes(query)
        }
    }

    // call this when user types in the search bar
    fun updateSearchQuery(query: String){
        _searchQuery.value = query
    }

    // call this to clear the search query
    fun clearSearch(){
        _searchQuery.value = " "
    }

    fun insert(note : Note) = viewModelScope.launch {
        noteDao.insertNote(note)
    }

    fun update(note: Note) = viewModelScope.launch{
        noteDao.updateNote(note)
    }

    fun delete (note: Note) = viewModelScope.launch{
        noteDao.deleteNote(note)
    }

    val allNotesWithTags: Flow<List<NoteWithTags>> = noteDao.getNotesWithTags()

    suspend fun getNoteById(id: Int): Note?{
        return noteDao.getNoteById(id)
    }

    suspend fun getNoteWithTags(noteId: Int): NoteWithTags?{
        return noteDao.getNoteWithTags(noteId)
    }

    fun insertTag(tag: Tag) = viewModelScope.launch{
        noteDao.insertTag(tag)
    }

    fun updateTag(tag: Tag) = viewModelScope.launch{
        noteDao.updateTag(tag)
    }

    fun deleteTag(tag: Tag) = viewModelScope.launch{
        noteDao.deleteTag(tag)
    }

    //add a tag
    fun addTagToNote(noteId: Int, tagId: Int) = viewModelScope.launch{
        noteDao.deleteNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
    }

    fun removeTagFromNote(noteId: Int, tagId: Int) = viewModelScope.launch {
        noteDao.deleteNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
    }
}