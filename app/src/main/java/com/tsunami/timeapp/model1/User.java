package com.tsunami.timeapp.model1;

import android.os.Environment;
import android.text.TextUtils;


import java.io.File;

/**
 * @author shenxiaoming
 */
public class User {

    private static User user;
    /**
     * 获取User的实例
     */
    public synchronized static User getInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    // 用户名和密码
    private String username;
    private String password;
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // 个人中心数据
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

    /**
     * 将构造方法私有化
     */
    private User() {
    }
//    public User(String username, String password) {
//        this.username = username;
//        this.password = password;
//    }
//    public User(String headUrl, String nickName, String sex, String age, String signature, String region) {
//        setHeadUrl(headUrl);
//        setNickName(nickName);
//        setSex(sex);
//        setAge(age);
//        setSignature(signature);
//        setRegion(region);
//    }
//    public User(String username, String password, String headUrl, String nickName, String sex, String age, String signature, String region) {
//        this.username = username;
//        this.password = password;
//        setHeadUrl(headUrl);
//        setNickName(nickName);
//        setSex(sex);
//        setAge(age);
//        setSignature(signature);
//        setRegion(region);
//    }
}
