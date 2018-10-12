package netzd.org.notificacionphp.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import netzd.org.notificacionphp.MainActivity
import netzd.org.notificacionphp.R
import netzd.org.notificacionphp.utils.NotificationUtils
import netzd.org.notificacionphp.vo.NotificationVO

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        Log.d(TAG, "From: " + remoteMessage!!.from!!)

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            val data = remoteMessage.data
            handleData(data)

        } else if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
            handleNotification(remoteMessage.notification!!)
        }// Check if message contains a notification payload.

    }

    private fun handleNotification(RemoteMsgNotification: RemoteMessage.Notification) {
        val message = RemoteMsgNotification.body
        val title = RemoteMsgNotification.title
        val notificationVO = NotificationVO()
        notificationVO.title = title
        notificationVO.message = message

        val resultIntent = Intent(applicationContext, MainActivity::class.java)
        val notificationUtils = NotificationUtils(applicationContext)
        notificationUtils.displayNotification(notificationVO, resultIntent)
        notificationUtils.playNotificationSound()
    }

    private fun handleData(data: Map<String, String>) {
        val title = data[TITLE]
        val message = data[MESSAGE]
        val iconUrl = data[IMAGE]
        val action = data[ACTION]
        val actionDestination = data[ACTION_DESTINATION]
        val notificationVO = NotificationVO()
        notificationVO.title = title
        notificationVO.message = message
        notificationVO.iconUrl = iconUrl
        notificationVO.action = action
        notificationVO.actionDestination = actionDestination

        val resultIntent = Intent(applicationContext, MainActivity::class.java)

        val notificationUtils = NotificationUtils(applicationContext)
        notificationUtils.displayNotification(notificationVO, resultIntent)
        notificationUtils.playNotificationSound()

        /*NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationVO.getTitle())
                .setContentText(notificationVO.getMessage())
                .setAutoCancel(false);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());*/

    }

    companion object {
        private val TAG = "MyFirebaseMsgingService"
        private val TITLE = "title"
        private val EMPTY = ""
        private val MESSAGE = "message"
        private val IMAGE = "image"
        private val ACTION = "action"
        private val DATA = "data"
        private val ACTION_DESTINATION = "action_destination"
    }
}
