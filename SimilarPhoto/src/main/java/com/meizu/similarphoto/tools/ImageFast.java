package com.meizu.similarphoto.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.provider.MediaStore;

import com.meizu.similarphoto.infos.PicInfo;

import java.util.List;

/**
 * Created by root on 16-3-18.
 */
public class ImageFast {

    //计算所有的图片的fingerprint
    public static void calculateFingerPrint(List<PicInfo> list, Context context) {

        float scale_width = 0f, scale_height = 0f;
        for (int i = 0; i < list.size(); i++) {
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                    list.get(i).getmID(), MediaStore.Images.Thumbnails.MICRO_KIND, null);
            //设置缩放比例
            scale_width = 8.0f / bitmap.getWidth();
            scale_height = 8.0f / bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(scale_width, scale_height);

            Bitmap reduce_bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            long fingerprint = getFingerPrint(reduce_bitmap);

            list.get(i).setmFingerPrint(fingerprint);
        }
    }

    public static long getFingerPrint(Bitmap image) {

        double[][] pixels = getGrayValue(image);
        double avg = getAverage(pixels);
        long fingerprint = getFingerPrint(pixels, avg);
        return fingerprint;
    }

    /**
     * 得到灰度值
     *
     * @param image
     * @return
     */
    private static double[][] getGrayValue(Bitmap image) {
        //int width = 8;
        //int height = 8;

        double[][] pixels = new double[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pixels[i][j] = computeGrayValue(image.getPixel(i, j));

            }
        }


        return pixels;
    }

    /**
     * 得到平均值
     *
     * @param pixels 灰度值矩阵
     * @return
     */
    private static double getAverage(double[][] pixels) {
        //int width = 8;
        //int height = 8;
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                count += pixels[i][j];
            }
        }
        return count / 64;
    }
    /**
     * 获得信息指纹
     *
     * @param pixels 灰度值矩阵
     * @param avg    平均值
     * @return 返回01串的十进制表示
     */
    private static long getFingerPrint(double[][] pixels, double avg) {

        int width = pixels[0].length;
        int height = pixels.length;
        byte[] bytes = new byte[height * width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (pixels[i][j] >= avg) {
                    bytes[i * height + j] = 1;
                } else {
                    bytes[i * height + j] = 0;
                }
            }
        }
        //两个long 来存储 64位数据，fingerprint1存储低32位，fingerprint存储高32位
        long fingerprint1 = 0;
        long fingerprint2 = 0;
        for (int i = 0; i < 64; i++) {
            if (i < 32) {
                fingerprint1 += (bytes[63 - i] << i);
            } else {
                fingerprint2 += (bytes[63 - i] << (i - 31));
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
    private static double computeGrayValue(int pixel) {
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = (pixel) & 255;
        return 0.3 * red + 0.59 * green + 0.11 * blue;
    }

}

