package fr.uparis.diamantkennel.memorisationapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import fr.uparis.diamantkennel.memorisationapplication.data.QuestionsDB

const val CHANNEL_ID = "REMINDERS"

class MemoApplication : Application() {
    val database: QuestionsDB by lazy { QuestionsDB.getDataBase(this) }

    override fun onCreate() {
        super.onCreate()
        createChannel(this)
    }

    private fun createChannel(c: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = c.getString(R.string.notif_channel_name)
            val descriptionText = c.getString(R.string.notif_channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
