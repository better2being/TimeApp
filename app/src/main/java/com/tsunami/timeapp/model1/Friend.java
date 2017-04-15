package com.tsunami.timeapp.model1;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * @author shenxiaoming
 */
public class Friend {

    private String friendName;
    public String getFriendName() {
        return friendName;
    }
    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    private String headUrl;
    private String nickName;
    private String sex;
    private String age;
    private String signature;
    private String region;
    public void setHeadUrl(String headUrl) {
        if (TextUtils.isEmpty(headUrl)) {
            headUrl = Environment.getExternalStorageDirectory().toString()
                    + File.separator
                    + "com.tsunami.timeapp"
                    + File.separator
                    + "default_useravatar.png";
        } else {
            this.headUrl = headUrl;
        }
    }
    public void setNickName(String nickName) {
        if (TextUtils.isEmpty(nickName)) {
            nickName = "";
        } else {
            this.nickName = nickName;
        }
    }
    public void setSex(String sex) {
        if (TextUtils.isEmpty(sex)) {
            sex = "男";
        } else {
            this.sex = sex;
        }
    }
    public void setAge(String age) {
        if (TextUtils.isEmpty(age)) {
            age = "20";
        } else {
            this.age = age;
        }
    }
    public void setSignature(String signature) {
        if (TextUtils.isEmpty(signature)) {
            signature = "";
        } else {
            this.signature = signature;
        }
    }
    public void setRegion(String region) {
        if (TextUtils.isEmpty(region)) {
            region = "江苏南京";
        } else {
            this.region = region;
        }
    }
    public String getRegion() {
        return region;
    }
    public String getHeadUrl() {
        return headUrl;
    }
    public String getNickName() {
        return nickName;
    }
    public String getSex() {
        return sex;
    }
    public String getAge() {
        return age;
    }
    public String getSignature() {
        return signature;
    }

    public Friend() {
    }
    public Friend(String friendName) {
        this.friendName = friendName;
    }
    public Friend(String headUrl, String nickName, String sex, String age, String signature, String region) {
        setHeadUrl(headUrl);
        setNickName(nickName);
        setSex(sex);
        setAge(age);
        setSignature(signature);
        setRegion(region);
    }
    public Friend(String friendName, String region, String signature, String age, String sex, String nickName, String headUrl) {
        this.friendName = friendName;
        setHeadUrl(headUrl);
        setNickName(nickName);
        setSex(sex);
        setAge(age);
        setSignature(signature);
        setRegion(region);
    }
}
