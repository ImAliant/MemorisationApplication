package fr.uparis.diamantkennel.memorisationapplication.ui

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import fr.uparis.diamantkennel.memorisationapplication.HOUR
import fr.uparis.diamantkennel.memorisationapplication.MINUTE
import fr.uparis.diamantkennel.memorisationapplication.MemoApplication
import fr.uparis.diamantkennel.memorisationapplication.RappelWorker
import fr.uparis.diamantkennel.memorisationapplication.STATS_TOTAL_BAD
import fr.uparis.diamantkennel.memorisationapplication.STATS_TOTAL_DONE
import fr.uparis.diamantkennel.memorisationapplication.STATS_TOTAL_GOOD
import fr.uparis.diamantkennel.memorisationapplication.STATS_TOTAL_TRIED
import fr.uparis.diamantkennel.memorisationapplication.TimeConfig
import fr.uparis.diamantkennel.memorisationapplication.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as MemoApplication).database.memoDao()

    private val stats = application.dataStore
    private val statsKeyTotal = intPreferencesKey(STATS_TOTAL_TRIED)
    private val statsKeyTotalDone = intPreferencesKey(STATS_TOTAL_DONE)
    private val statsKeyTotalGood = intPreferencesKey(STATS_TOTAL_GOOD)
    private val statsKeyTotalBad = intPreferencesKey(STATS_TOTAL_BAD)

    private val notifH = intPreferencesKey(HOUR)
    private val notifM = intPreferencesKey(MINUTE)

    val statTotal = stats.data.map { it[statsKeyTotal] ?: 0 }
    val statTotalDone = stats.data.map { it[statsKeyTotalDone] ?: 0 }
    val statTotalGood = stats.data.map { it[statsKeyTotalGood] ?: 0 }
    val statTotalBad = stats.data.map { it[statsKeyTotalBad] ?: 0 }

    val deletionDB = mutableStateOf(false)
    val deletionStat = mutableStateOf(false)
    var notif = mutableStateOf(false)

    val gavePermissionNow = mutableStateOf(false)

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

    @OptIn(ExperimentalMaterial3Api::class)
    fun choiceTimeNotif(state: TimePickerState, context: Context) {
        notif.value = false
        val newConfig = TimeConfig(
            state.hour,
            state.minute
        )
        Log.d("Periodic", "config=$newConfig")
        save(newConfig)
        schedule(newConfig, context)
    }

    fun winrate(good: Int, bad: Int): Int {
        val total = good + bad
        if (total == 0) {
            return 0
        }
        return ((good.toFloat() / total.toFloat()) * 100).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission(launcher: ActivityResultLauncher<String>) {
        launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermission(context: Context) {
        gavePermissionNow.value =
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    fun save(config: TimeConfig) {
        viewModelScope.launch {
            stats.edit {
                it[notifH] = config.hour
                it[notifM] = config.minute
            }
        }
    }

    val prefConfig = stats.data.map {
        TimeConfig(
            it[notifH] ?: 8,
            it[notifM] ?: 0
        )
    }

    private fun schedule(config: TimeConfig, context: Context) {
        val wm = WorkManager.getInstance(context)
        wm.cancelAllWork()
        wm.enqueue(request(config.hour, config.minute))
    }

    private fun request(h: Int, m: Int): PeriodicWorkRequest {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
        }
        if (target.before(now))
            target.add(Calendar.DAY_OF_YEAR, 1)
        val delta = target.timeInMillis - now.timeInMillis
        val request = PeriodicWorkRequest.Builder(RappelWorker::class.java, 1, TimeUnit.DAYS)
            .setInitialDelay(delta, TimeUnit.MILLISECONDS)
            .build()
        Log.d("Periodic", "request: $request")
        return request
    }
}
