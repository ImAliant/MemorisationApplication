package fr.uparis.diamantkennel.memorisationapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Insert
    suspend fun insert(set: SetQuestions): Long

    @Insert
    suspend fun insertQuestion(question: Question)

    @Transaction
    @Query("SELECT * FROM SetQuestions")
    fun loadAllSets(): Flow<List<SetOfQuestions>>

    @Query("DELETE FROM SetQuestions")
    fun deleteTable()

    @Delete
    fun delete(set: SetQuestions)
}
