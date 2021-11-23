package com.app.travel.flare.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.travel.flare.GalleryAdapter
import com.app.travel.flare.MainActivity
import com.app.travel.flare.R
import com.app.travel.flare.databinding.FragmentDashboardBinding
import com.app.travel.flare.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener


class DashboardFragment : Fragment(){

    lateinit var binding : FragmentDashboardBinding
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    var mLocationPermissionGranted : Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var cityName : String
    lateinit var cityInfo : String
    lateinit var cityInfoTextView : TextView

    companion object{
        const val TAG : String = "DashboardFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as AppCompatActivity)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Travel Flare"
        cityInfoTextView = binding.locationDisplayTV

        var galleryImageAdapter= GalleryAdapter(activity as MainActivity);
        binding.imageGallery.setAdapter(galleryImageAdapter);

        binding.imageGallery.setOnItemClickListener(OnItemClickListener { parent, v, position, id -> // show the selected Image
            binding.gallerySelectedImage.setImageResource(galleryImageAdapter.mImageIds[position])
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLocationPermission()
        //findCity()
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                activity as AppCompatActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            findCity()
        } else {
            ActivityCompat.requestPermissions(
                activity as AppCompatActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
            findCity()
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
                    findCity()
                }
                else {
                    activity?.finish()
                }
                Log.d(TAG, "mLocationPermissionGranted : $mLocationPermissionGranted")
            }
        }
    }

    fun findCity(){
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
                        var lat = location.latitude
                        var long = location.longitude

                        val geocoder = Geocoder(activity as AppCompatActivity, Locale.getDefault())
                        val addresses: List<Address> = geocoder.getFromLocation(lat, long, 1)
                        cityInfo  = addresses[0].getAddressLine(0)
                        cityInfoTextView.text = cityInfo
                        var result: List<String> = cityInfo.split(",").map { it.trim() }
                        Log.d("City Info:" , cityInfo)
                        cityName = result[1]
                        Log.d("City Name:" , cityName)
                        Utils.setStringData(cityInfo,Utils.CITY_INFO,activity as AppCompatActivity)
                        Utils.setStringData(cityName,Utils.CITY_NAME,activity as AppCompatActivity)
                    } else {
                    }
                }
        }
    }
}