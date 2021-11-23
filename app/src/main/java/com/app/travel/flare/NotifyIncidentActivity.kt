package com.app.travel.flare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.flare.utils.MyAlertDialog
import com.app.travel.flare.utils.HandleAlertListener

import android.util.Log
import com.app.travel.flare.utils.Utils

class NotifyIncidentActivity : AppCompatActivity() , HandleAlertListener{

    companion object{
        var TAG : String = NotifyIncidentActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify_incident)

        Log.d(TAG , "Incident Detected, Showing Alert")

        var time = System.currentTimeMillis()/1000
        Utils.setLastReportedTime( time, Utils.REPORTED_TIME,this )
        MyAlertDialog().showAlertDialog(this, getString(R.string.incident_notify_title), getString(R.string.incident_notify_msg ),this)
    }

    override fun handlePositiveBtn() {
//        var intent = Intent(this, ReportIncidentActivity::class.java)
//        startActivity(intent)
    }

    override fun handleNegativeBtn() {
        finish()
    }
}