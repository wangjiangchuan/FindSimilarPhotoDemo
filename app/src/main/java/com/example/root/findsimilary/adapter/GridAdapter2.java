package com.example.root.findsimilary.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.findsimilary.R;
import com.example.root.findsimilary.activity.MainActivity3;
import com.example.root.findsimilary.tools.NativeImageLoader;
import com.example.root.findsimilary.view.MyGridView;
import com.meizu.similarphoto.infos.PicSimilarInfo;

/**
 * Created by wangjiangchuan on 16-3-7.
 */
public class GridAdapter2 extends BaseAdapter {
    private static final int LOAD_OK = 1;

    private PicSimilarInfo mPicSimilarInfo;
    private Context mContext;
    private ContentResolver mCR;
    private int mWidth = 0;
    private int mHeith = 0;
    private MyGridView mGridView;
    private LayoutInflater mInflater;

    public GridAdapter2(PicSimilarInfo picSimilarInfo, Context context, MyGridView gridView) {
        this.mPicSimilarInfo = picSimilarInfo;
        this.mContext = context;
        this.mCR = context.getContentResolver();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;
        mHeith = dm.heightPixels;
        this.mGridView  = gridView;
        this.mInflater = ((Activity) mContext).getLayoutInflater();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = mPicSimilarInfo.getmList().get(position).getPath();
                Intent intent = new Intent(mContext,MainActivity3.class);
                intent.putExtra("path",path);
                mContext.startActivity(intent);
            }
        });


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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.griditem,null);
            viewHolder = new ViewHolder();
            viewHolder.itemText = (TextView)convertView.findViewById(R.id.item_text3);
            viewHolder.itemView1 = (ImageView)convertView.findViewById(R.id.item_view1);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.itemView1.setImageBitmap(getImageFromCache(String.valueOf(mPicSimilarInfo.getmList().get(position).getmID())));
        viewHolder.itemText.setText(String.valueOf(mPicSimilarInfo.getmList().get(position).getmClearNum()) + "/"+ String.valueOf( mPicSimilarInfo.getmList().get(position).getTotal_num()));



        return convertView;
    }

    private Bitmap getSobelBitmap(int[] pixels, int height, int width){
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

    private Bitmap getImageFromCache(String key) {

        Bitmap bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(key);
        if(bitmap == null) {
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

    class ViewHolder{
        ImageView itemView1;
        ImageView itemView2;
        TextView itemText;
    }
}
