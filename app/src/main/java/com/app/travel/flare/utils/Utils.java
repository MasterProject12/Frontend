package com.app.travel.flare.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    public  static String IS_LOGGED_IN = "IsLoggedIn";
    public  static String REPORTED_TIME = "ReportedTime";
    public static String USER_NAME = "UserName";
    public static String EMAIL_ID = "EMAILID";
    public static String CITY_INFO = "CityInfo";
    public static String CITY_NAME = "CityName";

    static SharedPreferences sharedPref;

    public static boolean isNullOrEmpty(String string){
        return string == null || string.isEmpty();
    }

    public static void cacheData(boolean value, String key, Context context){
        sharedPref = context.getSharedPreferences("TravelFlarePref" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getCacheData(String key, Context context){
        sharedPref = context.getSharedPreferences("TravelFlarePref" , Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }

    public static void setStringData(String value, String key, Context context){
        sharedPref = context.getSharedPreferences("TravelFlarePref" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringData(String key, Context context){
        sharedPref = context.getSharedPreferences("TravelFlarePref" , Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public static void setLastReportedTime(long value, String key, Context context){
        sharedPref = context.getSharedPreferences("TravelFlarePref" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLastReportedTime(String key, Context context) {
        sharedPref = context.getSharedPreferences("TravelFlarePref", Context.MODE_PRIVATE);
        return sharedPref.getLong(key, 0);
    }
}
