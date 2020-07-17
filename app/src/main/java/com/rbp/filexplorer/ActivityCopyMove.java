package com.rbp.filexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbp.filexplorer.modelo.Adaptador;
import com.rbp.filexplorer.modelo.FileUtils;
import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ActivityCopyMove extends AppCompatActivity implements Adaptador.CustomClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager lm;
    private Adaptador adaptador;

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
            launchActivityCarpeta();
        else
            finish();
    }

    private void cargarVista() {
        this.recyclerView = findViewById(R.id.recyclerCopyMove);
        this.recyclerView.setHasFixedSize(false);

        this.lm = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(this.lm);

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
                adaptador.notifyDataSetChanged();
                if (modo.equals(getResources().getString(R.string.move)))
                    fileUtils.delete(archivos);
                launchActivityCarpeta();
            }
        });

        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivityCarpeta();
            }
        });

        this.btnCreateFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogCreateNewFolder(ActivityCopyMove.this, carpeta).show();
            }
        });
    }

    private void launchActivityCarpeta() {
        Intent intent = new Intent(ActivityCopyMove.this, ActivityCarpeta.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getFiles() {
        archivos = new LinkedList<>();
        carpeta = new Archivo(path, this);
        File[] archivos = carpeta.listFiles();
        List<Archivo> fileList = new ArrayList<>();
        for (File file : archivos) {
            fileList.add(new Archivo(file.getAbsolutePath(), this));
        }
        this.archivos = this.fileUtils.getSortedFiles(fileList);
        this.adaptador = new Adaptador(this.archivos, this, this);
        this.recyclerView.setAdapter(this.adaptador);
    }

    public void updateFileList() {
        getFiles();
    }
}
