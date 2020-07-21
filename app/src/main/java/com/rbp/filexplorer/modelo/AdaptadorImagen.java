package com.rbp.filexplorer.modelo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.rbp.filexplorer.R;
import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.util.List;

public class AdaptadorImagen extends PagerAdapter {

    private View.OnClickListener onClickListener;

    private SwipeListener swipeListener;

    private List<Archivo> imagenes;

    private Activity activity;

    public AdaptadorImagen(List<Archivo> imagenes, Activity activity, View.OnClickListener onClickListener, SwipeListener swipeListener) {
        this.imagenes = imagenes;
        this.activity = activity;
        this.onClickListener = onClickListener;
        this.swipeListener = swipeListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View v = activity.getLayoutInflater().inflate(R.layout.imagen, null);

        ImageView imageView = v.findViewById(R.id.imgGallery);

        Archivo imagen = imagenes.get(position);
        Log.d("LOAD IMG", imagen.getName());

        Glide.with(activity).load(imagen).into(imageView);

        PhotoViewAttacher photo = new PhotoViewAttacher(imageView);
        photo.setZoomable(true);
        photo.setOnClickListener(onClickListener);
        photo.update();

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imagenes.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
