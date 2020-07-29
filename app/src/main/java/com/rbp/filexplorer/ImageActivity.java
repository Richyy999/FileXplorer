package com.rbp.filexplorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.rbp.filexplorer.modelo.AdaptadorImagen;
import com.rbp.filexplorer.modelo.FileUtils;
import com.rbp.filexplorer.modelo.SwipeListener;
import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private static final float COORD_Y_HIDDEN = -200f;

    private static final int DURATION = 300;

    private View.OnClickListener onClickListener;

    private Runnable showHideRunnable;
    private Runnable enableRunnable;

    private TranslateAnimation show;
    private TranslateAnimation hide;

    private ViewPager viewPager;

    private TextView lblTitle;

    private ConstraintLayout toolbar;

    private ImageView btnBack;

    private List<Archivo> imagenes;

    private boolean isShowed;
    private boolean isEnable;

    private float coordX;
    private float coordY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        isShowed = false;
        isEnable = true;
        toolbar = findViewById(R.id.toolbarImagen);
        coordX = toolbar.getX();
        coordY = toolbar.getY();
        cargarListenersAdapter();
        loadAnimatios();
        toolbar.setY(COORD_Y_HIDDEN);
        getFiles();
    }

    private void loadAnimatios() {
        show = new TranslateAnimation(coordX, coordX, COORD_Y_HIDDEN, coordY);
        show.setDuration(DURATION);

        hide = new TranslateAnimation(coordX, coordX, coordY, COORD_Y_HIDDEN);
        hide.setDuration(DURATION);
    }

    private void cargarListenersAdapter() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideActionBar();
            }
        };
    }

    private void getFiles() {
        String folderPath = getIntent().getStringExtra("folder");
        Archivo folder = new Archivo(folderPath);

        FileUtils fileUtils = new FileUtils();

        imagenes = fileUtils.getImages(folder, this);

        imagenes = fileUtils.getSortedFiles(imagenes);

        cargarVista();
    }

    private void cargarVista() {
        String chosenImgPath = getIntent().getStringExtra("img");
        Archivo chosenImg = new Archivo(chosenImgPath, this);

        lblTitle = findViewById(R.id.titleImage);

        btnBack = findViewById(R.id.imgBackGalery);

        int index = imagenes.indexOf(chosenImg);
        Log.d("INDEX", String.valueOf(index));

        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new AdaptadorImagen(imagenes, this, onClickListener));
        viewPager.setCurrentItem(index);

        lblTitle.setText(imagenes.get(index).getName());

        cargarListeners();
    }

    private void cargarListeners() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Archivo currentImage = imagenes.get(position);
                lblTitle.setText(currentImage.getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        showHideRunnable = new Runnable() {
            @Override
            public void run() {
                showHideActionBar();
            }
        };

        enableRunnable = new Runnable() {
            @Override
            public void run() {
                isEnable = true;
            }
        };

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showHideActionBar() {
        if (isEnable) {
            if (isShowed) {
                toolbar.startAnimation(hide);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toolbar.setY(COORD_Y_HIDDEN);
                    }
                }, DURATION);
                isEnable = false;
                isShowed = false;
            } else {
                toolbar.startAnimation(show);
                toolbar.setY(coordY);
                new Handler().postDelayed(showHideRunnable, 1500);
                isShowed = true;
                isEnable = false;
            }
            new Handler().postDelayed(enableRunnable, 750);
        }
    }
}