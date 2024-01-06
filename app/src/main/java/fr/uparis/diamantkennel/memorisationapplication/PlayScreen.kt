package fr.uparis.diamantkennel.memorisationapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fr.uparis.diamantkennel.memorisationapplication.ui.AnswerType
import fr.uparis.diamantkennel.memorisationapplication.ui.PlayViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    padding: PaddingValues,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    idSet: Int,
    model: PlayViewModel = viewModel()
) {
    // First update the list of questions
    model.updateQuestionList(idSet)

    val context = LocalContext.current

    val question by model.currentQuestion
    val reponse by model.proposedAnswer
    val correction by model.evaluatedAnswer
    var giveup by model.showAnswer
    val gameEnded by model.end
    val delay by model.delay.collectAsState(initial = 3000)

    val cpt by model.compteurSb
    if (correction != null) {
        LaunchedEffect(cpt) {
            model.sbUpdate()
            snackbarHostState.showSnackbar(
                when (correction!!) {
                    AnswerType.GOOD -> context.getString(R.string.good_answer)
                    AnswerType.BAD -> context.getString(R.string.bad_answer)
                }, duration = SnackbarDuration.Short
            )
            model.resetAfterSb()
        }
    }

    if (gameEnded) {
        EndDialog { navController.navigate(HOME) }
    }

    if (giveup && question != null) {
        SolutionDialog(question!!.reponse, model::newQuestion)
    }

    // Update timer if needed
    if (!model.isDelayElapsed(delay) && question != null) {
        model.updateTime(System.currentTimeMillis())
    }

    Column(
        modifier = Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (question == null) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(context.getString(R.string.no_question), fontSize = 30.sp)
            }
        } else {
            Text(text = question!!.enonce, fontSize = 30.sp, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.padding(top = 20.dp))

            OutlinedTextField(
                value = reponse,
                label = { Text(text = context.getString(R.string.reponse)) },
                onValueChange = model::updateAnswer
            )

            Spacer(modifier = Modifier.padding(top = 20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    enabled = reponse.isNotBlank() && correction == null,
                    onClick = model::checkAnswer
                ) {
                    Text(text = context.getString(R.string.to_answer))
                }

                Button(
                    enabled = model.isDelayElapsed(delay),
                    onClick = { giveup = true }) {
                    Text(text = context.getString(R.string.see_answer))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.navigate("$MODIFY_SET/$idSet") }) {
            Text(text = context.getString(R.string.modify_set))
        }
    }
}

@Composable
fun SolutionDialog(reponse: String, next: () -> Unit) =
    AlertDialog(onDismissRequest = next,
        title = { Text(text = LocalContext.current.getString(R.string.solution)) },
        text = { Text(text = reponse) },
        confirmButton = {
            Button(onClick = next) { Text(text = LocalContext.current.getString(R.string.ok)) }
        })

@Composable
fun EndDialog(next: () -> Unit) =
    AlertDialog(
        onDismissRequest = next,
        title = { Text(text = LocalContext.current.getString(R.string.bravo)) },
        text = { Text(text = LocalContext.current.getString(R.string.set_ended)) },
        confirmButton = {
            Button(onClick = next) { Text(text = LocalContext.current.getString(R.string.ok)) }
        })
