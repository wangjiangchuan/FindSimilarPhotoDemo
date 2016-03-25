package com.meizu.similarphoto.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import com.meizu.similarphoto.infos.PicInfo;

import java.util.List;

/**
 * Created by root on 16-3-18.
 */




public class SobelVague {

    private static int[] sobelY = {-1, 0, 1,
            -2, 0, 2,
            -1, 0, 1};
    private static int[] sobelX = {-1, -2, -1,
            0, 0, 0,
            1, 2, 1};


    public static void sobelProcess(Context context,List<PicInfo> allPics) {

        for(int i =0; i < allPics.size(); i++) {
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                    allPics.get(i).getmID(), MediaStore.Images.Thumbnails.MINI_KIND, null);

            //allPics.get(i).setOriginBitmap(bitmap);
            convertGreyImg(bitmap, allPics.get(i));
            //allPics.get(i).setmPixels(sobelResult.sobelPixels);

        }
    }

    public static void convertGreyImg(Bitmap img, PicInfo pic) {

        float max_light = 0;
        float min_light = 0;
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高

        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组
        img.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                pixels[width * i + j] = grey;

                if(min_light == 0 && max_light == 0) {
                    min_light = grey;
                    max_light = grey;
                }
                if(min_light > grey)
                    min_light = grey;
                if(max_light < grey)
                    max_light = grey;
            }
        }
        sobelCaculate(pixels, (int)(max_light + min_light)/2, width, height, pic);
    }

    public static void sobelCaculate(int[] pixels, int VALUE, int width, int height, PicInfo pic) {
        int count = 0;
        int[] result = new int[width * height];
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                int a1 = pixels[width * (i - 1) + j - 1];
                int a2 = pixels[width * (i - 1) + j];
                int a3 = pixels[width * (i - 1) + j + 1];

                int b1 = pixels[width * i + j - 1];
                int b2 = pixels[width * i + j];
                int b3 = pixels[width * i + j + 1];

                int c1 = pixels[width * (i + 1) + j - 1];
                int c2 = pixels[width * (i + 1) + j];
                int c3 = pixels[width * (i + 1) + j + 1];

                int sobelX = getSobelX(a1, a2, a3, b1, b2, b3, c1, c2, c3);
                int sobelY = getSobelY(a1, a2, a3, b1, b2, b3, c1, c2, c3);
                int sobel = Math.abs(sobelX) + Math.abs(sobelY);
                if(sobel > VALUE){
                    result[width * i + j] = -1;
                    count++;
                }else {
                    result[width * i + j] = 0;
                }
            }
        }
        pic.setmClearNum(count);
        pic.setTotal_num(width * height);
        //pic.setmSobelPicture(result);
        //pic.setmSobelHeight(height);
        //pic.setmSobelWidth(width);
    }

    public static int getSobelX(int a1, int a2, int a3, int b1, int b2, int b3, int c1, int c2, int c3){
        return  a1*sobelX[0] + a2*sobelX[1] + a3*sobelX[2] +
                b1*sobelX[3] + b2*sobelX[4] + b3*sobelX[5] +
                c1*sobelX[6] + c2*sobelX[7] + c3*sobelX[8];
    }

    public static int getSobelY(int a1, int a2, int a3, int b1, int b2, int b3, int c1, int c2, int c3){
        return  a1*sobelY[0] + a2*sobelY[1] + a3*sobelY[2] +
                b1*sobelY[3] + b2*sobelY[4] + b3*sobelY[5] +
                c1*sobelY[6] + c2*sobelY[7] + c3*sobelY[8];
    }

}
