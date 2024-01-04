package fr.uparis.diamantkennel.memorisationapplication

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fr.uparis.diamantkennel.memorisationapplication.ui.PlayViewModel

@Composable
fun PlayScreen(
    padding: PaddingValues,
    navController: NavController,
    model: PlayViewModel = viewModel()
) {
    val context = LocalContext.current

    Toast.makeText(context, "Start", Toast.LENGTH_SHORT).show()
}
