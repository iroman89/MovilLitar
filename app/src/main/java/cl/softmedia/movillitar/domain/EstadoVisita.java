package cl.softmedia.movillitar.domain;

/**
 * Created by iroman on 30/03/2016.
 */
public class EstadoVisita {

    public static final int GESTIONADA = 2;
    public static final int NO_GESTIONADA = 1;
    public static final int NO_COMPLETADA = 3;

    public static String GetEstadoVisita(int idEstadoVisita) {
        switch (idEstadoVisita) {
            case 2:
                return "Gestionada";
            case 1:
                return "No Gestionada";
            case 3:
                return "No Completada";
        }
        return "";
    }

    public static int GetColorEstadoVisita(int idEstadoVisita) {

        switch (idEstadoVisita) {
            case 2:
                return android.R.color.holo_green_dark;
            case 1:
                return android.R.color.holo_red_light;
            case 3:
                return android.R.color.holo_orange_light;
        }
        return 0;
    }
}

