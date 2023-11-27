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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    Column(
        modifier = Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShowList(setOfQuestions, currentSelection)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { Toast.makeText(context, "Create", Toast.LENGTH_SHORT).show() }) {
                Text(text = context.getString(R.string.main_button_create))
            }
            Button(onClick = { Toast.makeText(context, "Modify", Toast.LENGTH_SHORT).show() }) {
                Text(text = context.getString(R.string.main_button_modify))
            }
            Button(onClick = { Toast.makeText(context, "Import", Toast.LENGTH_SHORT).show() }) {
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
