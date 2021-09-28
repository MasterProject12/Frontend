package com.app.travel.flare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        findViewById<Button>(R.id.reportButton).setOnClickListener{
            var intent = Intent(this, ReportIncidentActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.speedoMeterButton).setOnClickListener{
            var intent = Intent(this, SpeedometerActivity::class.java)
            startActivity(intent)
        }
    }
}