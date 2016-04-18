package cl.softmedia.movillitar.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by iroman on 23/03/2016.
 */
@DatabaseTable
public class AsignacionVisita implements Serializable {

    public static final String ID = "_Id";
    public static final String ID_VISITA = "id_visita";
    public static final String OBSERVACION = "observacion";
    public static final String FECHA_VISITA = "fecha_visita";
    public static final String ID_TIPO_VISITA = "id_tipo_visita";
    public static final String ID_ESTADO_VISITA = "id_estado_visita";
    public static final String LONGITUD_VISITA = "longitud_visita";
    public static final String LATITUD_VISITA = "latitud_visita";
    public static final String ID_ZONA = "id_zona_visita";
    public static final String SALDO = "saldo";
    public static final String RUT_CLIENTE = "rut_cliente";
    public static final String ID_CLIENTE = "id_cliente";
    public static final String CLIENTE = "cliente";
    public static final String DIRECCION = "direccion";
    public static final String ID_COMUNA = "id_comuna";
    public static final String COMUNA = "comuna";
    public static final String RUT_VENDEDOR = "rut_vendedor";
    public static final String ID_VENDEDOR = "id_vendedor";
    public static final String RUT_SUPERVISOR = "rut_supervisor";
    public static final String ID_SUPERVISOR = "id_supervisor";
    public static final String TELEFONO_CLIENTE = "telefono";
    public static final String EMAIL_CLIENTE = "e_mail";
    public static final String TERRENO_PROPIO = "terreno_propio";
    public static final String HECTARIAS_PROPIO = "hectareas_propio";
    public static final String TERRENO_ARRENDADO = "terreno_arrendado";
    public static final String HECTARIAS_ARRENADO = "hectarias_arrendado";
    public static final String TIPO_CLIENTE = "tipo_cliente";
    public static final String ACTIVA_COORDENADAS = "ActivaCoordenadas";
    public static final String ACTIVA_NUEVA_VISITA = "activa_nueva_visita";
    public static final String LATITUD_CLIENTE = "latitud_cliente";
    public static final String LONGITUD_CLIENTE = "longitud_cliente";

    public static final String FECHA_PROXIMA_VISITA = "prox_visita";
    public static final String TEMA_TRATADO = "tema";
    public static final String ID_CULTIVO = "id_cultivo";
    public static final String CULTIVO = "cultivo";
    public static final String FECHA_INICIO_CULTIVO = "ini_cultivo";
    public static final String FECHA_FIN_CULTIVO = "fin_cultivo";
    public static final String ORDEN_VISITA = "orden_ejecucion_visita";
    public static final String FECHA_REGISTRO = "fecha_Registro";
    public static final String HORA_REGISTRO = "hora_Registro";
    public static final String CUMPLE_GPS = "cumple_gps";
    public static final String SINCRONIZA_TEMPORAL_EJECUTADO = "sincroniza_temporal_ejec";
    public static final String SINCRONIZA_VISITA_EJECUTADO = "sincroniza_visita_ejec";

    @DatabaseField(generatedId = true, columnName = ID)
    public int idAsignacionVisita;

    @DatabaseField(columnName = ID_VISITA)
    @JsonProperty(ID_VISITA)
    public int idVisita;

    @DatabaseField(columnName = FECHA_VISITA, canBeNull = true)
    @JsonProperty(FECHA_VISITA)
    public String fechaVisita;

    @DatabaseField(columnName = ID_TIPO_VISITA, canBeNull = true)
    @JsonProperty(ID_TIPO_VISITA)
    public int idTipoVisita;

    @DatabaseField(columnName = ID_ESTADO_VISITA, canBeNull = true)
    @JsonProperty(ID_ESTADO_VISITA)
    public int idEstadoVisita;

    @DatabaseField(columnName = LONGITUD_VISITA, canBeNull = true, defaultValue = "0.0")
    @JsonProperty(LONGITUD_VISITA)
    public String longitudVisita;

    @DatabaseField(columnName = LATITUD_VISITA, canBeNull = true, defaultValue = "0.0")
    @JsonProperty(LATITUD_VISITA)
    public String latitudVisita;

    @DatabaseField(columnName = ID_ZONA, canBeNull = true)
    @JsonProperty(ID_ZONA)
    public int idZona;

    @DatabaseField(columnName = SALDO, canBeNull = true)
    @JsonProperty(SALDO)
    public int saldo;

    @DatabaseField(columnName = RUT_CLIENTE, canBeNull = true)
    @JsonProperty(RUT_CLIENTE)
    public String rutCliente;

    @DatabaseField(columnName = ID_CLIENTE, canBeNull = true)
    @JsonProperty(ID_CLIENTE)
    public int idCliente;

    @DatabaseField(columnName = CLIENTE, canBeNull = true, defaultValue = "Ninguno")
    @JsonProperty(CLIENTE)
    public String cliente;

    @DatabaseField(columnName = DIRECCION, canBeNull = true, defaultValue = "Ninguna")
    @JsonProperty(DIRECCION)
    public String direccion;

    @DatabaseField(columnName = ID_COMUNA, canBeNull = true, defaultValue = "-1")
    @JsonProperty(ID_COMUNA)
    public int idComuna;

