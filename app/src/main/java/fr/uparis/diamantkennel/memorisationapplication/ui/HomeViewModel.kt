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

    var creation = mutableStateOf(false)
    var importation = mutableStateOf(false)
    var deletionSelect = mutableStateOf(false)
    var deletionDB = mutableStateOf(false)

    var sujet = mutableStateOf("")
    var error = mutableStateOf<ErrorsAjout?>(null)

    /* Listener */
    fun onSujetChange(s: String) {
        sujet.value = s
    }

    fun setCreation(t: Boolean) {
        creation.value = t
    }

    fun setImportation(t: Boolean) {
        importation.value = t
    }

    fun setDeletionSelect(t: Boolean) {
        deletionSelect.value = t
    }

    fun setDeletionDB(t: Boolean) {
        deletionDB.value = t
    }

    /* Methods */
    fun updateSelection(element: SetQuestions) {
        if (selected.value == element) {
            selected.value = null
        } else {
            selected.value = element
        }
    }

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
            creation.value = false
        }
    }

    fun deleteAll() {
        deletionDB.value = false
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteTable()
        }
    }

    fun deleteSelected() {
        deletionSelect.value = false
        if (selected.value == null) return

        val selection = selected.value!!
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(selection)
        }
        selected.value = null
    }

    private fun reset() {
        sujet.value = ""
        error.value = null
    }

    fun cleanErrors() {
        error.value = null
    }
}
