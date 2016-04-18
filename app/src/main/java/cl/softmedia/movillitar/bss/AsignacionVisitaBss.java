package cl.softmedia.movillitar.bss;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cl.softmedia.movillitar.dao.GenericDao;
import cl.softmedia.movillitar.domain.AsignacionVisita;
import cl.softmedia.movillitar.http.Request;

/**
 * Created by iroman on 30/03/2016.
 */
public class AsignacionVisitaBss {

    private Request oRequestController;
    private String sConexion;
    private GenericDao<AsignacionVisita> oAsignacionVisitaDao;

    public AsignacionVisitaBss(Context oContext) throws Exception {

        this.oRequestController = new Request();
        this.oAsignacionVisitaDao = new GenericDao<>(oContext, AsignacionVisita.class);
    }

    public String sincronizarTemporal(String sConexion, AsignacionVisita oAsignacionVisita) throws Exception {
        String response = "";
        try {

            this.sConexion = String.format("%s/%s", sConexion, "sincroniza_temporal.php?");

            HashMap<String, String> hashtable = new HashMap<>();

            hashtable.put("id_cliente", String.format("%s", oAsignacionVisita.idCliente));
            hashtable.put("id_asignacion", String.format("%s", oAsignacionVisita.idVisita));
            hashtable.put("rut", oAsignacionVisita.rutCliente);
            hashtable.put("nombre", oAsignacionVisita.cliente);
            hashtable.put("fono", String.format("%s", oAsignacionVisita.telefonoCliente));
            hashtable.put("prox_visita", oAsignacionVisita.proximaVisita);
            hashtable.put("hora", oAsignacionVisita.horaRegistro);
            hashtable.put("direccion", oAsignacionVisita.direccion);
            hashtable.put("latitud", oAsignacionVisita.latitudVisita);
            hashtable.put("longitud", oAsignacionVisita.longitudVisita);
            hashtable.put("id_comuna", String.format("%s", oAsignacionVisita.idComuna));
            hashtable.put("id_cultivo", String.format("%s", oAsignacionVisita.idCultivo));
            hashtable.put("cultivo", oAsignacionVisita.cultivo);
            hashtable.put("inicio", String.format("%s", oAsignacionVisita.fechaInicioCultivo));
            hashtable.put("fin", String.format("%s", oAsignacionVisita.fechaFinCultivo));

            if (oAsignacionVisita.terrenoArrendado.equals("Si")) {
                hashtable.put("superficie", oAsignacionVisita.hectariasArrendado);
                hashtable.put("propiedad", "2");
            } else {
                hashtable.put("superficie", oAsignacionVisita.hectariasPropio);
                hashtable.put("propiedad", "1");
            }

            hashtable.put("correo", oAsignacionVisita.emailCliente);
            hashtable.put("tema", oAsignacionVisita.temaTratado);
            hashtable.put("estado", String.format("%s", oAsignacionVisita.idEstadoVisita));
            hashtable.put("tipo_visita", String.format("%s", oAsignacionVisita.idTipoVisita));
            hashtable.put("tipo_cliente", String.format("%s", oAsignacionVisita.tipoCliente));
            hashtable.put("usuario", String.format("%s", oAsignacionVisita.idVendedor));

            response = this.oRequestController.connectToString(this.sConexion, hashtable).toString();

        } catch (Exception e) {

            throw e;
        }
        return response;
    }

    public String sincronizarVisitas(String sConexion, AsignacionVisita oAsignacionVisita) throws Exception {
        String response = "";
        try {

            this.sConexion = String.format("%s/%s", sConexion, "sincroniza_visitas.php?");

            HashMap<String, String> hashtable = new HashMap<>();

            hashtable.put("id_cliente", String.format("%s", oAsignacionVisita.idCliente));
            hashtable.put("id_asignacion", String.format("%s", oAsignacionVisita.idVisita));
            hashtable.put("rut", oAsignacionVisita.rutCliente);
            hashtable.put("nombre", oAsignacionVisita.cliente);
            hashtable.put("fono", String.format("%s", oAsignacionVisita.telefonoCliente));
            hashtable.put("prox_visita", oAsignacionVisita.proximaVisita);
            hashtable.put("hora", oAsignacionVisita.horaRegistro);
            hashtable.put("direccion", oAsignacionVisita.direccion);
            hashtable.put("latitud", oAsignacionVisita.latitudVisita);
            hashtable.put("longitud", oAsignacionVisita.longitudVisita);
            hashtable.put("id_comuna", String.format("%s", oAsignacionVisita.idComuna));
            hashtable.put("id_cultivo", String.format("%s", oAsignacionVisita.idCultivo));
            hashtable.put("cultivo", oAsignacionVisita.cultivo);
            hashtable.put("inicio", String.format("%s", oAsignacionVisita.fechaInicioCultivo));
            hashtable.put("fin", String.format("%s", oAsignacionVisita.fechaFinCultivo));
            if (oAsignacionVisita.terrenoArrendado.equals("Si")) {
                hashtable.put("superficie", oAsignacionVisita.hectariasArrendado);
                hashtable.put("propiedad", "2");
            } else {
                hashtable.put("superficie", oAsignacionVisita.hectariasPropio);
                hashtable.put("propiedad", "1");
            }
            hashtable.put("guarda_gps", oAsignacionVisita.idVisita == 0 ? "0" : "1");
            hashtable.put("cumple_gps", oAsignacionVisita.cumpleGPS);
            hashtable.put("correo", oAsignacionVisita.emailCliente);
            hashtable.put("tema", oAsignacionVisita.temaTratado);
            hashtable.put("estado", String.format("%s", oAsignacionVisita.idEstadoVisita));
            hashtable.put("tipo_visita", String.format("%s", oAsignacionVisita.idTipoVisita));
            hashtable.put("tipo_cliente", String.format("%s", oAsignacionVisita.tipoCliente));
            hashtable.put("usuario", String.format("%s", oAsignacionVisita.idVendedor));

            response = this.oRequestController.connectToString(this.sConexion, hashtable).toString();

        } catch (Exception e) {
            throw e;
        }
        return response;
    }


    public String insertaReporteDiario(String sConexion, int usuario, int gestionadosTotal, int noGestionadosTotal,
                                       int noCompletadosTotal, double kilometros) throws Exception {
        String response = "";
        try {

            this.sConexion = String.format("%s/%s", sConexion, "inserta_rep_dia_cab.php?");

            HashMap<String, String> hashtable = new HashMap<>();

            hashtable.put("usuario", String.format("%s", usuario));
            hashtable.put("fecha", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            hashtable.put("gestionados", String.format("%s", gestionadosTotal));
            hashtable.put("no_gestionados", String.format("%s", noGestionadosTotal));
            hashtable.put("no_completado", String.format("%s", noCompletadosTotal));
            hashtable.put("kilometros", String.format("%s", kilometros)); //sumatoria km.
            hashtable.put("estado_reporte", "1"); //Siempre es "1".

            response = this.oRequestController.connectToString(this.sConexion, hashtable).toString();

        } catch (Exception e) {
            throw e;
        }
        return response;
    }


}
