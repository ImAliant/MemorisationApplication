package fr.uparis.diamantkennel.memorisationapplication

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.diamantkennel.memorisationapplication.data.SetOfQuestions
import fr.uparis.diamantkennel.memorisationapplication.data.SetQuestions
import fr.uparis.diamantkennel.memorisationapplication.ui.HomeViewModel

@Composable
fun HomeScreen(padding: PaddingValues, model: HomeViewModel = viewModel()) {
    val context = LocalContext.current

    val setOfQuestions by model.setFlow.collectAsState(listOf())
    val currentSelection by model.selected

    var wantToCreate by remember { mutableStateOf(false) }
    var wantToImport by remember { mutableStateOf(false) }

    if (wantToCreate) {
        DialogCreation(
            annuler = {wantToCreate = false},
            confirmer = {wantToCreate = false}
        )
    }

    if (wantToImport) {
        DialogImportation(
            annuler = {wantToImport = false},
            confirmer = {wantToImport = false}
        )
    }

    Column(
        modifier = Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShowList(setOfQuestions, currentSelection)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                Toast.makeText(context, "Create", Toast.LENGTH_SHORT).show()
                wantToCreate = true
            }) {
                Text(text = context.getString(R.string.main_button_create))
            }
            Button(onClick = { Toast.makeText(context, "Modify", Toast.LENGTH_SHORT).show() }) {
                Text(text = context.getString(R.string.main_button_modify))
            }
            Button(onClick = {
                Toast.makeText(context, "Import", Toast.LENGTH_SHORT).show()
                wantToImport = true
            }) {
                Text(text = context.getString(R.string.main_button_import))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                modifier = Modifier.fillMaxWidth(0.9f),
                onClick = { Toast.makeText(context, "Start", Toast.LENGTH_SHORT).show() }) {
                Text(text = context.getString(R.string.main_button_start), fontSize = 30.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogCreation(annuler: () -> Unit, confirmer: () -> Unit) {
    var sujet by remember {mutableStateOf("")}

    AlertDialog(
        onDismissRequest = annuler,
        title = { Text(text ="Créer un sujet") },
        text = {
            OutlinedTextField(
                value = sujet,
                onValueChange = { newTextValue -> sujet = newTextValue},
                label = { Text("Nouveau sujet") }
            )
        },
        confirmButton = {
            Button(
                // on ajoute le sujet dans la liste des sujets
                onClick = confirmer,
                content = { Text("Ajouter") }
            )
        },
        dismissButton = {
            Button(
                onClick = annuler,
                content = { Text("Annuler") }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogImportation(annuler: () -> Unit, confirmer: () -> Unit) {
    val radioOptions = listOf("Locale", "Internet")
    val (selectedOption, onOptionSelected) = remember {mutableStateOf(radioOptions[0])}

    var lien by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = annuler,
        title = { Text(text = "Importer un jeu de question") },
        text = {
            Column {
                Column(Modifier.selectableGroup()) {
                    radioOptions.forEach { text ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { onOptionSelected(text) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null // pour l'instant
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = lien,
                    onValueChange = {newTextValue -> lien = newTextValue},
                    label = {Text(text = "Lien") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = confirmer,
                content = { Text("Importer") }
            )
        },
        dismissButton = {
            Button(
                onClick = annuler,
                content = { Text("Annuler") }
            )
        }
    )
}

@Composable
fun ShowList(
    sets: List<SetOfQuestions>,
    currentSelection: SetQuestions?,
) {
    LazyColumn(
        Modifier
            .fillMaxHeight(0.7f)
    ) {
        itemsIndexed(sets) { index, item ->
            ListItem(index, item, currentSelection)
        }
    }
}

@Composable
fun ListItem(
    index: Int,
    set: SetOfQuestions,
    currentSelection: SetQuestions?,
) = Card(Modifier.fillMaxSize()) {
    Row {
        Text(text = set.set.name, modifier = Modifier.padding(2.dp))
    }
}
