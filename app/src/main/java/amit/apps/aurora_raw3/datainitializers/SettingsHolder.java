package amit.apps.aurora_raw3.datainitializers;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsHolder {
    private static SharedPreferences settingsPreferences;

    public static void initiaizeSharedPreferences(Context c) {
        settingsPreferences=c.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public static boolean getfanotif() {
        return settingsPreferences.getBoolean("fanotif", true);
    }

    public static boolean getinnotif() {
        return settingsPreferences.getBoolean("innotif", true);
    }

    public static boolean gettwnotif() {
        return settingsPreferences.getBoolean("twnotif", true);
    }

    public static boolean getvinotif() {
        return settingsPreferences.getBoolean("vinotif", true);
    }

    public static boolean gettinotif() {
        return settingsPreferences.getBoolean("tinotif", true);
    }

    public static void setfanotif(boolean fn) {
        settingsPreferences.edit().putBoolean("fanotif", fn).apply();
    }

    public static void setinnotif(boolean fn) {
        settingsPreferences.edit().putBoolean("innotif", fn).apply();
    }

    public static void settwnotif(boolean fn) {
        settingsPreferences.edit().putBoolean("twnotif", fn).apply();
    }

    public static void setvinotif(boolean fn) {
        settingsPreferences.edit().putBoolean("vinotif", fn).apply();
    }

    public static void settinotif(boolean fn) {
        settingsPreferences.edit().putBoolean("tinotif", fn).apply();
    }
}