package cl.softmedia.movillitar.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;

import cl.softmedia.movillitar.domain.AsignacionVisita;
import cl.softmedia.movillitar.domain.Comuna;
import cl.softmedia.movillitar.domain.Cultivo;
import cl.softmedia.movillitar.domain.Usuario;

/**
 * Created by iroman on 27/10/2015.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "Litar.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_FOLDER = "%s/Litar/RepoDataBase/";


    private static DatabaseHelper conexionInstance;
    private Context oContext;

    public static synchronized DatabaseHelper getConexion(Context mContext) {

        if (conexionInstance == null)
            conexionInstance = new DatabaseHelper(mContext);
        return conexionInstance;
    }

    public DatabaseHelper(Context oContext) {
        super(oContext, /*String.format("%s%s", getDataBaseFolder(), */DATABASE_NAME/*)*/, null, DATABASE_VERSION);
        this.oContext = oContext;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {

            TableUtils.createTable(connectionSource, Usuario.class);
            TableUtils.createTable(connectionSource, AsignacionVisita.class);
            TableUtils.createTable(connectionSource, Comuna.class);
            TableUtils.createTable(connectionSource, Cultivo.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

    }

    /**
     * Obtiene la ruta de la BD.
     *
     * @return Object
     */
    private static Object getDataBaseFolder() {

        String RutaCarpeta = "";

        try {

            RutaCarpeta = String.format(DATABASE_FOLDER, Environment.getExternalStorageDirectory().getAbsolutePath());

            File dbFolder = new File(RutaCarpeta);

            if (!dbFolder.exists()) {
                dbFolder.mkdirs();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return RutaCarpeta;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=OFF;");
        }
    }
}
