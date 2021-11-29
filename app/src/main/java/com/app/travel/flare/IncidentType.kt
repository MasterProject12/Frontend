package com.app.travel.flare

data class IncidentType(val type : String, val resId : Int)

public val movieGenreList = listOf<IncidentType>(
    IncidentType("Car Accident", R.drawable.accident),
    IncidentType("Car Breakdown", R.drawable.car_breakdown),
    IncidentType("Road Blockage", R.drawable.blockage),
    IncidentType("Construction Site", R.drawable.construction),

)