package com.example.root.findsimilar.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import com.example.root.findsimilar.infos.PicInfo;
import com.example.root.findsimilar.infos.PicItemInfo;
import com.example.root.findsimilar.infos.PicSimilarInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 16-3-21.
 */
public class ImageTools2 {

    public static int ALREADY_LOAD_FINGERPRINT = 0;
    public static List<PicInfo> mAllPic;

    //查找相似图片的接口
    public static List<PicSimilarInfo> FindSimilarPhoto(Context context, int value, int time_gap) {

        List<PicSimilarInfo> similarInfoList;
        if(ALREADY_LOAD_FINGERPRINT == 0){
            //先将所有的图片信息查找出来，放在一个List<PicInfo> allPic 数组中
            mAllPic = PictureUtils.getDCIMImageList(context);
            //将allPic中所有的图片的fingerprint计算出来
            calculateFingerPrint(mAllPic, context);
            ALREADY_LOAD_FINGERPRINT = 1;
        }
        //然后将所有的PicInfo进行分组
        List<PicItemInfo> picItemInfoList = sortedByTimeGap(mAllPic, time_gap);
        if (picItemInfoList == null) {
            return null;
        } else {
            //不为空查找相似图片
            similarInfoList = similarySort(picItemInfoList, value);
            //Log.e("照片数量",String.valueOf(similarInfoList.size()));
        }
        return similarInfoList;
    }


    //按照时间间隔进行排序
    public static List<PicItemInfo> sortedByTimeGap(List<PicInfo> picInfoList, int TIME_GAP) {
        HashMap<Integer, PicItemInfo> timegroup = new HashMap<Integer, PicItemInfo>();
        List<PicItemInfo> picItemInfoList;
        //下面是讲图片进行分组
        long oldTakenTime = 0;
        int groupCount = 0;
        for (int i = 0; i < picInfoList.size(); i++) {
            PicInfo picInfo = picInfoList.get(i);
            long takenTime = Long.valueOf(picInfo.getTakenTime());
            if (Math.abs(oldTakenTime - takenTime) >= TIME_GAP) {
                List<PicInfo> childList = new ArrayList<PicInfo>();
                picInfo.setGroupId(groupCount + 1);
                childList.add(picInfo);
                PicItemInfo picItemInfo = new PicItemInfo(picInfo.getTakenTime(), childList);
                timegroup.put(++groupCount, picItemInfo);

            } else {
                picInfo.setGroupId(groupCount);
                timegroup.get(groupCount).getPicInfoList().add(picInfo);

            }
            oldTakenTime = takenTime;
        }
        picItemInfoList = subGroupOfImage(timegroup);
        return picItemInfoList;
    }

    //按照时间间隔进行排序 测试函数
    public static List<PicSimilarInfo> sortedByTimeGap2(List<PicInfo> picInfoList, int TIME_GAP) {
        List<PicSimilarInfo> picItemInfoList = new ArrayList<>();
        //下面是讲图片进行分组
        long oldTakenTime = 0;
        long takentime;
        int groupIndex = -1;
        for(int i= 0; i < picInfoList.size(); i++){
            PicInfo picInfo = picInfoList.get(i);
            takentime = Long.valueOf(picInfo.getTakenTime());
            if(takentime - oldTakenTime > TIME_GAP){
                //不是同一组,第一张肯定不是同一组
                List<PicInfo> childList = new ArrayList<>();
                childList.add(picInfo);
                PicSimilarInfo picItemInfo = new PicSimilarInfo(picInfo.getTakenTime(),childList);
                groupIndex++;
                picItemInfoList.add(picItemInfo);

            }else{
                //是同一组
                picItemInfoList.get(groupIndex).getmList().add(picInfo);
            }
            oldTakenTime = takentime;
        }

        return picItemInfoList;
    }

