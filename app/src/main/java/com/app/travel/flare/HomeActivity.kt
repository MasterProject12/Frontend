package com.app.travel.flare

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.flare.speedometer.LocationService
import com.app.travel.flare.speedometer.LocationService.LocalBinder
import com.app.travel.flare.utils.Utils

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
        Utils.cacheData(true,Utils.IS_LOGGED_IN,this)
        //bindService()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_items,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logoutBtn){
            Utils.cacheData(false,Utils.IS_LOGGED_IN,this)
            var intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun Context.handleSpeed() {
//            var intent = Intent(this, NotifyIncidentActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
        }
    }

//    var myService: LocationService? = null
//    private val sc: ServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName, service: IBinder) {
//            val binder = service as LocalBinder
//            myService = binder.service
//            SpeedometerActivity.status = true
//        }
//
//        override fun onServiceDisconnected(name: ComponentName) {
//            SpeedometerActivity.status = false
//        }
//    }
//
//    fun bindService() {
//        if (SpeedometerActivity.status) return
//        val i = Intent(applicationContext, LocationService::class.java)
//        bindService(i, sc, BIND_AUTO_CREATE)
//        SpeedometerActivity.status = true
//        SpeedometerActivity.startTime = System.currentTimeMillis()
//    }
//
//    fun unbindService() {
//        if (SpeedometerActivity.status == false) return
//        val i = Intent(applicationContext, LocationService::class.java)
//        unbindService(sc)
//        SpeedometerActivity.status = false
//    }
//
//    override fun onResume() {
//        super.onResume()
//    }
//
//    override fun onStart() {
//        super.onStart()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if (SpeedometerActivity.status == true) unbindService()
//    }
//
//    override fun onBackPressed() {
//        if (SpeedometerActivity.status == false) super.onBackPressed() else moveTaskToBack(true)
//    }
}