package fr.uparis.diamantkennel.memorisationapplication.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.diamantkennel.memorisationapplication.MemoApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as MemoApplication).database.memoDao()
    val deletionDB = mutableStateOf(false)

    fun deleteDb() {
        deletionDB.value = false
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteTable()
        }
    }
}
