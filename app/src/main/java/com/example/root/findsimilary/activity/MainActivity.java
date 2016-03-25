package com.example.root.findsimilary.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.root.findsimilary.R;
import com.example.root.findsimilary.adapter.ListAdapter;
import com.example.root.findsimilar.infos.PicSimilarInfo;
import com.example.root.findsimilar.tools.ImageTools;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    //消息处理的Flag
    private final static int COMPARE_END = 1;
    private final static int NONE_PIC = 2;
    //排序分组的间隔， 时间常数
    private int TIME_GAP = 60000;
    //进度条
    private ProgressDialog mProgressDialog;
    //查找相似的结果
    private List<PicSimilarInfo> mSimilaryList = new ArrayList<>();
    //adapter
    private ListAdapter mAdapter;
    private GridView mGridView;
    //view
    private ListView mListView;
    private Button mFindBut;
    private EditText mInputNum;
    private EditText mInputTime;


    //消息处理函数
    private Handler nHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //照片库中没有照片
                case NONE_PIC:
                    new AlertDialog.Builder(MainActivity.this).setTitle("").setMessage("手机中没有照片")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }}).show();
                    break;
                
                case COMPARE_END:
                    //查找相似完毕
                    mProgressDialog.dismiss();

                    if (mSimilaryList.size() == 0) {
                        Toast.makeText(MainActivity.this, "没有相似的图片", Toast.LENGTH_SHORT).show();
                    }


                    mAdapter = new ListAdapter(mSimilaryList, MainActivity.this);
                    mListView.setAdapter(mAdapter);

                    //重新查找按钮的监听事件
                    mFindBut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //获取EditText中的数值，EditText设置成只接受 数字
                            initOnClick();
                        }
                    });
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (GridView)findViewById(R.id.grid_view);
        mListView = (ListView) findViewById(R.id.group_list);
        mFindBut = (Button) findViewById(R.id.find_but);
        mInputNum = (EditText) findViewById(R.id.input_text);
        mInputTime = (EditText) findViewById(R.id.input_text2);
        mProgressDialog = ProgressDialog.show(this, null, "正在查找相册中相似的图片");

        //启用新的线程查找相似的图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                mSimilaryList = ImageTools.FindSimilarPhoto(MainActivity.this, 15, TIME_GAP);
                long end = System.currentTimeMillis() - start;
                Log.e("时间",String.valueOf(end));
                if (mSimilaryList == null) {
                    //照片库中没有照片
                    nHandler.sendEmptyMessage(NONE_PIC);
                }else{
                    nHandler.sendEmptyMessage(COMPARE_END);
                }

            }
        }).start();



    }

    private void initOnClick() {

        String value = mInputNum.getText().toString();
        String time = mInputTime.getText().toString();
        if (value.equals("") || time.equals("")) {
            Toast.makeText(MainActivity.this, "输入的数值不正确", Toast.LENGTH_SHORT).show();
        } else {
            int similar_int = Integer.parseInt(value);
            int time_gap = Integer.parseInt(time);
            if (similar_int > 64 || similar_int < 1 || time_gap < 1 || time_gap > 60) {
                Toast.makeText(MainActivity.this, "输入的数值不正确", Toast.LENGTH_SHORT).show();
            } else {
                mInputNum.setText(value);
                mInputTime.setText(time);
                //毫秒
                TIME_GAP = time_gap * 60000;

                mSimilaryList = ImageTools.FindSimilarPhoto(MainActivity.this, similar_int, TIME_GAP);

                if (mSimilaryList.size() == 0) {
                    Toast.makeText(MainActivity.this, "没有相似的图片", Toast.LENGTH_SHORT).show();
                }
                //更新ListView
                mAdapter = new ListAdapter(mSimilaryList, MainActivity.this);
                mListView.setAdapter(mAdapter);
            }
        }
    }

}