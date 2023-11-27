package fr.uparis.diamantkennel.memorisationapplication.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import fr.uparis.diamantkennel.memorisationapplication.MemoApplication
import fr.uparis.diamantkennel.memorisationapplication.data.SetQuestions

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as MemoApplication).database.memoDao()

    var setFlow = dao.loadAllSets()
    var selected = mutableStateOf<SetQuestions?>(null)
}
