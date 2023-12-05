package fr.uparis.diamantkennel.memorisationapplication.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)])
data class SetQuestions(
    @PrimaryKey(autoGenerate = true) val idSet: Int = 0,
    val name: String,
)
