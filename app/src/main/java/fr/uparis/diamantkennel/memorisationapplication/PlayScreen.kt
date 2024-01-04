package fr.uparis.diamantkennel.memorisationapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.diamantkennel.memorisationapplication.ui.AnswerType
import fr.uparis.diamantkennel.memorisationapplication.ui.PlayViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    padding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    idSet: Int,
    model: PlayViewModel = viewModel()
) {
    // First update the list and set ID
    model.setId.value = idSet
    model.updateQuestionList(idSet)

    val question by model.currentQuestion
    if (question == null) {
        return
    }
    val reponse by model.proposedAnswer
    val correction by model.evaluatedAnswer

    val cpt by model.compteurSb
    if (correction != null) {
        LaunchedEffect(cpt) {
            model.sbUpdate()
            snackbarHostState.showSnackbar(
                when (correction!!) {
                    AnswerType.GOOD -> "Bonnne réponse !"
                    AnswerType.BAD -> "Mauvaise réponse !"
                }, duration = SnackbarDuration.Short
            )
            model.resetAfterSb()
        }
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
            Button(
                enabled = reponse.isNotBlank() && correction == null,
                onClick = model::checkAnswer
            ) {
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
