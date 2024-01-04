package fr.uparis.diamantkennel.memorisationapplication

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fr.uparis.diamantkennel.memorisationapplication.ui.AnswerType
import fr.uparis.diamantkennel.memorisationapplication.ui.PlayViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    padding: PaddingValues,
    navController: NavController,
    idSet: Int,
    model: PlayViewModel = viewModel()
) {
    // First update the list and set ID
    model.setId.value = idSet
    model.updateQuestionList(idSet)

    val context = LocalContext.current

    val question by model.currentQuestion
    if (question == null) {
        return
    }
    val reponse by model.proposedAnswer
    val correction by model.evaluatedAnswer

    if (correction == AnswerType.GOOD) {
        Log.d("1312", "Bonne réponse !")
        model.newQuestion()
    }

    if (correction == AnswerType.BAD) {
        Log.d("1312", "Mauvaise réponse !")
        model.reset()
    }

    Column(
        modifier = Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = question!!.enonce)
        OutlinedTextField(
            value = reponse,
            label = { Text(text = "Réponse") },
            onValueChange = model::updateAnswer
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = model::checkAnswer) {
                Text(text = "Répondre")
            }

            Button(
                enabled = false /* TODO: s'activer au bout de 3 secondes */,
                onClick = { /*TODO*/ }) {
                Text(text = "Voir réponse")
            }
        }
    }
}
