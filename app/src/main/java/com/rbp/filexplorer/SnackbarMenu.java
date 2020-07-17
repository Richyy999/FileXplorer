package com.rbp.filexplorer;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.rbp.filexplorer.modelo.FileUtils;
import com.rbp.filexplorer.modelo.entidad.Archivo;

import java.util.List;

import static com.rbp.filexplorer.modelo.FileUtils.getFilesPath;

public class SnackbarMenu {

    private Snackbar snackbar;

    private Snackbar.SnackbarLayout layout;

    private LinearLayout menuMore;

    private View snackbarView;
    private View opacityPane;

    private LinearLayout btnMore;
    private LinearLayout btnCopy;
    private LinearLayout btnMove;
    private LinearLayout btnDelete;

    private TextView btnRename;
    private TextView btnNewFolder;
    private TextView btnOpen;
    private TextView btnDetails;

    private Animation hide;
    private Animation show;

    private ActivityCarpeta activityCarpeta;

    private List<Archivo> archivos;

    private FileUtils fileUtils;

    private boolean isDisplayed;

    private float coordX;
    private float coordY;

    public SnackbarMenu(View view, final List<Archivo> archivos, final ActivityCarpeta activityCarpeta) {
        this.snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
        this.archivos = archivos;
        this.activityCarpeta = activityCarpeta;
        this.fileUtils = new FileUtils();
        this.snackbarView = this.snackbar.getView();
        snackbarView.setBackground(null);

        this.layout = (Snackbar.SnackbarLayout) this.snackbar.getView();
        this.layout.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);
        View customLayout = this.activityCarpeta.getLayoutInflater().inflate(R.layout.snackbar_menu, null);

        this.btnMore = customLayout.findViewById(R.id.btnMore);
        this.btnCopy = customLayout.findViewById(R.id.btnCopy);
        this.btnMove = customLayout.findViewById(R.id.btnMove);
        this.btnDelete = customLayout.findViewById(R.id.btnDelete);

        this.opacityPane = customLayout.findViewById(R.id.opacityPaneSnackbar);

        this.menuMore = customLayout.findViewById(R.id.menuMore);
        this.menuMore.setVisibility(View.GONE);

        this.btnRename = customLayout.findViewById(R.id.btnRenamePopup);
        this.btnNewFolder = customLayout.findViewById(R.id.btnCreateFolderPopup);
        this.btnOpen = customLayout.findViewById(R.id.btnOpenAppPopup);
        this.btnDetails = customLayout.findViewById(R.id.btnDetails);

        loadAnimations();
        hide();

        this.layout.addView(customLayout);
        this.layout.setPadding(0, 0, 0, 0);

        cargarListeners();
    }

    private void cargarListeners() {
        this.btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivityCopyMove(activityCarpeta.getResources().getString(R.string.copy));
            }
        });

        this.btnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivityCopyMove(activityCarpeta.getResources().getString(R.string.move));
            }
        });

        this.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityCarpeta.showDeleteDialog();
            }
        });

        this.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDisplayed)
                    hide();
                else
                    show();
            }
        });

        this.opacityPane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        this.btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogRename(activityCarpeta, archivos.get(0)).show();
            }
        });

        this.btnNewFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogCreateNewFolder(activityCarpeta, activityCarpeta.getCarpeta()).show();
            }
        });

        this.btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUtils.openFile(archivos.get(0), activityCarpeta);
                activityCarpeta.clearModoSeleccion();
            }
        });

        this.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogDetalles(activityCarpeta, archivos.get(0)).show();
                activityCarpeta.clearModoSeleccion();
            }
        });
    }

    private void loadAnimations() {
        this.coordX = this.menuMore.getX();
        this.coordY = this.menuMore.getY();

        hide = new TranslateAnimation(this.coordX, this.coordX, coordY, 700);
        hide.setDuration(400);

        show = new TranslateAnimation(this.coordX, this.coordX, 700, this.coordY);
        show.setDuration(400);
    }

    private void hide() {
        this.isDisplayed = false;
        this.opacityPane.setVisibility(View.GONE);
        this.btnMore.setEnabled(false);
        this.menuMore.startAnimation(hide);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                menuMore.setY(700);
                menuMore.setVisibility(View.GONE);
                btnMore.setEnabled(true);
            }
        }, 400);
    }

    private void show() {
        this.isDisplayed = true;
        this.menuMore.setVisibility(View.VISIBLE);
        this.menuMore.startAnimation(show);
        this.menuMore.setY(this.coordY);
        this.btnMore.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnMore.setEnabled(true);
                opacityPane.setVisibility(View.VISIBLE);
            }
        }, 400);
    }

    private void launchActivityCopyMove(String modo) {
        Intent intent = new Intent(activityCarpeta.getApplicationContext(), ActivityCopyMove.class);
        intent.putExtra("modo", modo);
        intent.putExtra("paths", getFilesPath(this.archivos));
        activityCarpeta.startActivity(intent);
    }

    public void updateMenu() {
        if (this.archivos.size() > 1) {
            this.btnDetails.setEnabled(false);
            this.btnDetails.setBackgroundColor(activityCarpeta.getResources().getColor(R.color.lightGray));
            this.btnDetails.setTextColor(activityCarpeta.getResources().getColor(R.color.darkGray));

            this.btnOpen.setEnabled(false);
            this.btnOpen.setBackgroundColor(activityCarpeta.getResources().getColor(R.color.lightGray));
            this.btnOpen.setTextColor(activityCarpeta.getResources().getColor(R.color.darkGray));

            this.btnRename.setEnabled(false);
            this.btnRename.setBackgroundColor(activityCarpeta.getResources().getColor(R.color.lightGray));
            this.btnRename.setTextColor(activityCarpeta.getResources().getColor(R.color.darkGray));
        } else {
            this.btnDetails.setEnabled(true);
            this.btnDetails.setBackgroundColor(activityCarpeta.getResources().getColor(R.color.white));
            this.btnDetails.setTextColor(activityCarpeta.getResources().getColor(R.color.black));

            this.btnOpen.setEnabled(!this.archivos.get(0).isDirectory());
            if (this.btnOpen.isEnabled()) {
                this.btnOpen.setBackgroundColor(activityCarpeta.getResources().getColor(R.color.white));
                this.btnOpen.setTextColor(activityCarpeta.getResources().getColor(R.color.black));
            } else {
                this.btnOpen.setBackgroundColor(activityCarpeta.getResources().getColor(R.color.lightGray));
                this.btnOpen.setTextColor(activityCarpeta.getResources().getColor(R.color.darkGray));
            }

            this.btnRename.setEnabled(true);
            this.btnRename.setBackgroundColor(activityCarpeta.getResources().getColor(R.color.white));
            this.btnRename.setTextColor(activityCarpeta.getResources().getColor(R.color.black));
        }
    }

    public Snackbar getSnackbar() {
        return snackbar;
    }
}
