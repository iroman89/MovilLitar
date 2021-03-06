package cl.softmedia.movillitar.bss;

import org.json.JSONArray;

import cl.softmedia.movillitar.http.Request;

/**
 * Created by iroman on 23/03/2016.
 */
public class ComunaBss {

    private Request oRequestController;
    private String sConexion;

    public ComunaBss(String sConexion) {
        this.oRequestController = new Request();
        this.sConexion = String.format("%s/%s", sConexion, "buscar_comunas.php?");
    }

    public JSONArray obtenerComunas() throws Exception {
        JSONArray jsonArray = null;
        try {

            jsonArray = this.oRequestController.connect(sConexion, null);

        } catch (Exception e) {
            throw e;
        }
        return jsonArray;
    }
}


