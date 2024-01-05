package fr.uparis.diamantkennel.memorisationapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import fr.uparis.diamantkennel.memorisationapplication.data.QuestionsDB
import java.util.Calendar
import java.util.concurrent.TimeUnit

const val CHANNEL_ID = "MY_CHANNEL_ID"

class MemoApplication : Application() {
    val database: QuestionsDB by lazy { QuestionsDB.getDataBase(this) }

    override fun onCreate() {
        super.onCreate()
        createChannel(this)

        if (this.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            schedule()
        else
            Log.d("MemoApplication", "onCreate: no permission")
    }

    private fun createChannel(c: Context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val name = "MY_CHANNEL"
            val descriptionText = "notification channel for Memorisation project"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun schedule()
    {
        val wm = WorkManager.getInstance(this)
        wm.cancelAllWork()
        wm.enqueue(request(10, 45))
    }

    private fun request(h: Int, m: Int): PeriodicWorkRequest
    {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
        }
        if (target.before(now))
            target.add(Calendar.DAY_OF_YEAR, 1)
        val delta=target.timeInMillis - now.timeInMillis
        val request = PeriodicWorkRequest.Builder(RappelWorker::class.java, 1, TimeUnit.DAYS)
                .setInitialDelay(delta, TimeUnit.MILLISECONDS)
                .build()
        Log.d("Periodic", "request: $request")
        return request
    }
}
