package ph.edu.comteq.notetakingapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {
    @Insert
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("Select * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Query("Select * FROM notes ORDER BY id DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchQuery || '%' " +
            "OR content LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun searchNotes(searchQuery: String): Flow<List<Note>>

    // Connect a not to a tag
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNoteCrossRef(crossRef: NoteTagCrossRef)
    // disconnect a note form a tag
    @Delete
    suspend fun deleteNoteTagCrossRef(crossRef: NoteTagCrossRef)

    // get all notes with their tags
    @Transaction  //ensures that all data loads together
    @Query("SELECT * FROM notes ORDER BY updated_at DESC")
    fun getNotesWithTags(): Flow<List<NoteWithTags>>

    // get note with its tags
    @Transaction  //ensures that all data loads together
    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteWithTags(id: Int): NoteWithTags?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long

    @Update
    suspend fun updateTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<Tag>>

    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun getTagById(id: Int): Tag?


}