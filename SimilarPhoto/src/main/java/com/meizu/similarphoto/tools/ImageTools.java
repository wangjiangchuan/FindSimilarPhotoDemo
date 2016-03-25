package com.meizu.similarphoto.tools;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.provider.MediaStore;

import com.meizu.similarphoto.infos.PicInfo;
import com.meizu.similarphoto.infos.PicItemInfo;
import com.meizu.similarphoto.infos.PicSimilarInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangjiangchuan on 16-3-7.
 */
public class ImageTools {

    //用于西面的接口一，防止数据重新计算，耗时增加
    private static boolean ALREADY_FIND_PICS = false;
    private static List<PicInfo> mAllPic;

    //查找相似图片的接口
    public static List<PicSimilarInfo> FindSimilarPhoto(Context context, int value, int time_gap) {

        List<PicSimilarInfo> similarInfoList;
        if(ALREADY_FIND_PICS == false){
            //先将所有的图片信息查找出来，放在一个List<PicInfo> allPic 数组中
            mAllPic = PictureUtils.getDCIMImageList(context);
            ALREADY_FIND_PICS = true;
        }
        //然后将所有的PicInfo进行分组
        List<PicItemInfo> picItemInfoList = ImageTools.sortedByTimeGap(mAllPic, time_gap);
        if (picItemInfoList == null) {
            return null;
        } else {
            //计算指纹
            ImageTools.calculateFingerPrint(picItemInfoList, context);
            //不为空查找相似图片
            similarInfoList = ImageTools.similarySort(picItemInfoList, value);
        }
        return similarInfoList;
    }

    /**
     * 对指定的图像进行相似度查找
     * @param allPic 存储制定的图像信息
     * @param value 差异度阈值
     * @param time_gap 时间分组阈值
     * @return 返回相似度分组的图片信息
     * 如果返回值为 null 有以下几种可能
     * 1. allPic = null;
     * 2. allPic.size()==0;
     * 3. 没有相似的图片
     */
    public static List<PicSimilarInfo> FindSimilarPhoto(Context context, List<String> allPic, int value, int time_gap){
        List<PicSimilarInfo> similarInfoList;
        if(allPic == null || allPic.size() == 0){
            return null;
        }else{
            //将路径换成ID信息，保存起来
            List<PicInfo> picInfos = PictureUtils.pathToID(allPic, context);
            //将所有的PicInfo按时间阈值进行分组
            List<PicItemInfo> picItemInfoList = sortedByTimeGap(picInfos, time_gap);
            //计算指纹信息
            calculateFingerPrint(picItemInfoList, context);

            similarInfoList = similarySort(picItemInfoList, value);
            if(similarInfoList == null || similarInfoList.size() == 0){
                return null;
            }
        }
        return similarInfoList;
    }

    //按照时间间隔进行排序
    public static List<PicItemInfo> sortedByTimeGap(List<PicInfo> picInfoList, int TIME_GAP) {
        if(picInfoList == null) {
            return null;
        }

        List<PicItemInfo> picItemInfoList = new ArrayList<>();
        //下面是讲图片进行分组
        long oldTakenTime = 0;
        long takenTime;
        int groupIndex = -1;
        for(int i= 0; i < picInfoList.size(); i++){
            PicInfo picInfo = picInfoList.get(i);
            takenTime = Long.valueOf(picInfo.getTakenTime());
            if(takenTime - oldTakenTime > TIME_GAP){
                //不是同一组,第一张肯定不是同一组
                List<PicInfo> childList = new ArrayList<>();
                childList.add(picInfo);
                PicItemInfo picItemInfo = new PicItemInfo(picInfo.getTakenTime(),childList);
                groupIndex++;
                picItemInfoList.add(picItemInfo);

            }else{
                //是同一组
                picItemInfoList.get(groupIndex).getPicInfoList().add(picInfo);
            }
            oldTakenTime = takenTime;
        }
        //去除掉只有一张图片的分组
        for(int i =picItemInfoList.size() -1 ; i >= 0; i--){
            if(picItemInfoList.get(i).getPicInfoList().size() == 1){
                picItemInfoList.remove(i);
            }
        }
        return picItemInfoList;
    }

    //查找相似的照片
    public static List<PicSimilarInfo> similarySort(List<PicItemInfo> info_list, int value) {
        List<PicSimilarInfo> similaries = new ArrayList<>();
        if (value < 0 || value > 64) {
            value = 15;
        }
        for (int i = 0; i < info_list.size(); i++) {
            List<PicInfo> list = new ArrayList<>(info_list.get(i).getPicInfoList());
            if(list.size() == 1){
                continue;
            }
            while (list.size() > 0) {
                //得到第一张图片
                int index = list.size() - 1;
                PicInfo picInfo = list.get(index);
                list.remove(index);
                List<PicInfo> list_temp = new ArrayList<PicInfo>();
                long orign_finger = picInfo.getmFingerPrint();
                for (int j = list.size() - 1; j >= 0; j--) {
                    long com_finger = list.get(j).getmFingerPrint();
                    int different_num1 = ImageTools.compareFingerPrint(orign_finger, com_finger);
                    if (different_num1 <= value ) {
                        list_temp.add(list.get(j));
                        list.remove(j);
                        orign_finger = com_finger;
                    }
                }
                if (list_temp.size() > 0) {
                    list_temp.add(picInfo);
                    PicSimilarInfo picSimilarInfo = new PicSimilarInfo(picInfo.getTakenTime(), list_temp);
                    picSimilarInfo.setBaseFinger(orign_finger);
                    similaries.add(picSimilarInfo);
                }
            }
        }
        return similaries;
    }

    //计算所有的图片的fingerprint
    public static void calculateFingerPrint(List<PicItemInfo> list, Context context) {
        ContentResolver cr = context.getContentResolver();
        for (int i = 0; i < list.size(); i++) {
            for(int j = 0; j < list.get(i).getPicInfoList().size(); j++){
                if(list.get(i).getPicInfoList().get(j).getFingerState() == false){
                    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr,
                            list.get(i).getPicInfoList().get(j).getmID(), MediaStore.Images.Thumbnails.MINI_KIND, null);
                    //设置缩放比例
                    float scale_width = 8.0f / bitmap.getWidth();
                    float scale_height = 8.0f / bitmap.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale_width, scale_height);
                    Bitmap reduce_bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                    getFingerPrint(reduce_bitmap, list.get(i).getPicInfoList().get(j));

                    /*Bitmap bitmap2 = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                    Long.parseLong(list.get(i).getmID()), MediaStore.Images.Thumbnails.MINI_KIND, null);*/
                    //SobelVague.convertGreyImg(bitmap, list.get(i));
                }

            }
        }
    }

    public static void getFingerPrint(Bitmap image, PicInfo pic) {


        int count = 0;
        int[] pixels = new int[64];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int grey = computeGrayValue(image.getPixel(i, j));
                pixels[i * 8 + j] = grey;
                count += pixels[i * 8 + j];
            }
        }
        int avg = count / 64;
        pic.setmFingerPrint(getFingerPrint(pixels, avg));
        pic.setFingerState(true);
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
        int index = 0;
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

    /**
     * 计算灰度值
     *
     * @param pixel
     * @return
     */
    private static int computeGrayValue(int pixel) {
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = (pixel) & 255;
        return (int)(0.3 * red + 0.59 * green + 0.11 * blue);
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
