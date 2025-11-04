package ph.edu.comteq.notetakingapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class NoteViewModel(application: Application): AndroidViewModel(application){
    private val noteDao: NoteDao = AppDatabase.getDatabase(application).noteDao()

    // Main list of all notes with their tags
    private val _notesWithTags = MutableStateFlow<List<NoteWithTags>>(emptyList())
    val notesWithTags: StateFlow<List<NoteWithTags>> = _notesWithTags.asStateFlow()

    // State for the search query (internal)
    private val _searchQuery = MutableStateFlow("")
    // Exposed search results
    private val _searchResults = MutableStateFlow<List<NoteWithTags>>(emptyList())
    val searchResults: StateFlow<List<NoteWithTags>> = _searchResults.asStateFlow()


    init {
        // Collect all notes from the repository and update _notesWithTags
        viewModelScope.launch {
            noteDao.getNotesWithTags().collect {
                _notesWithTags.value = it
            }
        }

        // Setup a listener for search queries
        viewModelScope.launch {
            _searchQuery
                .debounce(300L) // Wait for user to stop typing
                .distinctUntilChanged() // Only react if query actually changes
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        flowOf(emptyList()) // No search if query is empty
                    } else {
                        noteDao.searchNotesWithTags(query) // Call DAO search method
                    }
                }
                .collect {
                    _searchResults.value = it // Update search results
                }
        }
    }

    // Function to update the search query, called from the UI
    fun searchNotes(query: String) {
        _searchQuery.value = query
    }

    // Remaining existing functions (modified only if necessary to fit the new flow structure)
    // The previous 'allNotesWithTags' is no longer needed as notesWithTags and searchResults handle it.

    fun addNote(title: String, content: String) = viewModelScope.launch {
        val newNote = Note(title = title, content = content)
        insert(newNote)
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
    
    fun deleteNoteById(noteId: Int) = viewModelScope.launch {
        noteDao.deleteNoteById(noteId)
    }

    fun getNoteById(id: Int): Flow<NoteWithTags?> {
        return noteDao.getNoteWithTagsById(id)
    }

    fun updateNote(noteId: Int, newTitle: String, newContent: String) = viewModelScope.launch {
        val noteWithTags = noteDao.getNoteWithTags(noteId)
        if (noteWithTags != null) {
            val updatedNote = noteWithTags.note.copy(title = newTitle, content = newContent)
            noteDao.updateNote(updatedNote)
        }
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
        noteDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
    }

    fun removeTagFromNote(noteId: Int, tagId: Int) = viewModelScope.launch {
        noteDao.deleteNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
    }
}
