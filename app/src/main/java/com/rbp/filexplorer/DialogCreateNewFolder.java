package com.rbp.filexplorer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import com.rbp.filexplorer.modelo.FileUtils;
import com.rbp.filexplorer.modelo.entidad.Archivo;

public class DialogCreateNewFolder extends Dialog {

    private Button btnCancel;
    private Button btnCrear;

    private TextInputEditText txtName;

    private ActivityCarpeta activityCarpeta;

    private ActivityCopyMove activityCopyMove;

    private Archivo archivo;

    private FileUtils fileUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_crear_carpeta);
        cargarVista();
    }

    public DialogCreateNewFolder(ActivityCarpeta activityCarpeta, Archivo archivo) {
        super(activityCarpeta);
        this.activityCarpeta = activityCarpeta;
        this.archivo = archivo;
        this.fileUtils = new FileUtils();
    }

    public DialogCreateNewFolder(ActivityCopyMove activityCopyMove, Archivo archivo) {
        super(activityCopyMove);
        this.activityCopyMove = activityCopyMove;
        this.archivo = archivo;
        this.fileUtils = new FileUtils();
    }

    private void cargarVista() {
        this.getWindow().setBackgroundDrawableResource(R.drawable.fondo_dialog_redondo);
        this.txtName = findViewById(R.id.txtCreateNewFolder);

        this.btnCancel = findViewById(R.id.btnCancelCreateFolder);
        this.btnCrear = findViewById(R.id.btnCreateNewFolder);

        cargarListeners();
    }

    private void cargarListeners() {
        this.btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtName.getText().toString().trim().equals(""))
                    crearCarpeta();
            }
        });

        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (activityCarpeta != null) {
                    activityCarpeta.updateFileList();
                    activityCarpeta.clearModoSeleccion();
                } else
                    activityCopyMove.updateFileList();
            }
        });
    }

    private void crearCarpeta() {
        String folderName = txtName.getText().toString().trim();
        Archivo folder = new Archivo(this.archivo.getAbsolutePath() + "/" + folderName);
        if (this.fileUtils.createFolder(folder))
            dismiss();
    }
}
