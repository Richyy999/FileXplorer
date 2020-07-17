package com.rbp.filexplorer.modelo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rbp.filexplorer.R;
import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Adaptador extends RecyclerView.Adapter<Adaptador.MyHolder> {

    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private CustomClickListener customClickListener;

        private View view;

        private TextView lblNombre;
        private TextView lblTamano;
        private TextView lblFechaMod;

        private ImageView icon;

        public MyHolder(@NonNull View itemView, CustomClickListener customClickListener) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            lblNombre = itemView.findViewById(R.id.lblNombreArchivo);
            lblTamano = itemView.findViewById(R.id.lblTamanoArchivo);
            lblFechaMod = itemView.findViewById(R.id.lblFechaMod);

            view = itemView.findViewById(R.id.separador);

            icon = itemView.findViewById(R.id.iconoArchivo);

            this.customClickListener = customClickListener;
        }

        @Override
        public void onClick(View v) {
            try {
                customClickListener.click(getAdapterPosition());
            } catch (IndexOutOfBoundsException e) {
                Log.w("INDEX", String.valueOf(getAdapterPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            customClickListener.longClick(getAdapterPosition());
            return false;
        }
    }

    private List<Archivo> listaArchivos;

    private CustomClickListener customClickListener;

    private Context context;

    public Adaptador(List<Archivo> listaArchivos, CustomClickListener customClickListener, Context context) {
        this.listaArchivos = listaArchivos;
        this.customClickListener = customClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public Adaptador.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.archivo, parent, false);
        return new MyHolder(v, customClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Archivo archivo = this.listaArchivos.get(position);
        holder.lblNombre.setText(archivo.getName());

        holder.lblTamano.setText(archivo.getTamano());

        holder.lblFechaMod.setText(archivo.getLastModification());

        holder.view.setBackgroundColor(holder.lblTamano.getCurrentTextColor());

        holder.icon.setImageBitmap(archivo.getIcono());

        if (archivo.isSelected())
            holder.itemView.setBackgroundColor(this.context.getResources().getColor(R.color.gray));
        else
            holder.itemView.setBackground(null);
    }

    @Override
    public int getItemCount() {
        return listaArchivos.size();
    }

    public void removeAt(int position) {
        this.listaArchivos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, this.listaArchivos.size());
    }

    public interface CustomClickListener {

        void longClick(int position);

        void click(int position);
    }
}
