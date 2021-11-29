package com.app.travel.flare.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.travel.flare.R
import com.app.travel.flare.databinding.FragmentLocationShareBinding
import com.app.travel.flare.network.Networking
import com.app.travel.flare.utils.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import java.text.MessageFormat

class LocationShareFragment : Fragment(), HandleAlertListener {

    lateinit var binding : FragmentLocationShareBinding
    private lateinit var alertDialog: MyAlertDialog
    private lateinit var progressDialog : MyProgressDialog
    private lateinit var token : String
    private lateinit var subscribeTV : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location_share, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Location Share"
        subscribeTV = binding.subscribeTV
        subscribeTV.visibility = View.VISIBLE
        fetchToken()
        showAlertDialog()

        val cityName = Utils.getStringData(Utils.CITY_NAME,activity as AppCompatActivity)
        subscribeTV.text = String.format(getString(R.string.subscribe_text), cityName)

        return binding.root
    }

    private fun showAlertDialog(){
        alertDialog = MyAlertDialog()
        alertDialog.showAlertDialog(activity as AppCompatActivity,
            getString(R.string.location_share_title), getString(R.string.location_share_msg ),this)
    }

    override fun handlePositiveBtn() {
        alertDialog.dismissAlertDialog()
        progressDialog = MyProgressDialog()
        progressDialog.showProgressDialog(activity as AppCompatActivity, "Sending...Please wait","")

        val cityName = Utils.getStringData(Utils.CITY_NAME,activity as AppCompatActivity)
        subscribe(cityName)
    }

    override fun handleNegativeBtn() {
        alertDialog.dismissAlertDialog()
    }

    fun subscribe(city: String?) {
        val url = "https://1bgkyjhncb.execute-api.us-west-2.amazonaws.com/prod/subscribe"
        val jsonObj = JsonObject()
        jsonObj.addProperty("city",city)
        jsonObj.addProperty("token",token)

        var map = HashMap<String,String>()
        map["Content-Type"] = "application/json"

        Networking.INSTANCE.asyncConnection(
            url,
            HttpMethods.POST.name,
            jsonObj.toString(),
            map,
            object : Networking.ResponseHandler {
                override fun onSuccess(response: String?) {
                    progressDialog.dismissProgressDialog()
                }

                override fun onFailure(failureType: Networking.ResponseHandler.FailureType?, responseCode: Int, reason: String?) {
                    progressDialog.dismissProgressDialog()
                }
            })
    }

    fun fetchToken(){
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    token = task.result
                    Log.d("FirebaseMessaging", "Firebase Token: $token")
                }
            })
    }
}