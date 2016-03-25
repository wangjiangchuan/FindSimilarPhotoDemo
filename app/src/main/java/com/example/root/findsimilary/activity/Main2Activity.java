package com.example.root.findsimilary.activity;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.root.findsimilary.R;
import com.example.root.findsimilar.infos.PicInfo;
import com.example.root.findsimilar.infos.PicSimilarInfo;
import com.example.root.findsimilar.tools.PictureUtils;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private List<PicInfo> allPic;
    private List<PicSimilarInfo> allItemInfo;
    private List<Bitmap> bitmaps = new ArrayList<>();
    private List<Bitmap> bitmaps1 = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allPic = PictureUtils.getDCIMImageList(this);
        ContentResolver cr = this.getContentResolver();
        long start = System.currentTimeMillis();
        for(int i =0 ; i < 50; i++){
            bitmaps.add(MediaStore.Images.Thumbnails.getThumbnail(cr, allPic.get(i).getmID(), MediaStore.Images.Thumbnails.MINI_KIND, null));

        }
        long end = System.currentTimeMillis() - start;
        Log.e("time", String.valueOf(end));
        long start2 = System.currentTimeMillis();
        int width, height;
        for(int i = 0; i < 50; i++){
            width = bitmaps.get(i).getWidth();
            height = bitmaps.get(i).getHeight();
            float scale_width = 8.0f / width;
            float scale_height = 8.0f / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scale_width, scale_height);
            bitmaps1.add(Bitmap.createBitmap(bitmaps.get(i), 0, 0, width, height, matrix, false));
        }
        long end2 = System.currentTimeMillis() - start2;
        Log.e("time", String.valueOf(end2));

    }
}
