package fr.uparis.diamantkennel.memorisationapplication.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = SetQuestions::class,
        parentColumns = ["idSet"],
        childColumns = ["setId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Question(
    @PrimaryKey(autoGenerate = true) val idQuestion: Int = 0,
    val setId: Int, // Foreign key linking to SetQuestions
    val enonce: String,
    val reponse: String
)
