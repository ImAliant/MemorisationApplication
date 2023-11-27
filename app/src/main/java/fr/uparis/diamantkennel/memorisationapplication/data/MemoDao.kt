package fr.uparis.diamantkennel.memorisationapplication.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Transaction
    @Query("SELECT * FROM SetQuestions")
    fun loadAllSets(): Flow<List<SetOfQuestions>>
}
