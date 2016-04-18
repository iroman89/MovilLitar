package cl.softmedia.movillitar.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by iroman on 20/03/2016.
 */
@DatabaseTable
public class Usuario {

    public static final String ID = "_Id";
    public static final String NOMBRES = "nombres";
    public static final String CORREO = "correo";
    public static final String LATITUD = "latitud";
    public static final String LONGITUD = "longitud";
    public static final String PASSWORD = "pass";

    @DatabaseField(id = true, columnName = ID)
    public int idUsuario;

    @DatabaseField(columnName = NOMBRES)
    public String nombres;

    @DatabaseField(columnName = CORREO)
    public String correo;

    @DatabaseField(columnName = PASSWORD)
    public String password;

    @DatabaseField(columnName = LATITUD)
    public String latitud;

    @DatabaseField(columnName = LONGITUD)
    public String longitud;
}
