package fr.uparis.diamantkennel.memorisationapplication.ui

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.diamantkennel.memorisationapplication.MemoApplication
import fr.uparis.diamantkennel.memorisationapplication.data.SetQuestions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as MemoApplication).database.memoDao()

    var setFlow = dao.loadAllSets()
    var selected = mutableStateOf<SetQuestions?>(null)

    var wantToCreate = mutableStateOf(false)
    var wantToImport = mutableStateOf(false)

    var sujet = mutableStateOf("")
    var error = mutableStateOf<ErrorsAjout?>(null)

    /* Listener */
    fun onSujetChange(s: String) {
        sujet.value = s
    }

    fun setCreation(t: Boolean) {
        wantToCreate.value = t
    }

    fun setImportation(t: Boolean) {
        wantToImport.value = t
    }


    /* Methods */

    fun addSet() {
        if (sujet.value.isBlank()) {
            error.value = ErrorsAjout.BAD_ENTRY
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.insert(
                    SetQuestions(
                        name = sujet.value.trim()
                    )
                )
            } catch (_: SQLiteConstraintException) {
                error.value = ErrorsAjout.DUPLICATE
            }

            sujet.value = ""
        }
    }

    private fun reset() {
        sujet.value = ""
        error.value = null
    }

    fun cleanErrors() {
        error.value = null
    }
}
