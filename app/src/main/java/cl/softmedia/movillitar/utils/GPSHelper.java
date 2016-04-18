package cl.softmedia.movillitar.utils;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by iroman on 02/04/2016.
 */
public class GPSHelper {

    public static boolean IsActive(Context oContext) {
        String provider = Settings.Secure.getString(oContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        return provider.contains("gps");
    }

    public static void turnOn(Context oContext) {
        try {

            oContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
