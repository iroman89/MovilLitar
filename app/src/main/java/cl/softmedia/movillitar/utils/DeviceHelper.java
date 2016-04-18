package cl.softmedia.movillitar.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class DeviceHelper {

    public DeviceHelper() {
    }

    /**
     * Obtiene el IMEI del dispositivo
     *
     * @return
     */
    public static String getIMEI(Context oContext) {
        TelephonyManager telephonyManager = (TelephonyManager) oContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

}
