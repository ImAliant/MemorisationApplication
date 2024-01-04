package fr.uparis.diamantkennel.memorisationapplication.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.diamantkennel.memorisationapplication.MemoApplication
import fr.uparis.diamantkennel.memorisationapplication.data.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as MemoApplication).database.memoDao()
    private val initialId = -1 // invalid ID, mean that we don't have the ID yet

    var setId = mutableStateOf(initialId)
    private var questions = dao.loadQuestions(setId.value)

    var currentQuestion = mutableStateOf<Question?>(null)
    private var index = mutableStateOf(0)

    var proposedAnswer = mutableStateOf("")
    var evaluatedAnswer = mutableStateOf(AnswerType.NONE)

    fun updateQuestionList(setId: Int) {
        if (setId != initialId) {
            viewModelScope.launch(Dispatchers.IO) {
                questions = dao.loadQuestions(setId)
                updateQuestion()
            }
        }
    }

    private fun updateQuestion() {
        viewModelScope.launch(Dispatchers.IO) {
            questions.collect { questionList ->
                if (index.value >= questionList.size) {
                    index.value = 0
                }
                currentQuestion.value = questionList[index.value]
            }
        }
    }

    fun reset() {
        proposedAnswer.value = ""
        evaluatedAnswer.value = AnswerType.NONE
    }

    fun newQuestion() {
        reset()
        index.value += 1
        updateQuestion()
    }

    fun updateAnswer(text: String) {
        proposedAnswer.value = text
    }

    private fun calcSimilarite(str1: String, str2: String): Float {
        val set1 = str1.lowercase().toSet()
        val set2 = str2.lowercase().toSet()

        return set1.intersect(set2).size.toFloat() / set1.union(set2).size.toFloat()
    }

    fun checkAnswer() {
        val probaReponse = calcSimilarite(currentQuestion.value!!.reponse, proposedAnswer.value)
        if (probaReponse >= .60f) {
            evaluatedAnswer.value = AnswerType.GOOD
        } else {
            evaluatedAnswer.value = AnswerType.BAD
        }
    }


}
