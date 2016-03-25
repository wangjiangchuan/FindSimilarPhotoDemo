package com.meizu.similarphoto.infos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16-3-7.
 */

//用于存储相似照片的分组
public class PicSimilarInfo {
    //照片的创建时间
    private String mTakenTime;
    //存储一组相似照片
    private List<PicInfo> mList = new ArrayList<>();
    //相似照片的基础指纹，指纹指的是描述照片信息的long型整数
    private long mBaseFinger = 0;
    //该组照片的平均差异度
    private int mAverageDiffnum = 0;
    //构造函数
    public PicSimilarInfo(String takenTime, List<PicInfo> list) {
        this.mList = list;
        this.mTakenTime = takenTime;
    }

    public List<PicInfo> getmList() {
        return mList;
    }

    public String getmTakenTime() {
        return mTakenTime;
    }

    public void setBaseFinger(long baseFinger) {
        this.mBaseFinger = baseFinger;
    }

    public long getBaseFinger() {
        return mBaseFinger;
    }

    public void setAverageDiffnum(int averageDiffnum) {
        this.mAverageDiffnum = averageDiffnum;
    }

    public int getAverageDiffnum() {
        return mAverageDiffnum;
    }

}
