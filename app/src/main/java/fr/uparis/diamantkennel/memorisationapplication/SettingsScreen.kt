package fr.uparis.diamantkennel.memorisationapplication

import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.diamantkennel.memorisationapplication.ui.SettingsViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(model: SettingsViewModel = viewModel()) {
    val context = LocalContext.current

    var deletionDBRequest by model.deletionDB
    var cleanStatRequest by model.deletionStat
    var choiceTimeNotifRequest by model.notif
    var choiceDelayRequest by model.delayRequest

    val prefConfigTime = runBlocking { model.prefConfigTime.first() }

    val stateTime = rememberTimePickerState(
        initialHour = prefConfigTime.hour,
        initialMinute = prefConfigTime.minute,
        is24Hour = true
    )

    model.checkPermission(context)

    ShowDialog(deletionDBRequest) { DeletionDBDialog(model::deleteDb, model::dismissDeletionDB) }
    ShowDialog(cleanStatRequest) { CleanStatDialog(model::cleanStats, model::dismissDeletionStat) }
    ShowDialog(choiceTimeNotifRequest) {
        ChoiceTimeNotifDialog(
            { model.choiceTimeNotif(stateTime, context) },
            model::dismissNotif,
            stateTime
        )
    }
    ShowDialog(choiceDelayRequest) {
        ChoiceDelayDialog(
            { model.choiceDelay(it) },
            model::dismissDelayRequest
        )
    }

    Settings(
        model,
        { choiceTimeNotifRequest = true },
        { deletionDBRequest = true },
        { cleanStatRequest = true },
        { choiceDelayRequest = true },
        model::resetDelay
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Settings(
    model: SettingsViewModel = viewModel(),
    onTimeNotifButtonClick: () -> Unit,
    deletionDBRequest: () -> Unit,
    cleanStatRequest: () -> Unit,
    onDelayButtonClick: () -> Unit,
    onResetButtonClick: () -> Unit,
) {
    val context = LocalContext.current

    var permissionNotif by model.gavePermissionNow
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {
        if (it) {
            permissionNotif = true
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Stats(model = model)
        AddSpacedDivider()
        NotificationSettings(context, model, permissionNotif, permissionLauncher, onTimeNotifButtonClick)
        AddSpacedDivider()
        GestionSettings(context, deletionDBRequest, cleanStatRequest)
        AddSpacedDivider()
        GameSettings(context, onDelayButtonClick, onResetButtonClick)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationSettings(
    context: Context,
    model: SettingsViewModel,
    permissionNotif: Boolean,
    permissionLauncher: ActivityResultLauncher<String>,
    onTimeNotifButtonClick: () -> Unit,
) {
    Text(text = context.getString(R.string.notification), fontSize = 30.sp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            enabled = !permissionNotif,
            onClick = {
                model.requestNotificationPermission(permissionLauncher)
            }
        ) {
            Text(text = context.getString(R.string.permission_button))
        }
        Button(
            enabled = permissionNotif,
            onClick = onTimeNotifButtonClick
        ) {
            Text(text = context.getString(R.string.time_notif_button))
        }
    }
}

@Composable
fun GestionSettings(context: Context, deletionDBRequest: () -> Unit, cleanStatRequest: () -> Unit) {
    Text(text = context.getString(R.string.gestion), fontSize = 30.sp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = deletionDBRequest,
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.red))
        ) {
            Text(text = context.getString(R.string.main_button_deletebase))
        }

        Button(
            onClick = cleanStatRequest,
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.red))
        ) {
            Text(text = context.getString(R.string.clean_stat_button))
        }
    }
}

@Composable
fun GameSettings(context: Context, onDelayButtonClick: () -> Unit, onResetButtonClick: () -> Unit) {
    Text(text = context.getString(R.string.game), fontSize = 30.sp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Button(onClick = onDelayButtonClick) {
                Text(text = context.getString(R.string.choice_delay_button))
            }
            Button(
                onClick = onResetButtonClick,
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.red))
            ) {
                Text(text = context.getString(R.string.reset_button))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoiceTimeNotifDialog(confirm: () -> Unit, dismiss: () -> Unit, state: TimePickerState) {
    Dialog(onDismissRequest = dismiss) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(550.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.time_notif),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                )

                TimePicker(state = state, layoutType = TimePickerLayoutType.Vertical)

                Button(onClick = confirm) {
                    Text(text = LocalContext.current.getString(R.string.confirm))
                }
            }
        }
    }
}

@Composable
fun ChoiceDelayDialog(confirm: (Int) -> Unit, dismiss: () -> Unit) {
    var value by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = dismiss,
        title = { Text(text = LocalContext.current.getString(R.string.choice_delay)) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = {
                    value = if (it.toIntOrNull() != null) { it } else ""
                },
                label = { Text(text = LocalContext.current.getString(R.string.enter_integer)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = {
                if (value.isNotBlank()) { confirm(value.toInt()) } else dismiss()
            }) {
                Text(text = LocalContext.current.getString(R.string.confirm))
            }
        }
    )
}


@Composable
fun AddSpacedDivider() {
    Spacer(modifier = Modifier.padding(top = 20.dp))
    Divider(color = Color.Gray)
    Spacer(modifier = Modifier.padding(top = 20.dp))
}
