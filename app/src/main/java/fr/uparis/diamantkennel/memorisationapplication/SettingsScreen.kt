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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.diamantkennel.memorisationapplication.ui.SettingsViewModel

@Composable
fun SettingsScreen(padding: PaddingValues, model: SettingsViewModel = viewModel()) {
    val context = LocalContext.current

    var deletionDBRequest by model.deletionDB
    var cleanStatRequest by model.deletionStat

    if (deletionDBRequest) {
        DeletionDBDialog(model::deleteDb) { deletionDBRequest = false }
    }

    if (cleanStatRequest) {
        CleanStatDialog(model::cleanStats) { cleanStatRequest = false }
    }

    Column(
        modifier = Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Stats(model)

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Divider(color = Color.Gray)
        Spacer(modifier = Modifier.padding(top = 10.dp))

        Text(text = "Gestion", fontSize = 30.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { deletionDBRequest = true },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.red))
            ) {
                Text(text = context.getString(R.string.main_button_deletebase))
            }

            Button(
                onClick = { cleanStatRequest = true },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.red))
            ) {
                Text(text = context.getString(R.string.clean_stat_button))
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

@Composable
fun Stats(model: SettingsViewModel) {
    val context = LocalContext.current

    val games by model.statTotal.collectAsState(0)
    val gamesDone by model.statTotalDone.collectAsState(0)
    val goodAnswers by model.statTotalGood.collectAsState(0)
    val badAnswers by model.statTotalBad.collectAsState(0)

    Text(text = context.getString(R.string.stats), fontSize = 30.sp)

    Column {
        Text(text = "${context.getString(R.string.stats_all_games)} : $games")
        Text(text = "${context.getString(R.string.stats_games_done)} : $gamesDone")
        Text(text = "${context.getString(R.string.stats_good_answer)} : $goodAnswers")
        Text(text = "${context.getString(R.string.stats_bad_answer)} : $badAnswers")
        Text(
            text = "${context.getString(R.string.stats_winrate)} : ${
                model.winrate(
                    goodAnswers,
                    badAnswers
                )
            }%"
        )
    }
}


@Composable
fun CleanStatDialog(confirm: () -> Unit, dismiss: () -> Unit) =
    AlertDialog(onDismissRequest = dismiss,
        title = { Text(text = LocalContext.current.getString(R.string.clean_stat)) },
        text = { Text(text = LocalContext.current.getString(R.string.clean_stat_desc)) },
        confirmButton = {
            Button(onClick = confirm) { Text(text = LocalContext.current.getString(R.string.yes)) }
        },
        dismissButton = { Button(onClick = dismiss) { Text(text = LocalContext.current.getString(R.string.no)) } })
