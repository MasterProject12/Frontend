package com.app.travel.flare.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.travel.flare.network.Networking
import com.app.travel.flare.utils.HttpMethods
import com.google.gson.JsonObject

class ReportIncidentViewModel : ViewModel() {

    var reportIncidentLiveData = MutableLiveData<Boolean>()

    fun reportIncident(incidentData: String?, lat: Double, long: Double?) {
        val url = "https://s1db9j47u4.execute-api.us-west-1.amazonaws.com/prod/addrefund"
        val obj = JsonObject()
        obj.addProperty("incident", incidentData)
        obj.addProperty("latitude", lat)
        obj.addProperty("longitude", long)
        Networking.INSTANCE.asyncConnection(
            url,
            HttpMethods.POST.name,
            obj.toString(),
            null,
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