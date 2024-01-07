package fr.uparis.diamantkennel.memorisationapplication

import android.content.res.Resources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.diamantkennel.memorisationapplication.data.Question
import fr.uparis.diamantkennel.memorisationapplication.ui.ActionModifySet
import fr.uparis.diamantkennel.memorisationapplication.ui.ModifySetViewModel
import kotlin.text.Typography.ellipsis

@Composable
fun ModifySetScreen(
    padding: PaddingValues,
    idSet: Int,
    model: ModifySetViewModel = viewModel()
) {
    // First update the list and set ID
    model.setId.value = idSet
    model.updateQuestionList(idSet)

    val currentSelection by model.selection
    val questions by model.questions.collectAsState(listOf())
    val action by model.action

    ShowDialog(action == ActionModifySet.AJOUT || action == ActionModifySet.MODIFICATION) {
        AjoutModifDialog(action, currentSelection, model::ajoutQuestion, model::dismissAction)
    }
    ShowDialog(action == ActionModifySet.SUPPRIMER) {
        RemoveDialog(model::removeQuestion, model::dismissAction)
    }

    Modify(
        padding = padding,
        model = model,
        questions = questions,
        currentSelection = currentSelection
    )
}

@Composable
fun Modify(
    padding: PaddingValues,
    model: ModifySetViewModel,
    questions: List<Question>,
    currentSelection: Question?
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShowQuestionList(model, questions, currentSelection)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                enabled = currentSelection != null,
                onClick = model::modifAction) {
                Text(text = context.getString(R.string.modify))
            }

            Spacer(modifier = Modifier.padding(2.dp))

            Button(onClick = model::ajoutAction) {
                Text(text = context.getString(R.string.add))
            }

            Spacer(modifier = Modifier.padding(2.dp))

            Button(
                enabled = currentSelection != null,
                onClick = model::supprAction) {
                Text(text = context.getString(R.string.modify_button_delete))
            }
        }
    }
}

@Composable
fun ShowQuestionList(
    model: ModifySetViewModel,
    questions: List<Question>,
    currentSelection: Question?
) {
    LazyColumn(
        Modifier.fillMaxHeight(0.6f)
    ) {
        itemsIndexed(questions) { index, item ->
            ListItem(index, item, currentSelection, model::updateSelection)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItem(
    index: Int,
    question: Question,
    currentSelection: Question?,
    updateSelection: (Question) -> Unit
) {

    val maxLength = Resources.getSystem().displayMetrics.widthPixels / 33

    val containerColor = when {
        currentSelection == question -> colorResource(id = R.color.selected)
        index % 2 == 0 -> colorResource(id = R.color.list_alternate)
        else -> colorResource(id = R.color.list)
    }

    Card(
        onClick = { updateSelection(question) },
        Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                question.enonce.take(maxLength)
                    .let { if (question.enonce.length > maxLength) it + ellipsis else it },
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjoutModifDialog(
    type: ActionModifySet,
    selection: Question?,
    confirm: (String, String) -> Unit,
    dismiss: () -> Unit
) {
    val context = LocalContext.current

    var enonce by remember {
        mutableStateOf(
            if (type == ActionModifySet.MODIFICATION) {
                selection?.enonce!!
            } else {
                ""
            }
        )
    }
    var reponse by remember {
        mutableStateOf(
            if (type == ActionModifySet.MODIFICATION) {
                selection?.reponse!!
            } else {
                ""
            }
        )
    }

    AlertDialog(onDismissRequest = dismiss,
        title = {
            Text(
                text = if (selection != null) {
                    context.getString(R.string.update_question)
                } else {
                    context.getString(R.string.add_question)
                }
            )
        },
        text = {
            Column {
                Row {
                    OutlinedTextField(
                        value = enonce,
                        onValueChange = { enonce = it },
                        label = { Text(text = context.getString(R.string.question)) })
                }

                Row {
                    OutlinedTextField(
                        value = reponse,
                        onValueChange = { reponse = it },
                        label = { Text(text = context.getString(R.string.reponse)) })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                confirm(enonce, reponse)
                dismiss()
            }) { Text(text = context.getString(R.string.ok)) }
        })
}

@Composable
fun RemoveDialog(confirm: () -> Unit, dismiss: () -> Unit) =
    AlertDialog(onDismissRequest = dismiss,
        title = { Text(text = LocalContext.current.getString(R.string.delete_question)) },
        text = { Text(text = LocalContext.current.getString(R.string.delete_question_desc)) },
        confirmButton = {
            Button(onClick = {
                confirm()
                dismiss()
            }) { Text(text = LocalContext.current.getString(R.string.ok)) }
        })
