package com.rbp.filexplorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.rbp.filexplorer.modelo.Adaptador;
import com.rbp.filexplorer.modelo.FileUtils;
import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Activity que muestra el contenido de las carpetas
 *
 * @author Ricardo Bordería Pi
 */
public class ActivityCarpeta extends AppCompatActivity implements Adaptador.CustomClickListener {

    private RecyclerView rv;
    private RecyclerView.LayoutManager lm;
    private Adaptador adapter;

    private RecyclerView recyvlerBuscar;

    private TextView lblNoResult;

    private Archivo carpeta;

    private FileUtils fileUtils;

    private Snackbar snackbar;

    private SnackbarMenu snackbarMenu;

    private View deleteDialog;

    private ConstraintLayout root;

    private List<Archivo> archivos;
    private List<Archivo> archivosSeleccionados;

    private boolean isClicable;
    private boolean modoSeleccion;

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpeta);
        this.deleteDialog = getLayoutInflater().inflate(R.layout.dialog_delete, null);
        this.deleteDialog.setVisibility(View.INVISIBLE);
        this.fileUtils = new FileUtils();
        path = getIntent().getStringExtra("path");
        isClicable = true;
        if (path != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(this, ActivityPermisos.class);
                startActivity(intent);
            }
        } else {
            cargarVista();
        }
        this.archivosSeleccionados = new LinkedList<>();
        this.modoSeleccion = false;
    }

    @Override
    protected void onResume() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(this, ActivityPermisos.class);
                startActivity(intent);
            }
        } else
            getFiles();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.itSearch);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.searchFile));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cargarBusqueda(fileUtils.find(carpeta, query, ActivityCarpeta.this));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                closeSearchBar();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                launchParentActivity(carpeta);
                break;
            case R.id.itNewFolder:
                new DialogCreateNewFolder(ActivityCarpeta.this, carpeta).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void longClick(int position) {
        modoSeleccion = true;
        seleccionarArchivo(this.archivos.get(position));
        this.snackbarMenu = new SnackbarMenu(findViewById(android.R.id.content), this.archivosSeleccionados, this);
        this.snackbar = this.snackbarMenu.getSnackbar();
        this.snackbar.show();
        this.snackbarMenu.updateMenu();
        pushUpRecyclerView(position == this.archivos.size() - 1);
    }

    @Override
    public void click(int position) {
        Archivo archivo = archivos.get(position);
        if (!modoSeleccion) {
            if (isClicable) {
                isClicable = false;
                String tipo = archivo.getTipo();
                if (tipo.contains("carpeta")) {
                    abrirCarpeta(archivo);
                } else if (tipo.contains("audio"))
                    new DialogMusic(this, archivo).show();
                else if (tipo.contains("image")) {
                    Intent intent = new Intent(this, ImageActivity.class);
                    intent.putExtra("img", archivo.getAbsolutePath());
                    startActivity(intent);
                } else
                    this.fileUtils.openFile(archivo, this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isClicable = true;
                    }
                }, 1000);
            }
        } else {
            seleccionarArchivo(archivo);
            this.snackbarMenu.updateMenu();
        }
    }

    @Override
    public void onBackPressed() {
        if (this.modoSeleccion)
            clearModoSeleccion();
        else
            launchParentActivity(carpeta);
    }

    private void closeSearchBar() {
        System.out.println("CLOSE");
        lblNoResult.setVisibility(View.INVISIBLE);
        recyvlerBuscar.setVisibility(View.GONE);
        recyvlerBuscar.setEnabled(false);
        rv.setVisibility(View.VISIBLE);
        rv.setEnabled(true);
        updateFileList();
    }

    private void cargarBusqueda(List<Archivo> archivos) {
        rv.setVisibility(View.INVISIBLE);
        rv.setEnabled(false);
        recyvlerBuscar = findViewById(R.id.recyclerBuscarCarpeta);
        recyvlerBuscar.setLayoutManager(new LinearLayoutManager(this));

        Adaptador adaptador = new Adaptador(archivos, this, this);
        recyvlerBuscar.setAdapter(adaptador);
        if (archivos.size() == 0) {
            recyvlerBuscar.setVisibility(View.GONE);
            lblNoResult.setVisibility(View.VISIBLE);
        }
    }

    private void abrirCarpeta(Archivo archivo) {
        Intent intent = new Intent(ActivityCarpeta.this, ActivityCarpeta.class);
        intent.putExtra("path", archivo.getAbsolutePath());
        startActivity(intent);
    }

    private void seleccionarArchivo(Archivo archivo) {
        if (archivo.isSelected()) {
            archivo.setSelected(false);
            this.archivosSeleccionados.remove(archivo);
        } else {
            archivo.setSelected(true);
            this.archivosSeleccionados.add(archivo);
        }
        if (this.archivosSeleccionados.size() == 0) {
            this.modoSeleccion = false;
            this.snackbar.dismiss();
            pushDownRecyclerView();
        }
        this.adapter.notifyDataSetChanged();
    }

    /**
     * Carga los elementos de la vista
     */
    private void cargarVista() {
        rv = findViewById(R.id.recyclerCarpeta);
        rv.setHasFixedSize(false);

        lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);

        lblNoResult = findViewById(R.id.lblNoResults);
        lblNoResult.setVisibility(View.INVISIBLE);

        root = findViewById(R.id.rootCarpeta);

        if (path == null)
            path = Archivo.ROOT_PATH;
        else
            setTitle(new File(path).getAbsolutePath().substring(20));

        getFiles();
    }

    /**
     * Obtiene y carga la lista de archivos de la carpeta, ya sean ficheros o directorios.
     */
    private void getFiles() {
        archivos = new LinkedList<>();
        carpeta = new Archivo(path, this);
        File[] archivos = carpeta.listFiles();
        List<Archivo> fileList = new ArrayList<>();
        for (File file : archivos) {
            fileList.add(new Archivo(file.getAbsolutePath(), this));
        }
        this.archivos = this.fileUtils.getSortedFiles(fileList);
        this.adapter = new Adaptador(this.archivos, this, this);
        rv.setAdapter(this.adapter);
    }

    private void pushUpRecyclerView(boolean isLast) {
        int padding_in_dp = 80;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
        root.setPadding(0, 0, 0, padding_in_px);
        if (isLast)
            rv.scrollToPosition(this.archivos.size() - 1);
    }

    private String getParentPath(Archivo archivo) {
        String parentPath = archivo.getParent();
        if (parentPath.equals(Archivo.ROOT_PATH))
            return null;
        return parentPath;
    }

    public void launchParentActivity(Archivo archivo) {
        String parentPath = getParentPath(archivo);
        if (!archivo.getAbsolutePath().equals(Archivo.ROOT_PATH)) {
            Intent intent = new Intent(ActivityCarpeta.this, ActivityCarpeta.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("path", parentPath);
            startActivity(intent);
        } else {
            finish();
        }
    }

    public void updateFileList() {
        getFiles();
    }

    public void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (this.archivosSeleccionados.size() == 1)
            builder.setMessage(R.string.alertDelete1);
        else
            builder.setMessage(R.string.alertDelete);
        builder.setTitle(R.string.remove);
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteDialog.setVisibility(View.VISIBLE);
                rv.setEnabled(false);
                fileUtils.delete(archivosSeleccionados);
                for (Archivo archivo : archivosSeleccionados) {
                    int index = archivos.indexOf(archivo);
                    adapter.removeAt(index);
                }
                archivosSeleccionados.clear();
                snackbar.dismiss();
                pushDownRecyclerView();
                modoSeleccion = false;
                deleteDialog.setVisibility(View.GONE);
                rv.setEnabled(true);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void pushDownRecyclerView() {
        root.setPadding(0, 0, 0, 0);
    }

    public void clearModoSeleccion() {
        for (Archivo archivo : this.archivosSeleccionados) {
            archivo.setSelected(false);
        }
        this.archivosSeleccionados.clear();
        this.modoSeleccion = false;
        this.adapter.notifyDataSetChanged();
        if (this.snackbar != null)
            this.snackbar.dismiss();
        pushDownRecyclerView();
    }

    public Archivo getCarpeta() {
        return this.carpeta;
    }
}