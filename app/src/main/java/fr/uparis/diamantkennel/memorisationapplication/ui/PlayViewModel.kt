package fr.uparis.diamantkennel.memorisationapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import fr.uparis.diamantkennel.memorisationapplication.MemoApplication

class PlayViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as MemoApplication).database.memoDao()

}
