package fr.uparis.diamantkennel.memorisationapplication

import android.app.Application
import fr.uparis.diamantkennel.memorisationapplication.data.QuestionsDB

class MemoApplication : Application() {
    val database: QuestionsDB by lazy { QuestionsDB.getDataBase(this) }
}
