package fr.uparis.diamantkennel.memorisationapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SetQuestions::class, Question::class, Sets::class],
    version = 1,
)
abstract class QuestionsDB : RoomDatabase() {
    abstract fun memoDao(): MemoDao

    companion object {
        @Volatile
        private var instance: QuestionsDB? = null

        fun getDataBase(c: Context): QuestionsDB {
            if (instance != null) return instance!!
            val db =
                Room.databaseBuilder(c.applicationContext, QuestionsDB::class.java, "memo")
                    .fallbackToDestructiveMigration().build()
            instance = db
            return instance!!
        }
    }
}
