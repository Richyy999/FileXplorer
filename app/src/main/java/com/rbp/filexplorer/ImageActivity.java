package com.rbp.filexplorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.rbp.filexplorer.modelo.entidad.Archivo;

public class ImageActivity extends AppCompatActivity {

    private Runnable runnable;

    private ImageView img;

    private boolean isShowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        img = findViewById(R.id.bigImageView);
        String path = getIntent().getStringExtra("img");
        Archivo file = new Archivo(path);
        img.setImageBitmap(file.getIcono());
        setTitle(file.getName());
        runnable = new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().hide();
                isShowed = false;
            }
        };
        getSupportActionBar().hide();
        isShowed = false;
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideActionBar();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
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