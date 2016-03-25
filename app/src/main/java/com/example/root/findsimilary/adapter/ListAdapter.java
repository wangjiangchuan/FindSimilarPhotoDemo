package com.example.root.findsimilary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.root.findsimilary.R;

import com.meizu.similarphoto.infos.PicSimilarInfo;
import com.example.root.findsimilary.view.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjaingchuan on 16-3-7.
 */
public class ListAdapter extends BaseAdapter{
    private List<PicSimilarInfo> mList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    private MyGridView mGridView;

    public ListAdapter(List<PicSimilarInfo> list, Context context) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);

        //调用NativeImageLoader去加载图片

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        //获取该组相似的照片,是GridView应该显示的数据
        PicSimilarInfo picSimilarInfo = mList.get(position);

        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.grid_layout, null);
            mGridView = (MyGridView)convertView.findViewById(R.id.grid_view);
            viewHolder.gridView = (MyGridView)convertView.findViewById(R.id.grid_view);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
            mGridView = (MyGridView)convertView.findViewById(R.id.grid_view);
        }

        GridAdapter adapter = new GridAdapter(picSimilarInfo,mContext,mGridView);
        viewHolder.gridView.setAdapter(adapter);

        return convertView;
    }

    class ViewHolder {
        MyGridView gridView;
    }
}
