package com.rbp.filexplorer;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class DialogDeleteing extends Dialog {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete);
    }


    public DialogDeleteing(@NonNull Activity activity) {
        super(activity);
    }
}
