package com.app.travel.flare

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.travel.flare.utils.Utils
import com.app.travel.flare.viewModel.HomeActivityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Geocoder
import android.widget.*
import com.app.travel.flare.utils.HandleAlertListener
import com.app.travel.flare.utils.MyAlertDialog
import com.app.travel.flare.utils.MyProgressDialog
import java.util.*

class HomeActivity : AppCompatActivity(), HandleAlertListener {

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    var mLocationPermissionGranted : Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel : HomeActivityViewModel
    private lateinit var alertDialog: MyAlertDialog
    private lateinit var progressDialog : MyProgressDialog
    private lateinit var cityInfoTextView : TextView
    private lateinit var cityName : String
    private lateinit var cityInfo : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        getLocationPermission()

        (this as AppCompatActivity?)!!.supportActionBar!!.title = "Dashboard"

        findViewById<RelativeLayout>(R.id.trackSpeedRL).setOnClickListener{
//            var intent = Intent(this, SpeedometerActivity::class.java)
//            startActivity(intent)

            var intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        findViewById<RelativeLayout>(R.id.relativeLayout).setOnClickListener{
            showAlertDialog()
        }
        findViewById<RelativeLayout>(R.id.reportRL).setOnClickListener{
            var intent = Intent(this, ReportIncidentActivity::class.java)
            intent.putExtra("CityName" , cityInfo)
            startActivity(intent)
        }

        cityInfoTextView = findViewById(R.id.locationInfoTV)

        Utils.cacheData(true,Utils.IS_LOGGED_IN,this)
        findCity()

        viewModel.subscribeCityLiveData.observe(this, {
                aBoolean ->
            if (aBoolean) {
                Toast.makeText(
                    this,
                    "Location has been  shared successfully",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Location sharing failed. Please try again later.",
                    Toast.LENGTH_LONG
                ).show()
            }
            progressDialog.dismissProgressDialog()
        })
    }

    private fun showAlertDialog(){
        alertDialog = MyAlertDialog()
        alertDialog.showAlertDialog(this, getString(R.string.location_share_title), getString(R.string.location_share_msg ),this)
    }

    private fun findCity(){
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
                        Log.d(ReportIncidentActivity.TAG, "Location returned.")
                        var lat = location.latitude
                        var long = location.longitude

                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses: List<Address> = geocoder.getFromLocation(lat, long, 1)
                        cityInfo  = addresses[0].getAddressLine(0)

                        cityInfoTextView.text = cityInfo

                        var result: List<String> = cityInfo.split(",").map { it.trim() }

                        this@HomeActivity.cityName = result[1]
//                        Log.d("HomeActivity", "City name: " + result[result.size -3])
//                        viewModel.subscribe(city)
                    } else {
                        Log.d(ReportIncidentActivity.TAG, "Null location returned.")
                    }
                }
        }
    }

    override fun handlePositiveBtn() {
        alertDialog.dismissAlertDialog()
        progressDialog = MyProgressDialog()
        progressDialog.showProgressDialog(this, "Sending...Please wait","")

        val cityName = cityInfoTextView.text.toString()
        var result: List<String> = cityName.split(",").map { it.trim() }
        var city = result[1]
        viewModel.subscribe(city)
    }

    override fun handleNegativeBtn() {
        alertDialog.dismissAlertDialog()
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