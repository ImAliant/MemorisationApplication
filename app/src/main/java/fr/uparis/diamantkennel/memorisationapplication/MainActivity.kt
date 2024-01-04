package fr.uparis.diamantkennel.memorisationapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.uparis.diamantkennel.memorisationapplication.ui.theme.MemorisationApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemorisationApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenMainActivity()
                }
            }
        }
    }
}

@Composable
fun MainScreenMainActivity() {
    MainScreen()
}

@Preview(showBackground = true)
@Composable
fun MainScreenActivityPreview() {
    MainScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(topBar = { TopBar() }, bottomBar = { BottomBar(navController) }) { padding ->
        NavHost(
            navController = navController,
            startDestination = HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(HOME) { HomeScreen(padding, navController) }
            composable("$MODIFY_SET/{$MODIFY_SET_ARGS}") {
                it.arguments?.getString(MODIFY_SET_ARGS)?.let { idSet ->
                    ModifySetScreen(
                        padding,
                        idSet.toInt()
                    )
                }
            }
            composable("$PLAY/{$PLAY_SET_ARG}") {
                it.arguments?.getString(PLAY_SET_ARG)?.let { idSet ->
                    PlayScreen(
                        padding,
                        navController,
                        idSet.toInt()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() = CenterAlignedTopAppBar(title = {
    Text(
        text = LocalContext.current.getString(R.string.app_name),
        style = MaterialTheme.typography.displayMedium
    )
})

@Composable
fun BottomBar(navController: NavHostController) =
    BottomNavigation(backgroundColor = MaterialTheme.colorScheme.primaryContainer) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        BottomNavigationItem(selected = currentRoute == HOME, onClick = {
            navController.navigate(HOME) { launchSingleTop = true }
        }, icon = {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = LocalContext.current.getString(R.string.home_button)
            )
        })
    }
