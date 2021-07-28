package com.app.travel.flare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.flare.utils.MyAlertDialog
import com.app.travel.flare.utils.NotifyIncidentListener

class NotifyIncidentActivity : AppCompatActivity() , NotifyIncidentListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify_incident)

        MyAlertDialog().showAlertDialog(this, getString(R.string.incident_notify_title), getString(R.string.incident_notify_msg ),this)
    }

    override fun handlePositiveBtn() {
        var intent = Intent(this, ReportIncidentActivity::class.java)
        startActivity(intent)
    }

    override fun handleNegativeBtn() {
        finish()
    }
}