package cl.softmedia.movillitar.utils;

/**
 * Created by iroman on 05/09/2015.
 */
public class ListViewItem {
    private String titulo;
    private String fecha;
    private String descripcion;
    private int iconId;

    public ListViewItem(String titulo, String fecha, String descripcion, int iconId) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.iconId = iconId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}
