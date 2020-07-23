package com.rbp.filexplorer.modelo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FileUtils {

    /**
     * Ordena los archivos de una carpeta por orden alfabético y priorizando las carpetas sobre el resto
     * de archivos
     *
     * @param files List de Archivos con los archivos de una carpeta
     * @return List de Archivos ordenados
     */
    public List<Archivo> getSortedFiles(List<Archivo> files) {
        List<Archivo> sortedFiles = new ArrayList<>();
        Map<String, Archivo> folder = new TreeMap<>();
        Map<String, Archivo> file = new TreeMap<>();
        for (Archivo archivo : files) {
            if (archivo.isDirectory())
                folder.put(archivo.getName().toLowerCase(), archivo);
            else
                file.put(archivo.getName().toLowerCase(), archivo);
        }
        for (Map.Entry<String, Archivo> archivo : folder.entrySet()) {
            sortedFiles.add(archivo.getValue());
        }
        for (Map.Entry<String, Archivo> archivo : file.entrySet()) {
            sortedFiles.add(archivo.getValue());
        }
        return sortedFiles;
    }

    /**
     * Elimina una lista de archivos
     *
     * @param files List de Archivo a eliminar
     */
    public void delete(List<Archivo> files) {
        for (Archivo archivo : files) {
            delete(archivo);
        }
    }

    /**
     * Elimina un único archivo
     *
     * @param archivo archivo a eliminar
     */
    private void delete(File archivo) {
        Log.d("DELETING", archivo.getAbsolutePath());
        if (archivo.isDirectory()) {
            if (archivo.listFiles().length == 0)
                archivo.delete();
            else {
                for (File file : archivo.listFiles()) {
                    delete(file);
                }
                if (archivo.listFiles().length == 0)
                    archivo.delete();
            }
        } else
            archivo.delete();
    }

    /**
     * Abre un archivo en otra aplicación distinta
     *
     * @param archivo  Archivo a abrir
     * @param activity Activity de la que procede el archivo
     */
    public void openFile(Archivo archivo, Activity activity) {
        Uri uri = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName() + ".provider", archivo);
        String mime = activity.getContentResolver().getType(uri);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(intent);
    }

    /**
     * Devuelve un array con las direcciones absolutas de la lista de archivos pasada por parámetro
     *
     * @param archivos List de Archivo de los que se quiere obtener sus rutas absolutas
     * @return String[] con las rutas absolutas
     */
    public static String[] getFilesPath(List<Archivo> archivos) {
        String[] paths = new String[archivos.size()];
        for (int i = 0; i < archivos.size(); i++) {
            Archivo archivo = archivos.get(i);
            paths[i] = archivo.getAbsolutePath();
        }
        return paths;
    }

    /**
     * Crea una lista de archivos ordenados a partir de su ruta.
     *
     * @param path String[] con las rutas de los archivos
     * @return List de Archivo con los archivos creados a partir de su ruta
     */
    public List<Archivo> getArchivosFromPath(String[] path) {
        List<Archivo> archivos = new ArrayList<>();
        for (String estring : path) {
            archivos.add(new Archivo(estring));
        }
        return getSortedFiles(archivos);
    }

    /**
     * Copia una lista de archivos en su carpeta de destino
     *
     * @param archivos List de Archivo a copiar
     * @param folder   carpeta de destino
     */
    public void copyFileOrFlder(List<Archivo> archivos, Archivo folder) {
        for (Archivo archivo : archivos) {
            File src = new File(archivo.getAbsolutePath());
            File dst = new File(folder.getAbsolutePath(), src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    List<Archivo> lista = new ArrayList<>();
                    lista.add(new Archivo(src1));
                    copyFileOrFlder(lista, new Archivo(dst1));
                }
            } else
                try {
                    copyFile(archivo, folder);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("ARCHIVO", archivo.getAbsolutePath());
                }
        }
    }

    /**
     * Copia un archivo en su carpeta de destino
     *
     * @param source Archivo a copiar
     * @param folder Carpeta de destino
     * @throws IOException Si ocurre algún error al copiar el contenido del archivo original al nuevo archivo
     */
    private void copyFile(Archivo source, Archivo folder) throws IOException {
        Archivo destination = new Archivo(folder.getAbsolutePath() + "/" + source.getName());
        if (!destination.getParentFile().exists())
            destination.getParentFile().mkdirs();
        if (!destination.exists())
            destination.createNewFile();
        else
            destination = getRepetidos(destination);

        Log.d("COPY", destination.getAbsolutePath());

        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;

        sourceChannel = new FileInputStream(source).getChannel();
        destinationChannel = new FileOutputStream(destination).getChannel();
        destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

        sourceChannel.close();
        destinationChannel.close();
    }

    /**
     * Cambia el nombre de un archivo
     *
     * @param archivo Archivo a renombrar
     * @param newName nuevo nombre del archivo
     */
    public void rename(File archivo, String newName) {
        String newPath = archivo.getParentFile().getAbsolutePath() + "/" + newName;
        Archivo newFile = new Archivo(newPath);
        newFile = getRepetidos(newFile);
        Log.d("RENAME", newFile.getAbsolutePath());
        archivo.renameTo(newFile);
    }

    /**
     * Verifica si el archivo ya existe. Si existe, crea un duplpicado para no sobreescribir el original.
     *
     * @param file Archivo a verificar
     * @return Si el archivo no existe, devuelve el archivo pasado por parámetro.
     * Si ya existe devuelve el archivo con el nombre del duplicado
     */
    private Archivo getRepetidos(Archivo file) {
        int numRepetidos = 0;
        String extension = "";
        boolean isFile = false;
        while (file.exists()) {
            numRepetidos++;
            if (file.isFile()) {
                extension = file.getExtension();
                isFile = true;
            }
            file = new Archivo(file.getAbsolutePath().replace(extension, ""));
            if (file.getName().endsWith("(" + numRepetidos + ")"))
                file = new Archivo(file.getAbsolutePath().replace("(" + numRepetidos + ")", "(" + (numRepetidos + 1) + ")"));
            else {
                file = new Archivo(file.getAbsolutePath() + "(1)");
                numRepetidos--;
            }
            if (isFile)
                file = new Archivo(file.getAbsolutePath() + extension);
        }
        return file;
    }

    /**
     * Crea una nueva carpeta. Si la carpeta existe añade un índice autogenerado
     *
     * @param folder carpeta a crear con la ruta nueva
     * @return true si se ha creado con éxito. false en caso contrario
     */
    public boolean createFolder(Archivo folder) {
        folder = getRepetidos(folder);
        Log.d("CREATE FOLDER", folder.getAbsolutePath());
        return folder.mkdir();
    }

    /**
     * Busca todos los archivos que contienen el nombre en la carpeta y todas sus sub carpetas
     *
     * @param folder     carpeta en la que se busca
     * @param searchName nombre del archivo buscado
     * @param context    contexto de la ventana desde la que se llama
     * @return List de Archivo con los archivos encontrados
     */
    public List<Archivo> find(File folder, String searchName, Context context) {
        List<Archivo> filesFounded = new ArrayList<>();
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory())
                filesFounded.addAll(find(file, searchName, context));
            else if (file.isFile() && file.getName().toLowerCase().contains(searchName.toLowerCase())) {
                Log.d("FILE FOUNDED", file.getAbsolutePath());
                filesFounded.add(new Archivo(file.getAbsolutePath(), context));
            }
        }
        return filesFounded;
    }

    /**
     * Devuelve una lista con todos las imágenes de la carpeta
     *
     * @param folder  carpeta contenedora
     * @param context contexto de la aplicación
     * @return List de Archivo con todos los archivos que sean imágenes
     */
    public List<Archivo> getImages(File folder, Context context) {
        List<Archivo> images = new ArrayList<>();
        List<Archivo> archivos = new ArrayList<>();
        File[] files = folder.listFiles();
        for (File file : files) {
            archivos.add(new Archivo(file.getAbsolutePath(), context));
        }
        for (Archivo archivo : archivos) {
            if (archivo.getTipo().toLowerCase().contains("image"))
                images.add(archivo);
        }
        return images;
    }
}
