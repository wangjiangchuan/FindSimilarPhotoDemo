package com.example.root.findsimilary.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.root.findsimilary.R;

public class MainActivity3 extends AppCompatActivity {

    private Button back_but;
    private ImageView imageDisplay;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_display);

        back_but = (Button)findViewById(R.id.but_back);
        imageDisplay = (ImageView)findViewById(R.id.image_display);

        back_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        path = getIntent().getStringExtra("path");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);

        int swidth = 720/ options.outWidth;
        int sheight = 900/ options.outHeight;

        options.inSampleSize = swidth < sheight ? swidth : sheight;
        options.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(path,options);

        imageDisplay.setImageBitmap(bitmap);

    }
}
