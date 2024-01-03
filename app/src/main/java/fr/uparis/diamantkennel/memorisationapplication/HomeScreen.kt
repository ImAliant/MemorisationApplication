package fr.uparis.diamantkennel.memorisationapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material3.ButtonDefaults
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
    val deletionDBRequest by model.deletionDB

    val errorEntry by model.error

    if (errorEntry != null) {
        ErrorDialog(
            when (errorEntry!!) {
                ErrorsAjout.BAD_ENTRY -> context.getString(R.string.error_bad_entry)
                ErrorsAjout.DUPLICATE -> context.getString(R.string.error_duplicate)
            }, model::cleanErrors
        )
    }

    if (creationRequest) {
        CreationDialog(
            dismiss = model::dismissCreation, model = model
        )
    }

    if (importationRequest) {
        ImportDialog(
            dismiss = model::dismissImportation, model = model
        )
    }

    if (deletionRequest) {
        DeletionDialog(
            model::deleteSelected
        )
    }

    if (deletionDBRequest) {
        DeletionDBDialog(
            model::deleteAll
        )
    }

    Column(
        modifier = Modifier.padding(padding), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShowList(setOfQuestions, currentSelection, model::updateSelection)

        ActionRow(context, model, navController)

        Button(onClick = { Toast.makeText(context, "Start", Toast.LENGTH_SHORT).show() }) {
            Text(text = context.getString(R.string.main_button_start), fontSize = 30.sp)
        }

        Spacer(modifier = Modifier.padding(top = 50.dp))

        DeleteRow(context, model)
    }
}

@Composable
private fun DeleteRow(
    context: Context, model: HomeViewModel
) {
    val selection by model.selected

    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { (model::doAction)(ActionHome.DELETION_DB) },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.red))
        ) {
            Text(text = context.getString(R.string.main_button_deletebase))
        }

        Spacer(modifier = Modifier.padding(2.dp))

        Button(enabled = selection != null, onClick = {
            (model::doAction)(ActionHome.DELETION_SELECT)
        }) {
            Text(text = context.getString(R.string.main_button_delete))
        }
    }
}

@Composable
private fun ActionRow(context: Context, model: HomeViewModel, navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(onClick = { (model::doAction)(ActionHome.CREATION) }) {
            Text(text = context.getString(R.string.main_button_create))
        }

        Spacer(modifier = Modifier.padding(2.dp))

        Button(onClick = { navController.navigate(MODIFY_SET) }) {
            Text(text = context.getString(R.string.main_button_modify))
        }

        Spacer(modifier = Modifier.padding(2.dp))

        Button(onClick = { (model::doAction)(ActionHome.IMPORTATION) }) {
            Text(text = context.getString(R.string.main_button_import))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreationDialog(
    dismiss: () -> Unit, model: HomeViewModel = viewModel()
) {
    val sujet by model.sujet

    AlertDialog(onDismissRequest = dismiss, title = { Text(text = "Créer un sujet") }, text = {
        OutlinedTextField(
            sujet, label = { Text("Nouveau sujet") }, onValueChange = model::onSujetChange
        )
    }, confirmButton = {
        Button(onClick = model::addSet, content = { Text("Ajouter") })
    }, dismissButton = {
        Button(onClick = dismiss, content = { Text("Annuler") })
    })
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun ImportDialog(
    dismiss: () -> Unit, model: HomeViewModel
) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(ActionImport.FILE) }

    var lien by remember { mutableStateOf("") }
    var ctx = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            lien = uri?.let { it.toString() } ?: lien
        }
    }

    AlertDialog(onDismissRequest = dismiss,
        title = { Text(text = "Importer un jeu de question") },
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
                                        text = "Explorateur",
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
                    label = { Text(text = "Lien") })
            }
        },
        confirmButton = {
            Button(onClick = { GlobalScope.launch { model.import(ctx, lien) } },
                content = { Text("Importer") })
        },
        dismissButton = {
            Button(onClick = dismiss, content = { Text("Annuler") })
        })
}

@Composable
fun ErrorDialog(errMsg: String, dismiss: () -> Unit) = AlertDialog(onDismissRequest = dismiss,
    title = { Text(text = "Erreur") },
    text = { Text(text = errMsg) },
    confirmButton = { Button(onClick = dismiss) { Text(text = "Ok") } })

@Composable
fun DeletionDialog(dismiss: () -> Unit) = AlertDialog(onDismissRequest = dismiss,
    title = { Text(text = "Supprimer un jeu de question") },
    text = { Text(text = "Voulez-vous supprimer ce jeu de question ?") },
    confirmButton = { Button(onClick = dismiss) { Text(text = "Ok") } })

@Composable
fun DeletionDBDialog(dismiss: () -> Unit) = AlertDialog(onDismissRequest = dismiss,
    title = { Text(text = "Supprimer la base de données") },
    text = { Text(text = "Voulez-vous supprimer la base de données ?") },
    confirmButton = { Button(onClick = dismiss) { Text(text = "Ok") } })

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
