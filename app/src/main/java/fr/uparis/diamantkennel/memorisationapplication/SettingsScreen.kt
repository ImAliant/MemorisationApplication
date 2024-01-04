package fr.uparis.diamantkennel.memorisationapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.diamantkennel.memorisationapplication.ui.SettingsViewModel

@Composable
fun SettingsScreen(padding: PaddingValues, model: SettingsViewModel = viewModel()) {
    val context = LocalContext.current

    var deletionDBRequest by model.deletionDB

    if (deletionDBRequest) {
        DeletionDBDialog(model::deleteDb) { deletionDBRequest = false }
    }

    Column(
        modifier = Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = { deletionDBRequest = true },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.red))
            ) {
                Text(text = context.getString(R.string.main_button_deletebase))
            }
        }
    }
}

@Composable
fun DeletionDBDialog(confirm: () -> Unit, dismiss: () -> Unit) =
    AlertDialog(onDismissRequest = dismiss,
        title = { Text(text = LocalContext.current.getString(R.string.delete_db)) },
        text = { Text(text = LocalContext.current.getString(R.string.delete_db_desc)) },
        confirmButton = {
            Button(onClick = confirm) { Text(text = LocalContext.current.getString(R.string.yes)) }
        },
        dismissButton = { Button(onClick = dismiss) { Text(text = LocalContext.current.getString(R.string.no)) } })