    @DatabaseField(columnName = COMUNA, canBeNull = true)
    @JsonProperty(COMUNA)
    public String comuna;

    @DatabaseField(columnName = RUT_VENDEDOR, canBeNull = true)
    @JsonProperty(RUT_VENDEDOR)
    public String rutVendedor;

    @DatabaseField(columnName = ID_VENDEDOR, canBeNull = true)
    @JsonProperty(ID_VENDEDOR)
    public int idVendedor;

    @DatabaseField(columnName = RUT_SUPERVISOR, canBeNull = true)
    @JsonProperty(RUT_SUPERVISOR)
    public String rutSupervisor;

    @DatabaseField(columnName = ID_SUPERVISOR, canBeNull = true)
    @JsonProperty(ID_SUPERVISOR)
    public int idSupervisor;

    @DatabaseField(columnName = TELEFONO_CLIENTE, canBeNull = true)
    @JsonProperty(TELEFONO_CLIENTE)
    public int telefonoCliente;

    @DatabaseField(columnName = EMAIL_CLIENTE, canBeNull = true)
    @JsonProperty(EMAIL_CLIENTE)
    public String emailCliente;

    @DatabaseField(columnName = TERRENO_PROPIO, canBeNull = true)
    @JsonProperty(TERRENO_PROPIO)
    public String terrenoPropio;

    @DatabaseField(columnName = HECTARIAS_PROPIO, canBeNull = true)
    @JsonProperty(HECTARIAS_PROPIO)
    public String hectariasPropio;

    @DatabaseField(columnName = TERRENO_ARRENDADO, canBeNull = true)
    @JsonProperty(TERRENO_ARRENDADO)
    public String terrenoArrendado;

    @DatabaseField(columnName = HECTARIAS_ARRENADO, canBeNull = true)
    @JsonProperty(HECTARIAS_ARRENADO)
    public String hectariasArrendado;

    @DatabaseField(columnName = TIPO_CLIENTE, canBeNull = true)
    @JsonProperty(TIPO_CLIENTE)
    public String tipoCliente;

    @DatabaseField(columnName = ACTIVA_COORDENADAS, canBeNull = true)
    @JsonProperty(ACTIVA_COORDENADAS)
    private String activaCoordenadas;

    @DatabaseField(columnName = ACTIVA_NUEVA_VISITA, canBeNull = true, defaultValue = "0")
    @JsonProperty(ACTIVA_NUEVA_VISITA)
    public String activaNuevaVisita;

    @DatabaseField(columnName = LATITUD_CLIENTE, canBeNull = true, defaultValue = "0.0")
    @JsonProperty(LATITUD_CLIENTE)
    public String latitudCliente;

    @DatabaseField(columnName = LONGITUD_CLIENTE, canBeNull = true, defaultValue = "0.0")
    @JsonProperty(LONGITUD_CLIENTE)
    public String longitudCliente;

    @DatabaseField(columnName = FECHA_PROXIMA_VISITA, canBeNull = true, defaultValue = "")
    @JsonProperty(FECHA_PROXIMA_VISITA)
    public String proximaVisita;

    @DatabaseField(columnName = TEMA_TRATADO, canBeNull = true)
    @JsonProperty(OBSERVACION)
    public String temaTratado;

    @DatabaseField(columnName = ID_CULTIVO, canBeNull = true)
    @JsonProperty(ID_CULTIVO)
    public int idCultivo;

    @DatabaseField(columnName = CULTIVO, canBeNull = true)
    @JsonProperty(CULTIVO)
    public String cultivo;

    @DatabaseField(columnName = FECHA_INICIO_CULTIVO, canBeNull = true)
    @JsonProperty(FECHA_INICIO_CULTIVO)
    public int fechaInicioCultivo;

    @DatabaseField(columnName = FECHA_FIN_CULTIVO, canBeNull = true)
    @JsonProperty(FECHA_FIN_CULTIVO)
    public int fechaFinCultivo;

    @DatabaseField(columnName = ORDEN_VISITA, canBeNull = true)
    @JsonProperty(ORDEN_VISITA)
    public int ordenVisita;

    @DatabaseField(columnName = FECHA_REGISTRO, canBeNull = true)
    @JsonProperty(FECHA_REGISTRO)
    public String fechaRegistro;

    @DatabaseField(columnName = HORA_REGISTRO, canBeNull = true)
    @JsonProperty(HORA_REGISTRO)
    public String horaRegistro;

    @DatabaseField(columnName = CUMPLE_GPS, canBeNull = true, defaultValue = "No")
    @JsonProperty(CUMPLE_GPS)
    public String cumpleGPS;

    @DatabaseField(columnName = SINCRONIZA_TEMPORAL_EJECUTADO, canBeNull = true, dataType = DataType.BOOLEAN)
    @JsonProperty(SINCRONIZA_TEMPORAL_EJECUTADO)
    public boolean sincronizaTemporalEjecutado;

    @DatabaseField(columnName = SINCRONIZA_VISITA_EJECUTADO, canBeNull = true, dataType = DataType.BOOLEAN)
    @JsonProperty(SINCRONIZA_VISITA_EJECUTADO)
    public boolean sincronizaVisitaEjecutado;
}
