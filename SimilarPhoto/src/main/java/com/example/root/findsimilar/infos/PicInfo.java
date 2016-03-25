package com.example.root.findsimilar.infos;

/**
 * Created by root on 16-3-7.
 */
public class PicInfo extends BasicInfo{
    //下面是记录照片信息
    private String mPath;               //路径
    private String mNoExtensionName;     //后缀名
    private String mFileSize;            //文件大小
    private String mCreatTime;           //创建时间
    private String modifyTime;          //最近一次编辑时间
    private String mMimeType;            //
    private String mBucketName;          //所在上级目录
    private String mBucketId;            //所在上级目录的ID
    private String mTakenTime;
    private long mID;                 //资源表中的ID信息
    private long mFingerPrint;          //描述照片信息的指纹
    private boolean ALREADY_CACULATE;
    private int mClearNum;           //衡量照片清晰度的
    private int[] mSobelPicture;
    private int mSobelWidth;
    private int mSobelHeight;
    private int total_num;
    private int mHeight;
    private int mWidth;

    //构造函数
    public PicInfo(String path, String noExtensionName, String creatTime, String filesize, String modifyTime, String mimetype, String bucketName, String bucketId, String takenTime, long id) {
        this.mPath = path;
        this.mNoExtensionName = noExtensionName;
        this.mCreatTime = creatTime;
        this.mFileSize = filesize;
        this.modifyTime = modifyTime;
        this.mMimeType = mimetype;
        this.mBucketName = bucketName;
        this.mBucketId = bucketId;
        this.mTakenTime = takenTime;
        this.mID = id;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public String getNoExtensionName() {
        return mNoExtensionName;
    }

    public void setNoExtensionName(String noExtensionName) {    this.mNoExtensionName = noExtensionName; }

    public String getmFileSize() {   return mFileSize;    }

    public void setmFileSize(String mFileSize) {  this.mFileSize = mFileSize;   }

    public String getCreatTime() {
        return mCreatTime;
    }

    public void setCreatTime(String creatTime) {
        this.mCreatTime = creatTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getMimetype() {
        return mMimeType;
    }

    public void setMimetype(String mimetype) {
        this.mMimeType = mimetype;
    }

    public String getBucketName() {
        return mBucketName;
    }

    public void setBucketName(String bucketName) {
        this.mBucketName = bucketName;
    }

    public String getBucketId() {
        return mBucketId;
    }

    public void setBucketId(String bucketId) {
        this.mBucketId = bucketId;
    }

    public String getTakenTime() {
        return mTakenTime;
    }

    public void setTakenTime(String takenTime) {    this.mTakenTime = takenTime; }

    public long getmID() {  return mID; }

    public void setmID(long mID) {
        this.mID = mID;
    }

    public void setmFingerPrint(long mFingerPrint) {
        this.mFingerPrint = mFingerPrint;
    }

    public long getmFingerPrint(){
        return this.mFingerPrint;
    }

    public void setmClearNum(int mClearNum) {
        this.mClearNum = mClearNum;
    }

    public int getmClearNum() {
        return mClearNum;
    }

    public void setmSobelPicture(int[] mSobelPicture) {
        this.mSobelPicture = mSobelPicture;
    }

    public int[] getmSobelPicture() {
        return mSobelPicture;
    }

    public void setmSobelHeight(int mSobelHeight) {
        this.mSobelHeight = mSobelHeight;
    }

    public int getmSobelHeight() {
        return mSobelHeight;
    }

    public void setmSobelWidth(int mSobelWidth) {
        this.mSobelWidth = mSobelWidth;
    }

    public int getmSobelWidth() {
        return mSobelWidth;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }

    public int getTotal_num() {
        return total_num;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public void setFingerState(boolean state) {
        this.ALREADY_CACULATE = state;
    }

    public boolean getFingerState() {
        return this.ALREADY_CACULATE ;
    }
}
