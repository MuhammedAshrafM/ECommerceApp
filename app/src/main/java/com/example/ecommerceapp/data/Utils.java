package com.example.ecommerceapp.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.pojo.ProductModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class Utils {

    private Context context;
    private Snackbar snackbar;

    public Utils(Context context) {
        this.context = context;
    }

    public void snackBar(View view, String msg, int duration) {
        snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE).setAction("Action", null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            snackbar.getView()
//                    .setBackgroundColor(context.getColor(R.color.redColor));
        }
        switch (duration){
            case 0:
                snackbar.setDuration(BaseTransientBottomBar.LENGTH_LONG);
                break;

            case -1:
                snackbar.setDuration(BaseTransientBottomBar.LENGTH_SHORT);
                break;

            case -2:
                snackbar.setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE);
                break;

            default:

                break;
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

    public static Drawable convertLayoutToImage(Context mContext, int count, int drawableId) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_badge_icon, null);
        ((ImageView)view.findViewById(R.id.icon_badge)).setImageResource(drawableId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(mContext.getResources(), bitmap);
    }

    public static ArrayList<ProductModel> filter(ArrayList<ProductModel> productModels, String query){
        while (query.startsWith(" ")){

            if(query.length() != 1){
                query = query.substring(1);
            }
            else {
                return null;
            }
        }
        query = query.toLowerCase();
        ArrayList<ProductModel> filterModelsList = new ArrayList<>();
        for(ProductModel model: productModels){
            final String title = model.getTitle().toLowerCase();
            if(title.contains(query)){
                try {
                    filterModelsList.add(model);

                }catch (Exception e){
                }
            }
        }

        return filterModelsList;
    }
}
