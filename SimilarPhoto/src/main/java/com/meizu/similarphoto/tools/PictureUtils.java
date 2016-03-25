package com.meizu.similarphoto.tools;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.meizu.similarphoto.infos.PicInfo;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjiangchuan on 16-3-7.
 */
public class PictureUtils {

    private static DecimalFormat mFormater = new DecimalFormat("#.##");

    //查询图片的字段信息
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME, // 名称
            MediaStore.Images.Media.DATA, //路径
            MediaStore.Images.Media._ID, // id
            MediaStore.Images.Media.BUCKET_ID, // dir id 目录
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // dir name 目录名字
            MediaStore.Images.Media.SIZE, //大小
            MediaStore.Images.ImageColumns.MIME_TYPE, //类型
            MediaStore.Images.ImageColumns.DATE_ADDED,//添加时间
            MediaStore.Images.ImageColumns.DATE_MODIFIED, //最后修改时间
            MediaStore.Images.ImageColumns.DATE_TAKEN, //拍摄时间
            MediaStore.Images.ImageColumns.WIDTH,      //图片的宽
            MediaStore.Images.ImageColumns.HEIGHT       //图片的高
    };

    private static final String[] THUMBNAIL_IAMGES = {
            MediaStore.Images.Thumbnails.HEIGHT,
            MediaStore.Images.Thumbnails.WIDTH
    };

    /**
     * 获取相册所有图片列表
     * @param context
     * @return
     */
    public static List<PicInfo> getDCIMImageList(Context context){
        // 指定要查询的uri资源
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri thumburi = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        // 获取ContentResolver
        ContentResolver contentResolver = context.getContentResolver();
        // 按照拍摄时间排序
        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " asc";
        // 查询sd卡上的图片
        Cursor cursor = contentResolver.query(uri, STORE_IMAGES, null,
                null, sortOrder);
        List<PicInfo> imageList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                //获取所在文件夹的名字
                String bucket_name = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                if(bucket_name.equalsIgnoreCase("DCIM") || bucket_name.equalsIgnoreCase("camera")) {
                    PicInfo picInfo = prasePic(cursor);
                    /*Cursor cursor1 = contentResolver.query(thumburi,THUMBNAIL_IAMGES,
                            MediaStore.Images.Thumbnails.IMAGE_ID + "=? ",new String[] {picInfo.getmID()},null);
                    if(cursor1 != null && cursor1.moveToFirst()){
                        String width = cursor1.getString(cursor1.getColumnIndex(MediaStore.Images.Thumbnails.WIDTH));
                        String height = cursor1.getString(cursor1.getColumnIndex(MediaStore.Images.Thumbnails.HEIGHT));
                        picInfo.setmWidth(Integer.valueOf(width));
                        picInfo.setmHeight(Integer.valueOf(height));
                    }*/
                    imageList.add(picInfo);
                }

            } while (cursor.moveToNext());
            cursor.close();
        }
        return imageList;
    }

    /**
     *找出路径对应的缩略图ID
     * @param paths
     */
    public static List<PicInfo> pathToID(List<String> paths, Context context){
        // 指定要查询的uri资源
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // 获取ContentResolver
        ContentResolver contentResolver = context.getContentResolver();
        List<PicInfo> result = new ArrayList<>();

        for(int i = 0; i < paths.size(); i++){
            Cursor cursor = contentResolver.query(uri, STORE_IMAGES, MediaStore.Images.Media.DATA + "=? ", new String[]{paths.get(i)}, null);
            if(cursor.moveToFirst()){
                PicInfo picInfo = prasePic(cursor);
                result.add(picInfo);
            }else{
                //没有找到缩略图，就没有对应的ID
                //通过文件找到对应的创建日期
                File file = new File(paths.get(i));
                String taken_time = String.valueOf(file.lastModified());
                PicInfo picInfo = new PicInfo(paths.get(i),null,null,null,null,null,null,null,taken_time,0);
                result.add(picInfo);
            }
        }
        return result;
    }

    private static PicInfo prasePic(Cursor cursor) {
        String path = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DATA));

        String displayName = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        String noExtensionName = getFileNameWithoutExtension(displayName);
        Long size = cursor.getLong(cursor
                .getColumnIndex(MediaStore.Images.Media.SIZE));
        String fileSize = getSizeFormatText(size);
        String mimetype = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE));
        String creatTime = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED));
        String modifyTime = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED));
        String takenTime = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
        //获取bucket name
        String bucket_name = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

        String bucket_id = String.valueOf(cursor.getLong(cursor
                .getColumnIndex(MediaStore.Images.Media.BUCKET_ID)));
        String id = String.valueOf(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
        long ID = Long.parseLong(id);
        PicInfo picInfo = new PicInfo(path, noExtensionName, creatTime, fileSize, modifyTime, mimetype, bucket_name, bucket_id, takenTime, ID);
        return picInfo;
    }

    /**
     * 获取文件名称
     *
     * @return
     */
    public static String getFileName(String filepath) {
        String path = filepath;
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        if (path.endsWith("/")) {
            int index = path.lastIndexOf("/");
            if (index >= 0 || index < path.length()) {
                path = path.substring(0, index);
            }
        }
        int start = path.lastIndexOf("/");
        if (start == -1) {
            return path;
        }
        String fileName = path.substring(start + 1);
        return fileName;
    }

    /**
     * 获取文件名称 不包含extension
     *
     * @param fileName
     * @return
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null) {
            return "";

        }
        String name = getFileName(fileName);
        int dotPosition = name.lastIndexOf(".");
        if (dotPosition > 0) {
            return name.substring(0, dotPosition);
        } else {
            return name;
        }
    }

    /**
     * @param length 文件长度
     * @return 带有合适单位名称的文件大小
     */
    public static String getSizeFormatText(long length) {
        if (length <= 0)
            return "0KB";

        String str = "B";
        double result = (double) length;
        if (length < 1024) {
            return "1KB";
        }
        // 以1024为界，找到合适的文件大小单位
        if (result >= 1024) {
            str = "KB";
            result /= 1024;
            if (result >= 1024) {
                str = "MB";
                result /= 1024;
            }
            if (result >= 1024) {
                str = "GB";
                result /= 1024;
            }
        }
        String sizeString = null;
        // 按照需求设定文件的精度
        // MB 和 GB 保留两位小数
        if (str.equals("MB") || str.equals("GB")) {
            sizeString = mFormater.format(result);
        }
        // B 和 KB 保留到各位
        else
            sizeString = Integer.toString((int) result);
        return sizeString + str;
    }

}
