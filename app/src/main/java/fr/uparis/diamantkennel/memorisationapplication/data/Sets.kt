package fr.uparis.diamantkennel.memorisationapplication.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    indices = [Index(value = ["idQuestion"])],
    primaryKeys = ["idSet", "idQuestion"],
    foreignKeys = [ForeignKey(
        entity = SetQuestions::class,
        parentColumns = ["idSet"],
        childColumns = ["idSet"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Question::class,
        parentColumns = ["idQuestion"],
        childColumns = ["idQuestion"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Sets(val idSet: Int, val idQuestion: Int)