    //将得到的hashmap分组，HashMap<Integer, PicItemInfo> 到 List<PicInfoIten>
    public static List<PicItemInfo> subGroupOfImage(HashMap<Integer, PicItemInfo> mGruopMap) {
        if (mGruopMap.size() == 0) {
            return null;
        }
        List<PicItemInfo> list = new ArrayList<PicItemInfo>();

        Iterator<Map.Entry<Integer, PicItemInfo>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, PicItemInfo> entry = it.next();
            PicItemInfo picItemInfo = entry.getValue();
            list.add(picItemInfo);
        }
        return list;
    }

    //查找相似的照片
    public static List<PicSimilarInfo> similarySort(List<PicItemInfo> info_list, int value) {
        List<PicSimilarInfo> similaries = new ArrayList<>();
        if (value < 0 || value > 64) {
            value = 15;
        }
        for (int i = 0; i < info_list.size(); i++) {

            List<PicInfo> list = new ArrayList<>(info_list.get(i).getPicInfoList());
            while (list.size() > 0) {
                //得到第一张图片
                int index = list.size() - 1;
                PicInfo picInfo = list.get(index);
                list.remove(index);
                List<PicInfo> list_temp = new ArrayList<PicInfo>();
                long orign_finger = picInfo.getmFingerPrint();
                //下面的循环中将能够分到一组的DiffNum加起来，求出分到一组的DiffNum的平均值
                for (int j = list.size() - 1; j >= 0; j--) {
                    long com_finger = list.get(j).getmFingerPrint();
                    int different_num = compareFingerPrint(orign_finger, com_finger);
                    if (different_num <= value) {
                        list_temp.add(list.get(j));
                        list.remove(j);
                    }
                }
                if (list_temp.size() > 0) {
                    list_temp.add(picInfo);
                    PicSimilarInfo picSimilarInfo = new PicSimilarInfo(picInfo.getTakenTime(), list_temp);
                    long basefinger = orign_finger;
                    picSimilarInfo.setBaseFinger(basefinger);
                    similaries.add(picSimilarInfo);
                }
            }
        }
        return similaries;
    }


    //计算所有的图片的fingerprint
    public static void calculateFingerPrint(List<PicInfo> list, Context context) {

        for (int i = 0; i < list.size(); i++) {
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                    list.get(i).getmID(), MediaStore.Images.Thumbnails.MICRO_KIND, null);

            int rateWidth = bitmap.getWidth() / 8;
            int rateHeight = bitmap.getHeight() / 8;
            //计算fingerprint
            getFingerPrint(bitmap,rateWidth, rateHeight);

            //计算sobel值
            Bitmap bitmap2 = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                    list.get(i).getmID(), MediaStore.Images.Thumbnails.MINI_KIND, null);
            SobelVague.convertGreyImg(bitmap2, list.get(i));
        }
    }

    public static long getFingerPrint(Bitmap image, int rateW, int rateH){

        int[] reducePixels = new int[64];
        //转换为灰度图像，同时获取平均灰度值
        int average = 0;
        for (int i = 0; i < rateH * 8; i = i + rateH) {
            for (int j = 0; j < rateW * 8; j = j + rateW) {
                int grey = image.getPixel(j,i);

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                reducePixels[8*(i / rateH) + j / rateW] = grey;
                average += grey;
            }
        }
        average /= 64;
        return getFingerPrint(reducePixels,average);
    }


    /**
     * 获得信息指纹
     *
     * @param pixels 灰度值矩阵
     * @param avg    平均值
     * @return 返回01串的十进制表示
     */
    private static long getFingerPrint(int[] pixels, int avg) {

        long fingerprint1 = 0;
        long fingerprint2 = 0;
        int index;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                index = 8 * i + j;
                if (pixels[index] >= avg) {
                    if (index < 32) {
                        fingerprint1 += (1 << index);
                    } else {
                        fingerprint2 += (1 << (index - 31));
                    }
                } else {
                    if (index < 32) {
                        fingerprint1 += (0 << index);
                    } else {
                        fingerprint2 += (0 << (index - 31));
                    }
                }
            }
        }
        //合并到最终的返回值fingerprint中
        long fingerprint = (fingerprint2 << 32) + fingerprint1;
        return fingerprint;
    }




    //比较指纹的函数
    public static int compareFingerPrint(long orgin1_fingerprint, long orign2_fingerprint) {
        long temp1 = 0x01;
        int count1 = 0;
        long result = orgin1_fingerprint ^ orign2_fingerprint;
        for (int i = 0; i < 64; i++) {
            if ((result & (temp1 << i)) == 0) {
                count1++;
            }
        }
        return (64 - count1);
    }


}
