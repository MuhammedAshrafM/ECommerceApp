package com.example.ecommerceapp.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.example.ecommerceapp.R;
import com.google.android.material.snackbar.Snackbar;

public class Utils {

    private Context context;
    private Snackbar snackbar;

    public Utils(Context context) {
        this.context = context;
    }

    public void snackBar(View view, String msg) {
        snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE).setAction("Action", null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snackbar.getView()
                    .setBackgroundColor(context.getColor(R.color.redColor));
        }
        snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
    }

    public void displaySnackBar(boolean show){
        if(show) {
            snackbar.show();
        }else {
            if(snackbar.isShown()){
                snackbar.dismiss();
            }
        }
    }
}
