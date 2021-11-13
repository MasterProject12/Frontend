package com.app.travel.flare.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    public  static String IS_LOGGED_IN = "IsLoggedIn";
    public  static String REPORTED_TIME = "ReportedTime";
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
