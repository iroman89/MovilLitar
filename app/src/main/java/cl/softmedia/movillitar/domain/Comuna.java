package cl.softmedia.movillitar.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by iroman on 16/03/2016.
 */
@DatabaseTable
public class Comuna {

    public static final String ID = "_Id";
    public static final String COMUNA = "comuna";
    public static final String ID_REGION = "idRegion";

    @DatabaseField(id = true, columnName = ID)
    public int idComuna;

    @DatabaseField(columnName = COMUNA)
    public String comuna;

    @DatabaseField(columnName = ID_REGION)
    public int idRegion;
}
