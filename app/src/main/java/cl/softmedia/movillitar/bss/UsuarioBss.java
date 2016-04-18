package cl.softmedia.movillitar.bss;

import org.json.JSONArray;

import java.util.HashMap;

import cl.softmedia.movillitar.domain.Usuario;
import cl.softmedia.movillitar.http.Request;

/**
 * Created by iroman on 18/10/2015.
 */
public class UsuarioBss {

    private Request oRequestController;
    private String sConexion;

    public UsuarioBss(String sConexion) {
        this.oRequestController = new Request();
        this.sConexion = String.format("%s/%s", sConexion, "login.php?");
    }

    public JSONArray validarUsuario(Usuario oUsuario) throws Exception {
        JSONArray jsonArray = null;
        try {

            HashMap<String, String> hashtable = new HashMap<>();
            hashtable.put("correo2", oUsuario.correo);
            hashtable.put("clave2", oUsuario.password);

            jsonArray = this.oRequestController.connect(sConexion, hashtable);

        } catch (Exception e) {
            throw e;
        }
        return jsonArray;
    }
}
