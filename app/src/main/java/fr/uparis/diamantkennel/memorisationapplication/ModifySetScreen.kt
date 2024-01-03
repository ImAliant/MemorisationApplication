package fr.uparis.diamantkennel.memorisationapplication

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun ModifySetScreen(padding: PaddingValues, navController: NavController) {
    val context = LocalContext.current
    
    Toast.makeText(context, "Modify", Toast.LENGTH_SHORT).show()
}
