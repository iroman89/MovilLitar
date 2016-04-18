package cl.softmedia.movillitar.domain;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by iroman on 26/03/2016.
 */
@DatabaseTable
public class Cultivo {

    public static final String ID = "_Id";
    public static final String CULTIVO = "cultivo";
    public static final String ESTADO = "estado";

    @DatabaseField(id = true, columnName = ID)
    public int idCultivo;

    @DatabaseField(columnName = CULTIVO, canBeNull = true)
    public String cultivo;

    @DatabaseField(columnName = ESTADO, dataType = DataType.BOOLEAN)
    public boolean estado;
}
