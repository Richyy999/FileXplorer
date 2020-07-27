package com.rbp.filexplorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.rbp.filexplorer.modelo.DoubleClickListener;
import com.rbp.filexplorer.modelo.FileUtils;
import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final int DURATION = 200;

    private List<Archivo> videos;

    private AlphaAnimation show;
    private AlphaAnimation hide;

    private ConstraintLayout toolbar;
    private ConstraintLayout seekBarLayout;
    private ConstraintLayout playPauseLayout;

    private ImageView btnBack;
    private ImageView btnPlayPause;
    private ImageView btnPrevious;
    private ImageView btnNext;

    private SeekBar seekBar;

    private TextView lblTitle;
    private TextView lblCurrentTime;
    private TextView lblDuration;

    private View clickableView;

    private SimpleExoPlayer simpleExoPlayer;

    private Runnable seekbarRunnable;
    private Runnable hideElements;

    private Handler handler;

    private int index;

    private boolean isPlaying;
    private boolean isShowed;
    private boolean wasPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            cargarVistaSeekbar();
        else
            setContentView(R.layout.activity_video_player);
        getFiles();
        initPlayer();
        cargarListeners(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N);
        wasPlaying = true;
    }

    @Override
    protected void onResume() {
        if (simpleExoPlayer != null)
            simpleExoPlayer.setPlayWhenReady(wasPlaying);
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (simpleExoPlayer != null) {
            wasPlaying = simpleExoPlayer.getPlayWhenReady();
            simpleExoPlayer.setPlayWhenReady(false);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (simpleExoPlayer != null)
            simpleExoPlayer.stop();
        super.onDestroy();
    }

    private void loadAnimations() {
        show = new AlphaAnimation(0, 1);
        show.setDuration(DURATION);

        hide = new AlphaAnimation(1, 0);
        hide.setDuration(DURATION);

        isShowed = true;

        hideElements = new Runnable() {
            @Override
            public void run() {
                toolbar.setVisibility(View.GONE);
                seekBarLayout.setVisibility(View.GONE);
                playPauseLayout.setVisibility(View.GONE);
            }
        };

        showHideDisplay();
    }

    private void showHideDisplay() {
        if (isShowed) {
            isShowed = false;
            toolbar.startAnimation(hide);
            seekBarLayout.startAnimation(hide);
            playPauseLayout.startAnimation(hide);
            handler.postDelayed(hideElements, DURATION);
        } else {
            isShowed = true;

            toolbar.setVisibility(View.VISIBLE);
            toolbar.startAnimation(show);

            seekBarLayout.setVisibility(View.VISIBLE);
            seekBarLayout.startAnimation(show);

            playPauseLayout.setVisibility(View.VISIBLE);
            playPauseLayout.startAnimation(show);
        }
    }

    private void cargarVistaSeekbar() {
        setContentView(R.layout.activity_video_player_seek_bar);

        clickableView = findViewById(R.id.clickableSurface);

        toolbar = findViewById(R.id.toolbarVideo);
        seekBarLayout = findViewById(R.id.videoSeekbarLayout);
        playPauseLayout = findViewById(R.id.playButtonLayout);

        seekBar = findViewById(R.id.seekBarVideo);

        btnBack = findViewById(R.id.imgBackVideo);
        btnNext = findViewById(R.id.imgNext);
        btnPlayPause = findViewById(R.id.imgPlayPauseVideo);
        btnPrevious = findViewById(R.id.imgPrevious);

        lblCurrentTime = findViewById(R.id.lblCurrentTime);
        lblDuration = findViewById(R.id.lblDuration);
        lblTitle = findViewById(R.id.titleVideo);

        handler = new Handler();

        seekbarRunnable = new Runnable() {
            @Override
            public void run() {
                updateSeekbar();
            }
        };

        loadAnimations();
    }

    private void cargarListeners(boolean sdk) {
        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            lblTitle.setText(videos.get(index).getName());
                            long currentTime = simpleExoPlayer.getDuration();
                            lblDuration.setText(getCurrentTime(currentTime));
                            seekBar.setMax((int) currentTime);
                            btnPlayPause.setImageResource(R.drawable.pause_white);
                            updateButtons();
                            updateSeekbar();
                        }
                        isPlaying = true;
                        break;
                    case Player.STATE_ENDED:
                        playNext();
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        if (sdk) {
            clickableView.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onDoubleClick() {
                    reproducir();
                }

                @Override
                public void onClick() {
                    showHideDisplay();
                }
            });

            btnPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playPrevious();
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playNext();
                }
            });

            btnPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reproducir();
                }
            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser)
                        simpleExoPlayer.seekTo(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    private void reproducir() {
        if (isPlaying) {
            simpleExoPlayer.setPlayWhenReady(false);
            btnPlayPause.setImageResource(R.drawable.play_white);
            isPlaying = false;
        } else {
            simpleExoPlayer.setPlayWhenReady(true);
            btnPlayPause.setImageResource(R.drawable.pause_white);
            isPlaying = true;
        }
    }

    private void playNext() {
        if (index < videos.size() - 1) {
            index++;
            simpleExoPlayer.stop();
            simpleExoPlayer.prepare(getMediaSource());
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    private void playPrevious() {
        if (simpleExoPlayer.getCurrentPosition() >= simpleExoPlayer.getDuration() * 0.05)
            simpleExoPlayer.seekTo(0);
        else if (index > 0) {
            index--;
            simpleExoPlayer.stop();
            simpleExoPlayer.prepare(getMediaSource());
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    private void updateButtons() {
        if (index == 0 && simpleExoPlayer.getCurrentPosition() < simpleExoPlayer.getDuration() * 0.05) {
            btnPrevious.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.VISIBLE);
        } else if (index == videos.size() - 1) {
            btnNext.setVisibility(View.INVISIBLE);
            btnPrevious.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnPrevious.setVisibility(View.VISIBLE);
        }
    }

    private void initPlayer() {
        PlayerView playerView = findViewById(R.id.player);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(factory);

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);

        playerView.setUseController(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N));
        playerView.setPlayer(simpleExoPlayer);

        simpleExoPlayer.prepare(getMediaSource());
        simpleExoPlayer.setPlayWhenReady(true);

        isPlaying = true;
    }

    private void updateSeekbar() {
        long currentTime = simpleExoPlayer.getCurrentPosition();
        seekBar.setProgress((int) currentTime);
        lblCurrentTime.setText(getCurrentTime(currentTime));
        updateButtons();
        if (simpleExoPlayer.getPlayWhenReady())
            handler.postDelayed(seekbarRunnable, 100);
    }

    private String getCurrentTime(long currentTime) {
        String result;

        Date date = new Date(currentTime);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int hour = cal.get(Calendar.HOUR_OF_DAY) - 1;

        String minute = String.valueOf(cal.get(Calendar.MINUTE));
        String second = String.valueOf(cal.get(Calendar.SECOND));

        if (minute.length() < 2)
            minute = "0" + minute;

        if (second.length() < 2)
            second = "0" + second;

        if (hour > 0)
            result = hour + ":" + minute + ":" + second;
        else
            result = minute + ":" + second;

        return result;
    }

    private void getFiles() {
        String folderPath = getIntent().getStringExtra("folder");
        String chosenFilePath = getIntent().getStringExtra("chosenFile");
        Archivo chosenFile = new Archivo(chosenFilePath);

        FileUtils fileUtils = new FileUtils();

        videos = fileUtils.getSortedFiles(fileUtils.getVideos(new Archivo(folderPath), this));
        index = videos.indexOf(chosenFile);
    }

    private MediaSource getMediaSource() {
        DataSpec dataSpec = new DataSpec(Uri.fromFile(videos.get(index)));
        final FileDataSource dataSource = new FileDataSource();
        try {
            dataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            Log.w("LOAD VIDEO", Objects.requireNonNull(e.getMessage()));
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return dataSource;
            }
        };
        return new ExtractorMediaSource.Factory(factory).createMediaSource(dataSource.getUri());
    }
}