package com.app.travel.flare

import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.travel.flare.databinding.ActivityReportIncidentBinding

class ReportIncidentActivity : AppCompatActivity(), OnCheckedChangeListener {

    lateinit var binding : ActivityReportIncidentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_incident)
        binding.locationSwitchBtn.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        if(isChecked){
            Log.d(TAG, "Enable location permission requested");
        }else{
            Log.d(TAG, "Disable location permission requested");
        }

    }

    companion object{
        val TAG: String = ReportIncidentActivity::class.java.name
    }
}