package cl.softmedia.movillitar.bss;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cl.softmedia.movillitar.http.Request;

/**
 * Created by iroman on 23/03/2016.
 */
public class CargaDatosBss {

    private Request oRequestController;
    private String sConexion;
    private SimpleDateFormat sdf;

    public CargaDatosBss(String sConexion) {
        this.oRequestController = new Request();
        this.sConexion = String.format("%s/%s", sConexion, "buscar_asignacion.php?");
        this.sdf = new SimpleDateFormat("yyyy-MM-dd");
    }

    public JSONArray obtenerAsignacion(int idVendedor, Date fecha) throws Exception {
        JSONArray jsonArray = null;
        try {

            Calendar myCal = Calendar.getInstance();
            myCal.set(Calendar.YEAR, 2016);
            myCal.set(Calendar.MONTH, 01);
            myCal.set(Calendar.DAY_OF_MONTH, 9);
            fecha = myCal.getTime();

            HashMap<String, String> hashtable = new HashMap<>();
            hashtable.put("vendedor", String.format("%s", idVendedor));
            hashtable.put("fecha", sdf.format(fecha).toString());

            jsonArray = this.oRequestController.connect(sConexion, hashtable);

        } catch (Exception e) {
            throw e;
        }
        return jsonArray;
    }
}


