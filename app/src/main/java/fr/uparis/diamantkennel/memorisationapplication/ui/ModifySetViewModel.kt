package fr.uparis.diamantkennel.memorisationapplication.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.diamantkennel.memorisationapplication.MemoApplication
import fr.uparis.diamantkennel.memorisationapplication.data.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModifySetViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as MemoApplication).database.memoDao()
    private val initialId = -1 // invalid ID, mean that we don't have the ID yet

    var setId = mutableStateOf(initialId)
    var questions = dao.loadQuestions(setId.value)
    var selection = mutableStateOf<Question?>(null)
    var action = mutableStateOf(ActionModifySet.AUCUN)

    fun updateQuestionList(setId: Int) {
        if (setId != initialId) {
            viewModelScope.launch(Dispatchers.IO) { questions = dao.loadQuestions(setId) }
        }
    }

    fun updateSelection(question: Question) {
        if (selection.value == question) {
            selection.value = null
        } else {
            selection.value = question
        }
    }

    fun ajoutQuestion(enonce: String, reponse: String) {
        when (action.value) {
            ActionModifySet.AJOUT -> viewModelScope.launch(Dispatchers.IO) {
                dao.insertQuestion(
                    Question(
                        setId = setId.value,
                        enonce = enonce,
                        reponse = reponse
                    )
                )
            }

            ActionModifySet.MODIFICATION -> viewModelScope.launch(Dispatchers.IO) {
                val question = selection.value!!
                question.enonce = enonce
                question.reponse = reponse

                dao.updateQuestion(question)
            }

            else -> {
                /* Ce cas n'arrivera jamais */
            }
        }
    }

    fun removeQuestion() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteQuestion(selection.value!!)
        }
    }

    fun modifAction() {
        action.value = ActionModifySet.MODIFICATION
    }

    fun ajoutAction() {
        action.value = ActionModifySet.AJOUT
    }

    fun supprAction() {
        action.value = ActionModifySet.SUPPRIMER
    }

    fun dismissAction() {
        action.value = ActionModifySet.AUCUN
    }
}
