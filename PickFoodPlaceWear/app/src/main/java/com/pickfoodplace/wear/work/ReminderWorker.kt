package com.pickfoodplace.wear.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pickfoodplace.wear.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val bookingId = inputData.getString(KEY_BOOKING_ID) ?: return Result.failure()
        val placeName = inputData.getString(KEY_PLACE_NAME) ?: ""
        val slot = inputData.getString(KEY_SLOT) ?: ""
        showNotification("Upcoming booking", "$placeName at $slot")
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CHANNEL_ID, "Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(ch)
        }
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        nm.notify(NOTIF_ID, notification)
    }

    companion object {
        const val KEY_BOOKING_ID = "booking_id"
        const val KEY_PLACE_NAME = "place_name"
        const val KEY_SLOT = "slot"
        private const val CHANNEL_ID = "reminders"
        private const val NOTIF_ID = 1001
    }
}