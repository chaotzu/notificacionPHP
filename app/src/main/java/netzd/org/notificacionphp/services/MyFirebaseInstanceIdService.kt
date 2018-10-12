package netzd.org.notificacionphp.services

import android.util.Log

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessaging

class MyFirebaseInstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)

        // now subscribe to `global` topic to receive app wide notifications
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        sendRegistrationToServer(refreshedToken)
    }

    /**
     * Persist token to third-party servers.
     *
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }

    companion object {
        private val TAG = "MyFirebaseIdService"
        private val TOPIC_GLOBAL = "global"
    }
}

