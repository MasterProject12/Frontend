package com.app.travel.flare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.travel.flare.databinding.ActivityReportIncidentBinding
import com.app.travel.flare.viewModel.ReportIncidentViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

open class ReportIncidentActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding : ActivityReportIncidentBinding
    var incidentList : ArrayList<String> = ArrayList()
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    var mLocationPermissionGranted : Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var viewModel : ReportIncidentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_incident)
        binding.reportBtn.setOnClickListener(this)
        viewModel = ViewModelProvider(this).get(ReportIncidentViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        (this as AppCompatActivity?)!!.supportActionBar!!.title = "Report Incident"

        viewModel.reportIncidentLiveData.observe(this,
            Observer<Boolean> { aBoolean ->
                if (aBoolean) {
                    Log.d(TAG, "Incident reported successfully")
                    Toast.makeText(this, "Incident reported successfully", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        this,
                        "Incident reporting failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

        getLocationPermission()
        createList()
        setUpSpinner()
        var cityName : String = ""
        if (intent != null) {
            if (intent.getStringExtra("CityName") != null) {
                cityName = intent.getStringExtra("CityName")!!
            }
        }
        binding.shareFab.setOnClickListener{
            val data = "An incident happened at : $cityName"
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, data)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Log.d(MainActivity.TAG, "mLocationPermissionGranted : $mLocationPermissionGranted")
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true
                } else {
                    finish()
                }
                Log.d(MainActivity.TAG, "mLocationPermissionGranted : $mLocationPermissionGranted")
            }
        }
    }

    private fun setUpSpinner() {
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            incidentList
        )
        binding.incidentSpinner.setAdapter(arrayAdapter)
    }

    private fun createList(){
        incidentList.add("Car Accident")
        incidentList.add("Car Breakdown")
        incidentList.add("Road Blockage")
        incidentList.add("Construction Site")
    }

    companion object{
        val TAG: String = ReportIncidentActivity::class.java.name
    }

    override fun onClick(view: View?) {
        val incident = incidentList.get(binding.incidentSpinner.selectedItemPosition)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
        if(mLocationPermissionGranted) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(this) { location ->
                    if (location != null) {
                        Log.d(TAG, "Location returned.")
                        var lat = ""+location.latitude
                        var long = ""+location.longitude
                        viewModel.reportIncident(incident, lat, long)
                    } else {
                        Log.d(TAG, "Null location returned.")
                    }
                }
            //var result = fusedLocationClient.lastLocation.result
        }
    }
}