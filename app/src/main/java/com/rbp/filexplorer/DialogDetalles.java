package com.rbp.filexplorer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.util.Objects;

public class DialogDetalles extends Dialog {

    private TextView lblRuta;

    private Button btnOk;

    private Archivo archivo;

    private ActivityCarpeta activityCarpeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_detalles);
        cargarVista();
    }

    public DialogDetalles(ActivityCarpeta activity, Archivo archivo) {
        super(activity);
        this.activityCarpeta = activity;
        this.archivo = archivo;
    }

    private void cargarVista() {
        Objects.requireNonNull(this.getWindow()).setBackgroundDrawableResource(R.drawable.fondo_dialog_redondo);

        ImageView imgLogo = findViewById(R.id.imgIconoDetalles);
        imgLogo.setImageBitmap(this.archivo.getIcono());

        TextView lblNombre = findViewById(R.id.lblNombreDetalles);
        lblNombre.setText(this.archivo.getName());

        this.lblRuta = findViewById(R.id.lblRutaDetalles);
        this.lblRuta.setText(this.archivo.getAbsolutePath());

        TextView lblType = findViewById(R.id.lblType);
        lblType.setText(this.archivo.getTipo());

        TextView lblTamano = findViewById(R.id.lblTamanoDetalles);
        lblTamano.setText(this.archivo.getTamano());

        TextView lblMod = findViewById(R.id.lblLastModDetalles);
        lblMod.setText(this.archivo.getLastModification());

        TextView lblIsReadable = findViewById(R.id.lblIsReadble);
        if (this.archivo.canRead())
            lblIsReadable.setText(R.string.yes);
        else
            lblIsReadable.setText(R.string.no);

        TextView lblIsWritable = findViewById(R.id.lblIsWritables);
        if (this.archivo.canWrite())
            lblIsWritable.setText(R.string.yes);
        else
            lblIsWritable.setText(R.string.no);

        TextView lblIsHidden = findViewById(R.id.lblIsHidden);
        if (this.archivo.isHidden())
            lblIsHidden.setText(R.string.yes);
        else
            lblIsHidden.setText(R.string.no);

        this.btnOk = findViewById(R.id.btnOkDetalles);

        cargarListeners();
    }

    private void cargarListeners() {
        this.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        this.lblRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (archivo.isDirectory())
                    activityCarpeta.launchCarpeta(archivo);
                else
                    activityCarpeta.launchParentActivity(archivo);
                dismiss();
            }
        });

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activityCarpeta.clearModoSeleccion();
            }
        });
    }
}
