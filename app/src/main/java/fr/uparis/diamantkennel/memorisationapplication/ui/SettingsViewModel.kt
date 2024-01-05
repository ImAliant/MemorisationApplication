package fr.uparis.diamantkennel.memorisationapplication.ui

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.diamantkennel.memorisationapplication.MemoApplication
import fr.uparis.diamantkennel.memorisationapplication.STATS_TOTAL_BAD
import fr.uparis.diamantkennel.memorisationapplication.STATS_TOTAL_DONE
import fr.uparis.diamantkennel.memorisationapplication.STATS_TOTAL_GOOD
import fr.uparis.diamantkennel.memorisationapplication.STATS_TOTAL_TRIED
import fr.uparis.diamantkennel.memorisationapplication.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as MemoApplication).database.memoDao()

    private val stats = application.dataStore
    private val statsKeyTotal = intPreferencesKey(STATS_TOTAL_TRIED)
    private val statsKeyTotalDone = intPreferencesKey(STATS_TOTAL_DONE)
    private val statsKeyTotalGood = intPreferencesKey(STATS_TOTAL_GOOD)
    private val statsKeyTotalBad = intPreferencesKey(STATS_TOTAL_BAD)

    val statTotal = stats.data.map { it[statsKeyTotal] ?: 0 }
    val statTotalDone = stats.data.map { it[statsKeyTotalDone] ?: 0 }
    val statTotalGood = stats.data.map { it[statsKeyTotalGood] ?: 0 }
    val statTotalBad = stats.data.map { it[statsKeyTotalBad] ?: 0 }

    val deletionDB = mutableStateOf(false)
    val deletionStat = mutableStateOf(false)

    fun deleteDb() {
        deletionDB.value = false
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteTable()
        }
    }

    fun cleanStats() {
        deletionStat.value = false
        viewModelScope.launch {
            stats.edit {
                it[statsKeyTotal] = 0
                it[statsKeyTotalDone] = 0
                it[statsKeyTotalGood] = 0
                it[statsKeyTotalBad] = 0
            }
        }
    }

    fun winrate(good: Int, bad: Int): Int {
        val total = good + bad
        if (total == 0) {
            return 0
        }
        return ((good.toFloat() / total.toFloat()) * 100).toInt()
    }

    fun requestNotificationPermission(launcher: ActivityResultLauncher<String>)
    {
        launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    fun isNotGranted(context: Context): Boolean
    {
        return context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    }
}
