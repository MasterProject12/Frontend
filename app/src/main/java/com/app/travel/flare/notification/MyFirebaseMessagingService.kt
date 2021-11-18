package com.app.travel.flare.notification

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "onMessageReceived $remoteMessage")
        Log.d(TAG, "priority: " + remoteMessage.getPriority())

        val data: Map<String, String> = remoteMessage.getData()
        val intent = Intent(this, IncomingNotificationService::class.java)
//        intent.action = Constants.PUSH
//        intent.putExtra(Constants.PUSH_TITLE, data["title"])
//        intent.putExtra(Constants.PUSH_MESSAGE, data["message"])
//        intent.putExtra(Constants.SEGMENT_NAME, data["segment_name"])
        startService(intent)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    companion object{
        const val TAG = "MyFirebaseMessagingService"
    }
}