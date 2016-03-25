package com.meizu.similarphoto.infos;

/**
 * Created by root on 16-3-7.
 */
public class BasicInfo implements Comparable<BasicInfo> {
    //下面是类存储的基础信息
    public String mOriginName;
    //分组ID
    public long mGroupId;

    //重写的比较排序的方法
    @Override
    public int compareTo(BasicInfo another) {
        return 0;
    }

    //originName

    public void setOriginName(String originName) {
        this.mOriginName = originName;
    }

    public String getOriginName() {
        return mOriginName;
    }

    //groupId

    public void setGroupId(long groupId) {
        this.mGroupId = groupId;
    }

    public long getGroupId() {
        return mGroupId;
    }
}
