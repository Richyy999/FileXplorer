package com.rbp.filexplorer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rbp.filexplorer.modelo.FileUtils;
import com.rbp.filexplorer.modelo.entidad.Archivo;

public class DialogRename extends Dialog {

    private Button btnCancelar;
    private Button btnRename;

    private TextView lblTitulo;

    private TextInputLayout txtRenameLayout;
    private TextInputEditText txtRename;

    private Archivo archivo;

    private FileUtils fileUtils;

    private ActivityCarpeta activityCarpeta;

    private String newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rename);
        cargarVista();
    }

    public DialogRename(ActivityCarpeta activityCarpeta, Archivo archivo) {
        super(activityCarpeta);
        this.activityCarpeta = activityCarpeta;
        this.archivo = archivo;
        this.fileUtils = new FileUtils();
    }

    private void cargarVista() {
        this.getWindow().setBackgroundDrawableResource(R.drawable.fondo_dialog_redondo);

        this.btnRename = findViewById(R.id.btnRename);
        this.btnCancelar = findViewById(R.id.btnCancelRename);

        this.txtRenameLayout = findViewById(R.id.txtLayoutRename);
        this.txtRename = findViewById(R.id.txtRename);

        String hint = "";

        if (archivo.isDirectory())
            hint = this.activityCarpeta.getResources().getString(R.string.renameFolder);
        else
            hint = this.activityCarpeta.getResources().getString(R.string.renameFile);

        this.txtRenameLayout.setHint(hint);

        cargarListeners();
    }

    private void cargarListeners() {
        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procesarNombre();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activityCarpeta.updateFileList();
                activityCarpeta.clearModoSeleccion();
            }
        });
    }

    private void procesarNombre() {
        this.newName = txtRename.getText().toString().trim();
        if (this.newName.contains(".")) {
            procesarExtension();
        } else
            rename();
    }

    private void procesarExtension() {
        Archivo file = new Archivo(this.newName);
        String extension = file.getExtension();
        String tipo = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.replace(".", ""));
        if (tipo != null) {
            showExtensionAlert();
        } else
            rename();
    }

    private void showExtensionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activityCarpeta);
        builder.setTitle(R.string.changeExtension);
        builder.setMessage(R.string.changeExtensionMessage);
        builder.setPositiveButton(R.string.OK, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeExtension();
            }
        });
        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void changeExtension() {
        this.fileUtils.rename(this.archivo, this.newName);
        dismiss();
    }

    private void rename() {
        String extension = this.archivo.getExtension();
        this.fileUtils.rename(this.archivo, this.newName + extension);
        dismiss();
    }
}
