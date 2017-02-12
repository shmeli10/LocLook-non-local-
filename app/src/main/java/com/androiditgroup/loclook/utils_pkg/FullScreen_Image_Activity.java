package com.androiditgroup.loclook.utils_pkg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.androiditgroup.loclook.utils_pkg.MySingleton;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.tape_pkg.*;

import java.io.File;

/**
 * Created by OS1 on 18.12.2015.
 */
public class FullScreen_Image_Activity  extends     Activity
                                        implements  View.OnClickListener {

    private Context          context;
    private ImageLoader      imageLoader;
    private NetworkImageView imageView;

    private String imageLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // задать файл-компоновщик элементов окна
        setContentView(R.layout.fullscreen_image_layout);

        ///////////////////////////////////////////////////////////////////////////////////

        context = this;
        imageLoader = MySingleton.getInstance(context).getImageLoader();

        ///////////////////////////////////////////////////////////////////////////////////

        imageView = (NetworkImageView) findViewById(R.id.FullScreen_Image_IV);

        ///////////////////////////////////////////////////////////////////////////////////

        Intent intent = getIntent();

        imageLink = intent.getStringExtra("imagePath");

        ///////////////////////////////////////////////////////////////////////////////////

        setImage();
    }

    //
    private void setImage() {

        if((imageLink != null) && (!imageLink.equals(""))) {

            // кладем изображение в представление
            imageView.setImageUrl(imageLink, imageLoader);

            // задаем обработчик щелчка по изображению
            imageView.setOnClickListener(this);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View view) {
        finish();
    }

    //
    public void onBackPressed() {
        finish();
    }
}