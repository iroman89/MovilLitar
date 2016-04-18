package cl.softmedia.movillitar.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private final String SHARED_FILE = "Configuracion";
    public static final String KEY_USUARIO = "USUARIO";
    public static final String KEY_PASSWD = "PASSWD";
    public static final String KEY_VISITA_ORDER = "VISITA_ORDER";
    public static final String KEY_DATE_VISITA_PROCESSED = "FECHA_VISITAS_PROCESADAS";


    private Context mContext;

    public SharedPreferencesManager(Context mContext) {
        this.mContext = mContext;
    }

    private SharedPreferences getSettings() {
        return mContext.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE);
    }

    public String getString(String sKey) {
        return getSettings().getString(sKey, null);
    }

    public void setString(String sKey, String sValue) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(sKey, sValue);
        editor.commit();
    }

    public int getInt(String sKey) {
        return getSettings().getInt(sKey, -1);
    }

    public void setInt(String sKey, int iValue) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(sKey, iValue);
        editor.commit();
    }

    public boolean getBoolean(String sKey) {
        return getSettings().getBoolean(sKey, false);
    }

    public void setBoolean(String sKey, boolean bValue) {
        getSettings().edit().putBoolean(sKey, bValue).commit();
    }
}
