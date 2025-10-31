package ph.edu.comteq.notetakingapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "note_tag_cross_ref",
    primaryKeys = ["note_id", "tag_id"],
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NoteTagCrossRef(
    @ColumnInfo(name="note_id", index = true)
    val noteId: Int,
    @ColumnInfo(name="tag_id", index = true)
    val tagId: Int,
)
