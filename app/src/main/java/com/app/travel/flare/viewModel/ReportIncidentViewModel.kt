package com.app.travel.flare.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.travel.flare.network.Networking
import com.app.travel.flare.utils.HttpMethods
import com.google.gson.JsonObject

class ReportIncidentViewModel : ViewModel() {

    var reportIncidentLiveData = MutableLiveData<Boolean>()

    fun reportIncident(incidentData: String?, lat: String, long: String?) {
        //val url = "http://ec2-54-187-127-92.us-west-2.compute.amazonaws.com:8080/incident/add"
        Log.d("Lat, Long: ", "$lat $long")
        val url = "http://52.12.113.171:8080/incident/add"

        val locParent = JsonObject()
        val obj = JsonObject()
        obj.addProperty("latitude", lat)
        obj.addProperty("longitude", long)
        //locParent.add("location",obj)

        val incident = JsonObject()
        incident.addProperty("reason", incidentData)
        incident.addProperty("direction", "north")
        incident.addProperty("speed", "30mph")
        //incidentParent.add("incidentData",incident)

        var merge = JsonObject()
        merge.add("location",obj);
        merge.add("incidentData",incident)

        var map = HashMap<String,String>()
        map["Content-Type"] = "application/json"

        Networking.INSTANCE.asyncConnection(
            url,
            HttpMethods.POST.name,
            merge.toString(),
            map,
            object : Networking.ResponseHandler {
                override fun onSuccess(response: String?) {
                    Log.d(TAG, "Report Incident Success Response: $response")
                    reportIncidentLiveData.postValue(true)
                }

                override fun onFailure(failureType: Networking.ResponseHandler.FailureType?, responseCode: Int, reason: String?) {
                    Log.d(TAG, "Report Incident Failed: $failureType")
                    reportIncidentLiveData.postValue(false)
                }
            })
    }

    companion object{
        val TAG : String = ReportIncidentViewModel::class.java.name
    }
}