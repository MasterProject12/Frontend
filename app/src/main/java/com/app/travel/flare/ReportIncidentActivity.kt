package com.app.travel.flare

import android.Manifest
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

class ReportIncidentActivity : AppCompatActivity(), OnCheckedChangeListener, View.OnClickListener {

    lateinit var binding : ActivityReportIncidentBinding
    var incidentList : ArrayList<String> = ArrayList()
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    var mLocationPermissionGranted : Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var viewModel : ReportIncidentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_incident)
        binding.locationSwitchBtn.setOnCheckedChangeListener(this)
        binding.reportBtn.setOnClickListener(this)
        viewModel = ViewModelProvider(this).get(ReportIncidentViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        if(isChecked){
            Log.d(TAG, "Enable location permission requested");
        }else{
            Log.d(TAG, "Disable location permission requested");
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
        incidentList.add("Road Accident")
        incidentList.add("Road Construction")
        incidentList.add("Bad conditioned road")
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
            var result = fusedLocationClient.lastLocation.result
            var lat = result.latitude
            var long = result.longitude
            viewModel.reportIncident(incident, lat, long)
        }
    }
}