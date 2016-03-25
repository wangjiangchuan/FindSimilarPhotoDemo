package com.example.root.findsimilar.infos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16-3-7.
 */

/*
用来简化PicInfo的
在这个类中只包含图片的路径和名称信息
维护的是一个包含图片信息的数组
 */
public class PicItemInfo {

    //记录改组照片的创建时间
    private String mGroupByTime;
    //存储时间间隔之内的一组照片信息，照片信息保存在PicInfo中
    private List<PicInfo> mPicInfoList = new ArrayList<PicInfo>();

    //构造函数
    public PicItemInfo(String groupByTime, List<PicInfo> picInfoList) {
        this.mGroupByTime = groupByTime;
        this.mPicInfoList = picInfoList;
    }

    public List<PicInfo> getPicInfoList() {
        return mPicInfoList;
    }

    public void setPicInfoList(List<PicInfo> picInfoList) {
        this.mPicInfoList = picInfoList;
    }

    public void setGroupByTime(String groupByTime) {
        this.mGroupByTime = groupByTime;
    }

    public String getGroupByTime() {
        return mGroupByTime;
    }
}
