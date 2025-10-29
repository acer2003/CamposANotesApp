package ph.edu.comteq.notetakingapp

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * This class represents a Note with all associated tags
 */
data class NoteWithTags(
    @Embedded
    val note: Note,

    @Relation(
        parentColumn = "id", // Note's id
        entityColumn = "id", // Tag's id
        associateBy = Junction(
            value = NoteTagCrossRef::class,
            parentColumn = "note_id",
            entityColumn = "tag_id"
        )
    )

    val tags: List<Tag>
)
