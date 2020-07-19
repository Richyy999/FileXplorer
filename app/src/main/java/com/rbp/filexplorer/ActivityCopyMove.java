package com.rbp.filexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbp.filexplorer.modelo.AdaptadorCarpeta;
import com.rbp.filexplorer.modelo.FileUtils;
import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ActivityCopyMove extends AppCompatActivity implements AdaptadorCarpeta.CustomClickListener {

    private RecyclerView recyclerView;
    private AdaptadorCarpeta adaptadorCarpeta;

    private LinearLayout btnPaste;
    private LinearLayout btnCancel;
    private LinearLayout btnCreateFolder;

    private String modo;
    private String path;

    private String[] paths;

    private List<Archivo> archivos;

    private Archivo carpeta;

    private FileUtils fileUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_move);
        this.modo = getIntent().getStringExtra("modo");
        this.path = getIntent().getStringExtra("path");
        this.paths = getIntent().getStringArrayExtra("paths");
        if (this.path == null)
            this.path = Archivo.ROOT_PATH;
        this.carpeta = new Archivo(this.path, this);
        this.fileUtils = new FileUtils();
        setTitle(this.modo);
        cargarVista();
    }

    @Override
    public void longClick(int position) {

    }

    @Override
    public void click(int position) {
        Archivo archivo = this.archivos.get(position);
        if (archivo.isDirectory()) {
            Intent intent = new Intent(ActivityCopyMove.this, ActivityCopyMove.class);
            intent.putExtra("modo", modo);
            intent.putExtra("path", archivo.getAbsolutePath());
            intent.putExtra("paths", paths);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (path.equals(Archivo.ROOT_PATH))
            launchActivityCarpeta(fileUtils.getArchivosFromPath(paths).get(0).getParentFile());
        else
            finish();
    }

    private void cargarVista() {
        this.recyclerView = findViewById(R.id.recyclerCopyMove);
        this.recyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(lm);

        this.btnCancel = findViewById(R.id.btnCancel);
        this.btnCreateFolder = findViewById(R.id.btnCreateFolder);
        this.btnPaste = findViewById(R.id.btnPaste);

        getFiles();

        cargarListeners();
    }

    private void cargarListeners() {
        this.btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Archivo> archivos = fileUtils.getArchivosFromPath(paths);
                fileUtils.copyFileOrFlder(archivos, carpeta);
                adaptadorCarpeta.notifyDataSetChanged();
                if (modo.equals(getResources().getString(R.string.move)))
                    fileUtils.delete(archivos);
                launchActivityCarpeta(carpeta);
            }
        });

        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivityCarpeta(fileUtils.getArchivosFromPath(paths).get(0).getParentFile());
            }
        });

        this.btnCreateFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogCreateNewFolder(ActivityCopyMove.this, carpeta).show();
            }
        });
    }

    private void launchActivityCarpeta(File folder) {
        Intent intent = new Intent(ActivityCopyMove.this, ActivityCarpeta.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d("PATH", folder.getAbsolutePath());
        if (!folder.getAbsolutePath().equals(Archivo.ROOT_PATH))
            intent.putExtra("path", folder.getAbsolutePath());
        startActivity(intent);
    }

    private void getFiles() {
        archivos = new LinkedList<>();
        carpeta = new Archivo(path, this);
        File[] archivos = carpeta.listFiles();
        List<Archivo> fileList = new ArrayList<>();
        for (File file : archivos) {
            Log.d("FILES", file.getAbsolutePath());
            fileList.add(new Archivo(file.getAbsolutePath(), this));
        }
        this.archivos = this.fileUtils.getSortedFiles(fileList);
        this.adaptadorCarpeta = new AdaptadorCarpeta(this.archivos, this, this);
        this.recyclerView.setAdapter(this.adaptadorCarpeta);
    }

    public void updateFileList() {
        getFiles();
    }
}
