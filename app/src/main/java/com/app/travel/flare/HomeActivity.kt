package com.app.travel.flare

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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
        Utils.cacheData(true,this);
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_items,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logoutBtn){
            Utils.cacheData(false,this)
            var intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }


        return super.onOptionsItemSelected(item)

    }
}