package com.app.travel.flare.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.travel.flare.R
import com.app.travel.flare.databinding.FragmentReportIncidentBinding
import com.app.travel.flare.viewModel.ReportIncidentViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class ReportIncidentFragment : Fragment(){

    lateinit var binding : FragmentReportIncidentBinding
    lateinit var viewModel : ReportIncidentViewModel
    var incidentList : ArrayList<String> = ArrayList()
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    var mLocationPermissionGranted : Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_incident, container, false)

        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Report Incident"

        viewModel = ViewModelProvider(this).get(ReportIncidentViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        getLocationPermission()
        createList()
        setUpSpinner()

        var cityName = "Sunnyvale"
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

        viewModel.reportIncidentLiveData.observe(activity as AppCompatActivity,
            Observer<Boolean> { aBoolean ->
                if (aBoolean) {
                    Log.d("ReportIncidentFragment", "Incident reported successfully")
                    Toast.makeText(activity, "Incident reported successfully", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        activity,
                        "Incident reporting failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

        binding.reportBtn.setOnClickListener{
            reportIncident()
        }

        return binding.root
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                activity as AppCompatActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Log.d("TAG", "mLocationPermissionGranted : $mLocationPermissionGranted")
        } else {
            ActivityCompat.requestPermissions(
                activity as AppCompatActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
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
//                } else {
//                    finish()
//                }
                    Log.d("TAG", "mLocationPermissionGranted : $mLocationPermissionGranted")
                }
            }
        }
    }

    private fun setUpSpinner() {
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            activity as AppCompatActivity,
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

    fun reportIncident() {
        val incident = incidentList.get(binding.incidentSpinner.selectedItemPosition)
        if (ActivityCompat.checkSelfPermission(activity as AppCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity as AppCompatActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity as AppCompatActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
        if(mLocationPermissionGranted) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(activity as AppCompatActivity) { location ->
                    if (location != null) {
                        Log.d("TAG", "Location returned.")
                        var lat = ""+location.latitude
                        var long = ""+location.longitude
                        viewModel.reportIncident(incident, lat, long)
                    } else {
                        Log.d("TAG", "Null location returned.")
                    }
                }
        }
    }
}
