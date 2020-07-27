package com.rbp.filexplorer.modelo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rbp.filexplorer.R;
import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.util.List;

public class AdaptadorCarpeta extends RecyclerView.Adapter<AdaptadorCarpeta.MyHolder> {

    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private CustomClickListener customClickListener;

        private View view;

        private TextView lblNombre;
        private TextView lblTamano;
        private TextView lblFechaMod;

        private ImageView icon;

        private ProgressBar progressBar;

        public MyHolder(@NonNull View itemView, CustomClickListener customClickListener) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            lblNombre = itemView.findViewById(R.id.lblNombreArchivo);
            lblTamano = itemView.findViewById(R.id.lblTamanoArchivo);
            lblFechaMod = itemView.findViewById(R.id.lblFechaMod);

            view = itemView.findViewById(R.id.separador);

            icon = itemView.findViewById(R.id.iconoArchivo);

            progressBar = itemView.findViewById(R.id.fileProgressbar);

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

    public AdaptadorCarpeta(List<Archivo> listaArchivos, CustomClickListener customClickListener, Context context) {
        this.listaArchivos = listaArchivos;
        this.customClickListener = customClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public AdaptadorCarpeta.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.archivo, parent, false);
        return new MyHolder(v, customClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        RequestListener requestListener = new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        };

        Archivo archivo = this.listaArchivos.get(position);
        holder.lblNombre.setText(archivo.getName());

        holder.lblTamano.setText(archivo.getTamano());

        holder.lblFechaMod.setText(archivo.getLastModification());

        holder.view.setBackgroundColor(holder.lblTamano.getCurrentTextColor());

        if (archivo.getTipo().contains("video"))
            Glide.with(context).load(archivo).listener(requestListener).into(holder.icon);
        else
            Glide.with(context).load(archivo.getIcono()).listener(requestListener).into(holder.icon);

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
