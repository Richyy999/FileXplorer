package com.rbp.filexplorer.modelo.entidad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.rbp.filexplorer.R;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Ricardo Bordería Pi
 */
public class Archivo extends File {

    public static final String ROOT_PATH = "/storage/emulated/0";

    private Context context;

    private boolean selected;

    public Archivo(String path) {
        super(path);
        selected = false;
    }

    public Archivo(@NonNull String pathname, Context context) {
        super(pathname);
        this.context = context;
        this.selected = false;
    }

    /**
     * Devuelve el icono que le corresponde según el tipo de archivo sea.
     * Si es una imagen devuelve un Bitmap de si mismo
     *
     * @return Bitmap con el icono correspondiente.
     */
    public Bitmap getIcono() {
        Bitmap icono = null;
        Drawable d = null;
        String tipo = getTipo().toLowerCase();
        if (tipo.contains("audio"))
            d = this.context.getResources().getDrawable(R.drawable.musica);
        else if (tipo.contains("android"))
            d = this.context.getResources().getDrawable(R.drawable.apk);
        else if (tipo.contains("pdf"))
            d = this.context.getResources().getDrawable(R.drawable.pdf);
        else if (this.isDirectory())
            d = this.context.getResources().getDrawable(R.drawable.carpeta_logo);
        else if (tipo.contains("image"))
            icono = BitmapFactory.decodeFile(this.getPath());
        else
            d = this.context.getResources().getDrawable(R.drawable.file);
        if (d != null)
            icono = ((BitmapDrawable) d).getBitmap();
        return icono;
    }

    /**
     * Devuelve el tipo de archivo que es.
     *
     * @return String con su tipo y su extensión sin punto y unidos por un /
     */
    public String getTipo() {
        String tipo = null;
        if (this.isDirectory())
            tipo = "carpeta";
        else {
            try {
                String extension = getExtension();
                tipo = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.replace(".", ""));
            } catch (IndexOutOfBoundsException e) {
                Log.d("SIN PUNTO", this.getName());
            }
        }
        if (tipo == null)
            tipo = "otro";
        return tipo;
    }

    /**
     * Devuelve la extensión del archivo
     *
     * @return String con la extensión con punto
     */
    public String getExtension() {
        return this.getAbsolutePath().substring(this.getAbsolutePath().lastIndexOf("."));
    }

    /**
     * Devuelve el tamaño del archivo.
     *
     * @return Si es una carpeta, devuelve el número de elementos que contiene. Si es un fichero, su peso
     */
    public String getTamano() {
        String lengthStr;
        if (this.isDirectory()) {
            int length = this.listFiles().length;
            if (length == 1)
                lengthStr = "1 Elemento";
            else
                lengthStr = length + " Elementos";
        } else {
            double length = this.length() / 1024;
            if (length / 1048576 >= 0.8) {
                length /= 1048576;
                if (length % 1 == 0)
                    lengthStr = length + " GB";
                else
                    lengthStr = (round(length, 2) + " GB");
            } else if (length / 1024 >= 1) {
                length /= 1024;
                if (length % 1 == 0)
                    lengthStr = (int) length + " MB";
                else
                    lengthStr = (round(length, 2)) + " MB";
            } else {
                if (length % 1 == 0)
                    lengthStr = (int) length + " KB";
                else
                    lengthStr = (round(length, 2) + " KB");
            }
        }
        return lengthStr;
    }

    private double round(double numero, int decimales) {
        BigDecimal bd = BigDecimal.valueOf(numero);
        bd = bd.setScale(decimales, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Devuelve la fecha formateada de la última modificación del archivo
     *
     * @return fecha con la última modificación del fichero
     */
    public String getLastModification() {
        Date fechaMod = new Date(this.lastModified());
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaMod);
        String fecha = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR) + " "
                + cal.get(Calendar.HOUR) + ":";
        if (String.valueOf(cal.get(Calendar.MINUTE)).length() < 2)
            fecha += "0" + cal.get(Calendar.MINUTE);
        else
            fecha += String.valueOf(cal.get(Calendar.MINUTE));
        int am = cal.get(Calendar.AM_PM);
        if (am == 0)
            fecha += " pm";
        else
            fecha += " am";

        return fecha;
    }

    /**
     * Devuelve si el archivo está seleccionado o no
     *
     * @return true si está seleccionado. false en caso contrario
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Selecciona o desselecciona el archivo
     *
     * @param selected true si el archivo está seleccionado. false en caso contrario
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
