package com.app.travel.flare

import android.Manifest
import android.R.attr
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.travel.flare.databinding.FragmentReportIncidentBinding
import com.app.travel.flare.databinding.IncidentReportDetailBinding
import com.app.travel.flare.viewModel.ReportIncidentViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.core.app.ActivityCompat.startActivityForResult

import android.provider.MediaStore
import com.app.travel.flare.utils.Utils
import android.R.attr.data

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.app.travel.flare.utils.MyAlertDialog
import com.app.travel.flare.utils.MyProgressDialog


const val SELECTED_INCIDENT_TYPE = "SELECTED_INCIDENT_TYPE"
class IncidentReportActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding : IncidentReportDetailBinding
    var incidentList : ArrayList<String> = ArrayList()
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    var mLocationPermissionGranted : Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var viewModel : ReportIncidentViewModel
    var incidentType : String = ""
    var PIC_ID = 123
    lateinit var capturedIV : ImageView
    lateinit var progressDialog : MyProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.incident_report_detail)
        binding.reportBtn.setOnClickListener(this)
        viewModel = ViewModelProvider(this).get(ReportIncidentViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        (this as AppCompatActivity?)!!.supportActionBar!!.title = "Report Incident"

        capturedIV = binding.capturedIV
        capturedIV.visibility = View.GONE
        progressDialog = MyProgressDialog()
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

                progressDialog.dismissProgressDialog()
            })

        getLocationPermission()
        createList()
        setUpSpinner()
        var cityName : String = ""
        if (intent != null) {
            if (intent.getStringExtra("CityName") != null) {
                cityName = intent.getStringExtra("CityName")!!
            }
            if (intent.getStringExtra(SELECTED_INCIDENT_TYPE) != null){
                incidentType = intent.getStringExtra(SELECTED_INCIDENT_TYPE)!!
                binding.incidentHeader.text = incidentType
            }
        }

        binding.cameraIV.setOnClickListener{
            val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camera_intent, PIC_ID)
        }


        binding.shareFab.setOnClickListener{
            var city = Utils.getStringData(Utils.CITY_INFO, this)
            val data = "An incident happened at : $city"
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
            Log.d(TAG, "mLocationPermissionGranted : $mLocationPermissionGranted")
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === PIC_ID) {

            if (data != null) {
                var bitmap : Bitmap =(data.extras?.get("data") as Bitmap)
                capturedIV.visibility = View.VISIBLE
                capturedIV.setImageBitmap(bitmap)
            }
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
                Log.d(TAG, "mLocationPermissionGranted : $mLocationPermissionGranted")
            }
        }
    }

    private fun setUpSpinner() {
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            incidentList
        )

        //var adapter = IncidentTypeAdapter(this, 111 , incidentList)

       // binding.incidentSpinner.setAdapter(arrayAdapter)
    }

    private fun createList(){
        incidentList.add("Car Accident")
        incidentList.add("Car Breakdown")
        incidentList.add("Road Blockage")
        incidentList.add("Construction Site")
    }

    companion object{
        val TAG: String = IncidentReportActivity::class.java.name
    }

    override fun onClick(view: View?) {
        //val incident = incidentList.get(binding.incidentSpinner.selectedItemPosition)
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

                        progressDialog.showProgressDialog(this, "Reporting...Please wait","")
                        viewModel.reportIncident(incidentType, lat, long)
                    } else {
                        Log.d(TAG, "Null location returned.")
                    }
                }
            //var result = fusedLocationClient.lastLocation.result
        }
    }
}