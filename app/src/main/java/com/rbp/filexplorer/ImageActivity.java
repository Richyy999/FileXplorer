package com.rbp.filexplorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.rbp.filexplorer.modelo.AdaptadorImagen;
import com.rbp.filexplorer.modelo.FileUtils;
import com.rbp.filexplorer.modelo.SwipeListener;
import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private View.OnClickListener onClickListener;

    private SwipeListener swipeListener;

    private Runnable showHideRunnable;
    private Runnable enableRunnable;

    private ViewPager viewPager;

    private List<Archivo> imagenes;

    private boolean isShowed;
    private boolean isEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isShowed = false;
        isEnable = true;
        getSupportActionBar().hide();
        cargarListenersAdapter();
        getFiles();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void cargarListenersAdapter() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideActionBar();
            }
        };

        swipeListener = new SwipeListener(this) {

            @Override
            public void swipeDown() {
                finish();
                super.swipeDown();
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

        int index = imagenes.indexOf(chosenImg);
        Log.d("INDEX", String.valueOf(index));

        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new AdaptadorImagen(imagenes, this, onClickListener, swipeListener));
        viewPager.setCurrentItem(index);
        setTitle(imagenes.get(index).getName());
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
                setTitle(currentImage.getName());
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
    }

    private void showHideActionBar() {
        if (isEnable) {
            if (isShowed) {
                getSupportActionBar().hide();
                isEnable = false;
                isShowed = false;
            } else {
                getSupportActionBar().show();
                new Handler().postDelayed(showHideRunnable, 1500);
                isShowed = true;
                isEnable = false;
            }
            new Handler().postDelayed(enableRunnable, 750);
        }
    }
}