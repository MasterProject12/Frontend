package com.app.travel.flare.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.app.travel.flare.MainActivity

const val INCIDENT_DETAIL_MESSAGE = "INCIDENT_DETAIL_MESSAGE"
const val INCIDENT_PUSH = "INCIDENT_PUSH"

class IncomingNotificationService : Service() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if(intent != null){
            if(isAppVisible()){
                val activityIntent = Intent(this, MainActivity::class.java)
                activityIntent.action = intent!!.action
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activityIntent.putExtra(INCIDENT_DETAIL_MESSAGE, "New Road Incident Reported")
                startActivity(activityIntent)
            }else{

            }
        }
        return START_NOT_STICKY
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