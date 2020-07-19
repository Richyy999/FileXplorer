package com.rbp.filexplorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.rbp.filexplorer.modelo.FileUtils;
import com.rbp.filexplorer.modelo.SwipeListener;
import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private Runnable runnable;

    private ImageView img;

    private List<Archivo> imagenes;

    private boolean isShowed;

    private int index;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFiles();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void getFiles() {
        String folderPath = getIntent().getStringExtra("folder");
        Archivo folder = new Archivo(folderPath);

        FileUtils fileUtils = new FileUtils();

        imagenes = fileUtils.getImages(folder, this);

        cargarVista();
    }

    private void cargarVista() {
        img = findViewById(R.id.bigImageView);

        String chosenImgPath = getIntent().getStringExtra("img");
        Archivo chosenImg = new Archivo(chosenImgPath, this);

        setTitle(chosenImg.getName());

        img.setImageBitmap(chosenImg.getIcono());

        index = imagenes.indexOf(chosenImg);

        cargarListeners();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void cargarListeners() {
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideActionBar();
            }
        });

        img.setOnTouchListener(new SwipeListener(this) {
            @Override
            public void swipeDown() {
                finish();
                super.swipeDown();
            }

            @Override
            public void swipeLeft() {
                getPreviousImg();
                super.swipeLeft();
            }

            @Override
            public void swipeRight() {
                getNextImage();
                super.swipeRight();
            }
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                showHideActionBar();
            }
        };
    }

    private void getPreviousImg() {
        if (index > 0) {
            index--;
            Archivo archivo = imagenes.get(index);
            img.setImageBitmap(archivo.getIcono());
            setTitle(archivo.getName());
        }
    }

    public void getNextImage() {
        if (index < imagenes.size()) {
            index++;
            Archivo archivo = imagenes.get(index);
            img.setImageBitmap(archivo.getIcono());
            setTitle(archivo.getName());
        }
    }

    private void showHideActionBar() {
        if (isShowed) {
            getSupportActionBar().hide();
            isShowed = false;
        } else {
            getSupportActionBar().show();
            new Handler().postDelayed(runnable, 1500);
            isShowed = true;
        }
    }
}