package com.app.travel.flare.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.app.travel.flare.MainActivity

const val INCIDENT_DETAIL_MESSAGE = "INCIDENT_DETAIL_MESSAGE"
const val INCIDENT_PUSH = "INCIDENT_PUSH"
const val GEO_INCIDENT_DATA = "GEO_INCIDENT_DATA"

class IncomingNotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if(intent != null){
            val incidentMessage = intent.getStringExtra(GEO_INCIDENT_DATA)
            if(isAppVisible()){
                val activityIntent = Intent(this, MainActivity::class.java)
                activityIntent.action = intent!!.action
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activityIntent.putExtra(INCIDENT_DETAIL_MESSAGE, incidentMessage)
                startActivity(activityIntent)
            }else if(incidentMessage != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    sendOreoNotification(incidentMessage)
                }
                else
                {
                    sendNotification(incidentMessage)
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun sendNotification(incidentDetail: String)
    {
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putString("incidentDetail", incidentDetail)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val buider: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setContentTitle("New Road Alert").setContentText(incidentDetail)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        noti.notify(100, buider.build())

    }

    private fun sendOreoNotification(incidentDetail: String) {

        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putString("incidentDetail", incidentDetail)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val oreoNotification = OreoNotification(this)

        val buider: Notification.Builder = oreoNotification.getOreoNotification("New Road Alert", incidentDetail, pendingIntent, defaultSound, null)
        oreoNotification.getManager!!.notify(100, buider.build())
    }


    companion object {
        val TAG : String = IncomingNotificationService::class.java.name
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }

    private fun isAppVisible(): Boolean {
        return ProcessLifecycleOwner
            .get()
            .lifecycle
            .currentState
            .isAtLeast(Lifecycle.State.STARTED)
    }
}