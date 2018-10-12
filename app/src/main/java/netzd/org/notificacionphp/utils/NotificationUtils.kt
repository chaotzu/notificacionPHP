package netzd.org.notificacionphp.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.text.Html

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap

import netzd.org.notificacionphp.MainActivity
import netzd.org.notificacionphp.R
import netzd.org.notificacionphp.SecondActivity
import netzd.org.notificacionphp.vo.NotificationVO

class NotificationUtils(private val mContext: Context) {
    internal var activityMap: MutableMap<String, Class<*>> = HashMap()

    init {
        //Populate activity map
        activityMap["MainActivity"] = MainActivity::class.java
        activityMap["SecondActivity"] = SecondActivity::class.java
    }

    /**
     * Displays notification based on parameters
     *
     * @param notificationVO
     * @param resultIntent
     */
    fun displayNotification(notificationVO: NotificationVO, resultIntent: Intent) {
        var resultIntent = resultIntent
        run {
            val message = notificationVO.message
            val title = notificationVO.title
            val iconUrl = notificationVO.iconUrl
            val action = notificationVO.action
            val destination = notificationVO.actionDestination
            var iconBitMap: Bitmap? = null
            if (iconUrl != null) {
                iconBitMap = getBitmapFromURL(iconUrl)
            }
            val icon = R.mipmap.ic_launcher

            val resultPendingIntent: PendingIntent

            if (URL == action) {
                val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(destination))

                resultPendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0)
            } else if (ACTIVITY == action && activityMap.containsKey(destination)) {
                resultIntent = Intent(mContext, activityMap[destination])

                resultPendingIntent = PendingIntent.getActivity(
                        mContext,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                )
            } else {
                resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                resultPendingIntent = PendingIntent.getActivity(
                        mContext,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                )
            }


            val mBuilder = NotificationCompat.Builder(
                    mContext, CHANNEL_ID)

            val notification: Notification

            if (iconBitMap == null) {
                //When Inbox Style is applied, user can expand the notification
                val inboxStyle = NotificationCompat.InboxStyle()

                inboxStyle.addLine(message)
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(resultPendingIntent)
                        .setStyle(inboxStyle)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
                        .setContentText(message)
                        .build()

            } else {
                //If Bitmap is created from URL, show big icon
                val bigPictureStyle = NotificationCompat.BigPictureStyle()
                bigPictureStyle.setBigContentTitle(title)
                bigPictureStyle.setSummaryText(Html.fromHtml(message).toString())
                bigPictureStyle.bigPicture(iconBitMap)
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(resultPendingIntent)
                        .setStyle(bigPictureStyle)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
                        .setContentText(message)
                        .build()
            }

            val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //Para que se muestre en Android Oreo
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel(
                        CHANNEL_ID, "Test", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(mChannel)
            }

            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    /**
     * Downloads push notification image before displaying it in
     * the notification tray
     *
     * @param strURL : URL of the notification Image
     * @return : BitMap representation of notification Image
     */
    private fun getBitmapFromURL(strURL: String): Bitmap? {
        try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * Playing notification sound
     */
    fun playNotificationSound() {
        try {
            val alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.packageName + "/raw/notification")
            val r = RingtoneManager.getRingtone(mContext, alarmSound)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {
        private val NOTIFICATION_ID = 201
        private val PUSH_NOTIFICATION = "pushNotification"
        private val CHANNEL_ID = "myChannelPHP"
        private val URL = "url"
        private val ACTIVITY = "activity"
    }
}
