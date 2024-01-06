package fr.uparis.diamantkennel.memorisationapplication.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.diamantkennel.memorisationapplication.MemoApplication
import fr.uparis.diamantkennel.memorisationapplication.STATS_TOTAL_BAD
import fr.uparis.diamantkennel.memorisationapplication.STATS_TOTAL_DONE
import fr.uparis.diamantkennel.memorisationapplication.STATS_TOTAL_GOOD
import fr.uparis.diamantkennel.memorisationapplication.STATS_TOTAL_TRIED
import fr.uparis.diamantkennel.memorisationapplication.DELAY
import fr.uparis.diamantkennel.memorisationapplication.data.Question
import fr.uparis.diamantkennel.memorisationapplication.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PlayViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as MemoApplication).database.memoDao()
    private var questions = mutableStateOf<List<Question>>(listOf())

    private val datastore = application.dataStore
    private val statsKeyTotal = intPreferencesKey(STATS_TOTAL_TRIED)
    private val statsKeyTotalDone = intPreferencesKey(STATS_TOTAL_DONE)
    private val statsKeyTotalGood = intPreferencesKey(STATS_TOTAL_GOOD)
    private val statsKeyTotalBad = intPreferencesKey(STATS_TOTAL_BAD)

    private val delayKey= intPreferencesKey(DELAY)
    val delay = datastore.data.map { it[delayKey] ?: 3000 }

    var currentQuestion = mutableStateOf<Question?>(null)
    private var index = mutableStateOf(0)
    var proposedAnswer = mutableStateOf("")
    var evaluatedAnswer = mutableStateOf<AnswerType?>(null)
    val compteurSb = mutableStateOf(0)
    private var timestampQuestion = mutableStateOf(System.currentTimeMillis())
    private var currentTime = mutableStateOf(System.currentTimeMillis())
    var showAnswer = mutableStateOf(false)
    val end = mutableStateOf(false)

    fun updateQuestionList(setId: Int) {
        if (currentQuestion.value == null) {
            viewModelScope.launch(Dispatchers.Main) {
                dao.loadQuestions(setId).collect { questionList ->
                    questions.value = questionList.shuffled()
                    if (questions.value.isNotEmpty()) {
                        datastore.edit { it[statsKeyTotal] = (it[statsKeyTotal] ?: 0) + 1 }
                    }
                    updateQuestion()
                }
            }
        }
    }

    private fun updateQuestion() {
        if (questions.value.isEmpty()) {
            currentQuestion.value = null
        } else {
            if (index.value >= questions.value.size) {
                /* Fin des questions */
                end.value = true
                viewModelScope.launch {
                    datastore.edit {
                        it[statsKeyTotalDone] = (it[statsKeyTotalDone] ?: 0) + 1
                    }
                }
            } else {
                currentQuestion.value = questions.value[index.value]
            }
        }
    }

    private fun reset() {
        proposedAnswer.value = ""
        showAnswer.value = false
    }

    fun resetAfterSb() {
        evaluatedAnswer.value = null
        compteurSb.value++
    }

    fun newQuestion() {
        reset()
        index.value++
        updateQuestion()

        // reset le timer uniquement en changeant de question
        timestampQuestion.value = System.currentTimeMillis()
    }

    fun updateAnswer(text: String) {
        proposedAnswer.value = text
    }

    /** Permet de calculer la similarité entre 2 string.
     *  Renvoie un pourcentage entre 0 et 1. */
    private fun calcSimilarite(str1: String, str2: String): Float {
        val set1 = str1.lowercase().toSet()
        val set2 = str2.lowercase().toSet()

        return set1.intersect(set2).size.toFloat() / set1.union(set2).size.toFloat()
    }

    fun checkAnswer() {
        val probaReponse = calcSimilarite(currentQuestion.value!!.reponse, proposedAnswer.value)
        if (probaReponse >= .70f) {
            evaluatedAnswer.value = AnswerType.GOOD
        } else {
            evaluatedAnswer.value = AnswerType.BAD
        }
    }

    fun sbUpdate() {
        /* TODO: Statistiques à sauvegarder : temps de réponse */
        when (evaluatedAnswer.value!!) {
            AnswerType.GOOD -> {
                viewModelScope.launch {
                    datastore.edit {
                        it[statsKeyTotalGood] = (it[statsKeyTotalGood] ?: 0) + 1
                    }
                }
                newQuestion()
            }

            AnswerType.BAD -> {
                viewModelScope.launch {
                    datastore.edit {
                        it[statsKeyTotalBad] = (it[statsKeyTotalBad] ?: 0) + 1
                    }
                }
                reset()
            }
        }
    }

    fun isDelayElapsed(delay: Int) = currentTime.value - timestampQuestion.value >= delay

    fun updateTime(time: Long) {
        currentTime.value = time
    }

}