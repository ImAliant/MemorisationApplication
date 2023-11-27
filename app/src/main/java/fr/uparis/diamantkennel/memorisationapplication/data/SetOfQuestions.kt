package fr.uparis.diamantkennel.memorisationapplication.data

import androidx.room.Embedded
import androidx.room.Relation

data class SetOfQuestions(
    @Embedded val set: SetQuestions,
    @Relation(
        parentColumn = "idSet",
        entityColumn = "setId"
    )
    val questions: List<Question>
)
