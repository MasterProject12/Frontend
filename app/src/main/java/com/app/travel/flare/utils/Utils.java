package com.app.travel.flare.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class Utils {
    public  static String IS_LOGGED_IN = "IsLoggedIn";
    static SharedPreferences sharedPref;

    public static boolean isNullOrEmpty(String string){
        return string == null || string.isEmpty();
    }

    public static void cacheData(boolean value, Context context){
        sharedPref = context.getSharedPreferences("TravelFlarePref" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_LOGGED_IN, value);
        editor.commit();
    }

    public static boolean getCacheData(String key, Context context){
        sharedPref = context.getSharedPreferences("TravelFlarePref" , Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }
}
