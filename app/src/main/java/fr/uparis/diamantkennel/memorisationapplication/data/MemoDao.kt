package fr.uparis.diamantkennel.memorisationapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Insert
    suspend fun insert(set: SetQuestions): Long

    @Insert
    suspend fun insertQuestion(question: Question)

    @Update
    fun updateQuestion(question: Question)

    @Query("SELECT * FROM SetQuestions")
    fun loadAllSets(): Flow<List<SetOfQuestions>>

    @Query("SELECT * FROM Question WHERE setId = :idSet")
    fun loadQuestions(idSet: Int): Flow<List<Question>>

    @Query("SELECT * FROM SetQuestions WHERE idSet = :requestedId")
    fun getSet(requestedId: Int): SetOfQuestions

    @Query("DELETE FROM SetQuestions")
    fun deleteTable()

    @Delete
    fun delete(set: SetQuestions)

    @Delete
    fun deleteQuestion(question: Question)
}
