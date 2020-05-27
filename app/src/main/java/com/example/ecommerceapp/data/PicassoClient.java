package com.example.ecommerceapp.data;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.ImageView;

import com.example.ecommerceapp.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

public class PicassoClient {

    private Context context;
    private WeakReference<ContentResolver> contentResolverWeakReference;

    public static void loadImage(Context context, String url, ImageView imageView) {
        if (url.length() > 0 && url != null) {
            Picasso.with(context).load(url).into(imageView);
        } else {
            Picasso.with(context).load(R.mipmap.ic_launcher_logout).into(imageView);
        }

    }

    public static void loadCategoryImage(Context context, String url, ImageView imageView) {
        if (url.length() > 0 && url != null) {
            Picasso.with(context).load(url).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.mobiles).into(imageView);
        }

    }

    //target to save
    public static Target getTarget(final String name, final Context context){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + name);
                try {
                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }
}
