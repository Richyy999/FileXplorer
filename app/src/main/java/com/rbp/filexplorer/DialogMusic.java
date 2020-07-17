package com.rbp.filexplorer;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.io.IOException;

public class DialogMusic extends Dialog {

    private Archivo archivo;

    private MediaPlayer mediaPlayer;

    private View btnPlayPause;

    private ImageView imgPlayPause;

    private TextView lblNombre;

    private SeekBar seekBar;

    private Handler handler;

    private boolean isPlaying;
    private boolean hasFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setContentView(R.layout.dialog_musica);
            cargarVistaSeekBar();
        } else {
            setContentView(R.layout.dialog_musica_sin_seekbar);
            cargarVistaSinSeekBar();
        }
    }

    public DialogMusic(@NonNull Activity activity, Archivo archivo) {
        super(activity);

        this.archivo = archivo;
        this.mediaPlayer = new MediaPlayer();
        loadFile();
        this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                hasFinish = false;
                reproducir();
            }
        });
        this.isPlaying = false;
        this.hasFinish = false;
        this.handler = new Handler();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cargarVistaSeekBar() {
        this.getWindow().setBackgroundDrawableResource(R.drawable.fondo_dialog_redondo);

        this.btnPlayPause = findViewById(R.id.btnPlayPause);

        this.imgPlayPause = findViewById(R.id.imgPlayPause);

        this.lblNombre = findViewById(R.id.lblNombreAudio);
        this.lblNombre.setText(archivo.getName());

        this.seekBar = findViewById(R.id.seekBar);
        this.seekBar.setMax(this.mediaPlayer.getDuration());

        cargarListeners(true);
    }

    private void cargarVistaSinSeekBar() {
        this.getWindow().setBackgroundDrawableResource(R.drawable.fondo_dialog_redondo);

        this.btnPlayPause = findViewById(R.id.btnPlayPauseSin);

        this.imgPlayPause = findViewById(R.id.imgPlayPauseSin);

        this.lblNombre = findViewById(R.id.lblNombreAudioSin);
        this.lblNombre.setText(archivo.getName());

        cargarListeners(false);
    }

    private void cargarListeners(boolean hasSeekBar) {
        this.btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducir();
            }
        });
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mediaPlayer.stop();
            }
        });
        if (hasSeekBar)
            this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser)
                        mediaPlayer.seekTo(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();
                seekBar.setProgress(0);
                imgPlayPause.setImageResource(R.drawable.play);
                isPlaying = false;
                hasFinish = true;
            }
        });
    }

    private void loadFile() {
        try {
            this.mediaPlayer.setDataSource(archivo.getPath());
            this.mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            dismiss();
        }
    }

    private void reproducir() {
        if (hasFinish)
            loadFile();
        else {
            if (this.isPlaying) {
                mediaPlayer.pause();
                this.isPlaying = false;
                this.imgPlayPause.setImageResource(R.drawable.play);
            } else {
                mediaPlayer.start();
                this.isPlaying = true;
                this.imgPlayPause.setImageResource(R.drawable.pausa);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                updateSeekBar();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateSeekBar() {
        this.seekBar.setProgress(this.mediaPlayer.getCurrentPosition());
        if (this.mediaPlayer.isPlaying()) {
            this.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            }, 800);
        }
    }
}
