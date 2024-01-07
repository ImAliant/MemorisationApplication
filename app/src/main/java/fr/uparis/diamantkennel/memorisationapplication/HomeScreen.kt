package fr.uparis.diamantkennel.memorisationapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fr.uparis.diamantkennel.memorisationapplication.data.SetOfQuestions
import fr.uparis.diamantkennel.memorisationapplication.data.SetQuestions
import fr.uparis.diamantkennel.memorisationapplication.ui.ActionHome
import fr.uparis.diamantkennel.memorisationapplication.ui.ActionImport
import fr.uparis.diamantkennel.memorisationapplication.ui.ErrorsAjout
import fr.uparis.diamantkennel.memorisationapplication.ui.HomeViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    padding: PaddingValues, navController: NavController, model: HomeViewModel = viewModel()
) {
    val context = LocalContext.current

    val setOfQuestions by model.setFlow.collectAsState(listOf())
    val currentSelection by model.selected

    val creationRequest by model.creation
    val importationRequest by model.importation
    val deletionRequest by model.deletionSelect

    val errorEntry by model.error

    ShowDialog(errorEntry != null) {
        ErrorDialog(
            when (errorEntry!!) {
                ErrorsAjout.BAD_ENTRY -> context.getString(R.string.error_bad_entry)
                ErrorsAjout.DUPLICATE -> context.getString(R.string.error_duplicate)
            }, model::cleanErrors
        )
    }
    ShowDialog(creationRequest) { CreationDialog(model::dismissCreation, model) }
    ShowDialog(importationRequest) { ImportDialog(model::dismissImportation, model) }
    ShowDialog(deletionRequest) {
        DeletionDialog(model::dismissDeleteOne, model::deleteSelected)
    }

    Home(
        padding,
        navController,
        model,
        setOfQuestions,
        currentSelection
    )
}

@Composable
private fun Home(
    padding: PaddingValues,
    navController: NavController,
    model: HomeViewModel,
    setOfQuestions: List<SetOfQuestions> = listOf(),
    currentSelection: SetQuestions? = null,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(padding), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShowList(setOfQuestions, currentSelection, model::updateSelection)

        ActionRow(context, model, navController)

        Button(
            enabled = currentSelection != null,
            onClick = { navController.navigate("$PLAY/${currentSelection?.idSet}") }) {
            Text(text = context.getString(R.string.main_button_start), fontSize = 30.sp)
        }

        Spacer(modifier = Modifier.padding(top = 50.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(enabled = currentSelection != null, onClick = {
                model.doAction(ActionHome.DELETION_SELECT)
            }) {
                Text(text = context.getString(R.string.main_button_delete))
            }
        }
    }
}

@Composable
private fun ActionRow(context: Context, model: HomeViewModel, navController: NavController) {
    val selection by model.selected

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(onClick = { model.doAction(ActionHome.CREATION) }) {
            Text(text = context.getString(R.string.main_button_create))
        }

        Spacer(modifier = Modifier.padding(2.dp))

        Button(
            enabled = selection != null,
            onClick = { navController.navigate("$MODIFY_SET/${selection?.idSet}") }) {
            Text(text = context.getString(R.string.modify))
        }

        Spacer(modifier = Modifier.padding(2.dp))

        Button(onClick = { model.doAction(ActionHome.IMPORTATION) }) {
            Text(text = context.getString(R.string.importation))
        }
    }
}

@Composable
fun CreationDialog(
    dismiss: () -> Unit, model: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val sujet by model.sujet

    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text(text = context.getString(R.string.create_subject)) },
        text = {
            OutlinedTextField(
                sujet,
                label = { Text(context.getString(R.string.new_subject)) },
                onValueChange = model::onSujetChange
            )
        },
        confirmButton = {
            Button(onClick = model::addSet, content = { Text(context.getString(R.string.add)) })
        },
        dismissButton = {
            Button(onClick = dismiss, content = { Text(context.getString(R.string.cancel)) })
        })
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ImportDialog(
    dismiss: () -> Unit, model: HomeViewModel
) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(ActionImport.FILE) }

    val context = LocalContext.current
    var lien by remember { mutableStateOf("") }
    val ctx = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            lien = uri?.toString() ?: lien
        }
    }

    AlertDialog(onDismissRequest = dismiss,
        title = { Text(text = context.getString(R.string.import_set_qestions)) },
        text = {
            Column {
                Column(Modifier.selectableGroup()) {
                    ActionImport.values().forEach { text ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { onOptionSelected(text) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = (text == selectedOption), onClick = null
                            )
                            Text(
                                text = text.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )

                            if (text == ActionImport.FILE) {
                                Button(enabled = selectedOption == ActionImport.FILE,
                                    modifier = Modifier.padding(start = 16.dp),
                                    onClick = {
                                        filePickerLauncher.launch(
                                            Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
                                                .setType("*/*")
                                        )
                                    }) {
                                    Text(
                                        text = context.getString(R.string.explorer),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(enabled = selectedOption == ActionImport.INTERNET,
                    value = lien,
                    onValueChange = { newTextValue -> lien = newTextValue },
                    label = { Text(text = context.getString(R.string.link)) })
            }
        },
        confirmButton = {
            Button(onClick = { GlobalScope.launch { model.import(ctx, lien) } },
                content = { Text(context.getString(R.string.importation)) })
        },
        dismissButton = {
            Button(onClick = dismiss, content = { Text(context.getString(R.string.cancel)) })
        })
}

@Composable
fun ErrorDialog(errMsg: String, dismiss: () -> Unit) = AlertDialog(onDismissRequest = dismiss,
    title = { Text(text = LocalContext.current.getString(R.string.error)) },
    text = { Text(text = errMsg) },
    confirmButton = { Button(onClick = dismiss) { Text(text = LocalContext.current.getString(R.string.ok)) } })

@Composable
fun DeletionDialog(dismiss: () -> Unit, confirm: () -> Unit) =
    AlertDialog(onDismissRequest = dismiss,
        title = { Text(text = LocalContext.current.getString(R.string.delete_set_questions)) },
        text = { Text(text = LocalContext.current.getString(R.string.delete_set_qestions_desc)) },
        confirmButton = { Button(onClick = confirm) { Text(text = LocalContext.current.getString(R.string.ok)) } })

@Composable
fun ShowList(
    sets: List<SetOfQuestions>,
    currentSelection: SetQuestions?,
    updateSelection: (SetQuestions) -> Unit
) {
    LazyColumn(
        Modifier.fillMaxHeight(0.6f)
    ) {
        itemsIndexed(sets) { index, item ->
            ListItem(index, item, currentSelection, updateSelection)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItem(
    index: Int,
    set: SetOfQuestions,
    currentSelection: SetQuestions?,
    updateSelection: (SetQuestions) -> Unit,
) {
    val containerColor = when {
        currentSelection == set.set -> colorResource(id = R.color.selected)
        index % 2 == 0 -> colorResource(id = R.color.list_alternate)
        else -> colorResource(id = R.color.list)
    }

    Card(
        onClick = { updateSelection(set.set) },
        Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(set.toString(), modifier = Modifier.padding(2.dp))
        }
    }
}
