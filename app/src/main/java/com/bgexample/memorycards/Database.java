package com.bgexample.memorycards;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Database {
    public static void writeData(Activity activity, int points, int level, int columns, int rows) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("points", points);
        editor.putInt("level", level);
        editor.putInt("columns", columns);
        editor.putInt("rows", rows);
        editor.apply();
    }

    public static int readPoints(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt("points", 0);
    }
    public static int readLevel(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt("level", 1);
    }
    public static int readColumns(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt("columns", GameFragment.getStartCountColumns());
    }
    public static int readRows(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt("rows", GameFragment.getStartCountRows());
    }

    public static int countStarts(Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("starts", sharedPref.getInt("starts", 0) + 1);
        editor.apply();
        return sharedPref.getInt("starts", 0);
    }
    public static boolean viewDialog(Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean("dialog", true);
    }

    public static void setViewDialog(Activity activity, boolean status){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("dialog", false);
        editor.apply();
    }
    public static void setReferrer(Activity activity, String referrer){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("referrer", referrer);
        editor.apply();
    }
    public static String getReferrer(Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString("referrer", "");
    }
}
