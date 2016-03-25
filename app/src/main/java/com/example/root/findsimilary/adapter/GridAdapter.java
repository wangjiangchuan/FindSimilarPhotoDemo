package com.example.root.findsimilary.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.root.findsimilary.R;
import com.example.root.findsimilary.tools.NativeImageLoader;
import com.example.root.findsimilary.view.MyGridView;
import com.meizu.similarphoto.infos.PicSimilarInfo;

/**
 * Created by wangjiangchuan on 16-3-7.
 */
public class GridAdapter extends BaseAdapter {
    private PicSimilarInfo mPicSimilarInfo;
    private Context mContext;
    private ContentResolver mCR;
    private int mWidth = 0;
    private int mHeith = 0;
    private MyGridView mGridView;

    public GridAdapter(PicSimilarInfo picSimilarInfo, Context context, MyGridView gridView) {
        this.mPicSimilarInfo = picSimilarInfo;
        this.mContext = context;
        this.mCR = context.getContentResolver();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;
        mHeith = dm.heightPixels;
        this.mGridView  = gridView;

        //屏幕适配
        if((mHeith * mWidth) > 1280 * 720){
            //1080p
            if(mWidth == 1080) {
                mGridView.setNumColumns(3);
            }
            if(mWidth == 1920){
                mGridView.setNumColumns(6);
            }

        }else {
            //720p
            if(mWidth == 720) {
                mGridView.setNumColumns(3);
            }
            if(mWidth == 1280){
                mGridView.setNumColumns(5);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mPicSimilarInfo.getmList().get(position);
    }

    @Override
    public int getCount() {
        return mPicSimilarInfo.getmList().size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        final ImageView imageView;
        if(mPicSimilarInfo.getmList().size() == 0) {
            Toast.makeText(mContext, "没有要显示的图片", Toast.LENGTH_SHORT).show();
        }
        if(convertView == null) {
            convertView = new ImageView(mContext);
            //convertView.setLayoutParams(new GridView.LayoutParams(200, 200));
        }
        imageView = (ImageView)convertView;
        //convertView.setLayoutParams(new GridView.LayoutParams(200, 200));
        //((ImageView) convertView).setScaleType(ImageView.ScaleType.FIT_XY);
        Bitmap bitmap = getImageFromCache(String.valueOf(mPicSimilarInfo.getmList().get(position).getmID()));


        if(bitmap != null) {
            ((ImageView)convertView).setImageBitmap(bitmap);
        }else {
            ((ImageView)convertView).setImageResource(R.drawable.friends_sends_pictures_no);
        }
        return convertView;
    }

    private Bitmap getImageFromCache(String key) {

        Bitmap bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(key);
        //屏幕是否发生了旋转
        if(bitmap == null) {
            //发生了旋转
            bitmap = MediaStore.Images.Thumbnails.getThumbnail(mCR,
                        Long.parseLong(key), MediaStore.Images.Thumbnails.MINI_KIND, null);
            NativeImageLoader.getInstance().addBitmapToMemoryCache(key, bitmap);

        }

        int width = 0, height = 0;
        //先判断是1080p还是720p
        if((mHeith * mWidth) > 1280 * 720){
            //1080p
            width = 300;
            height = 300;

        }else {
            //720p
            width = 200;
            height = 200;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap,width,height,false);
        return bitmap;
    }
}
