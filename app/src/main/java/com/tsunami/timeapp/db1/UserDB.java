package com.tsunami.timeapp.db1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;


import com.tsunami.timeapp.model1.Circle;
import com.tsunami.timeapp.model1.Friend;
import com.tsunami.timeapp.model1.Schedule;
import com.tsunami.timeapp.model1.User;

import java.util.List;
import java.util.ArrayList;

/**
 * @author shenxiaoming
 */
public class UserDB {

    public static final String TAG = "UserDB";

    // 数据库名
    public static final String DB_NAME = "user_db";

    // 数据库版本
    public static final int VERSION = 1;

    private static UserDB userDB;

    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private UserDB(Context context) {
        UserDBHelper userDBHelper = new UserDBHelper(context,
                DB_NAME, null, VERSION);
        db = userDBHelper.getWritableDatabase();
    }

    /**
     * 获取UserDB的实例
     */
    public synchronized static UserDB getInstance(Context context) {
        if (userDB == null) {
            userDB = new UserDB(context);
        }
        return userDB;
    }

    /**
     * 将Friend信息存储到数据库
     */
    public void saveFriend(Friend friend) {
        if (friend != null) {
            ContentValues values = new ContentValues();
            values.put("friendname", friend.getFriendName());
            db.insert("friend_table", null, values);
        }
    }

    /**
     * 从数据库读取Friend列表
     */
    public List<Friend> loadFriends() {
        List<Friend> list = new ArrayList<>();
        Cursor cursor = db
                .query("friend_table", null, null,null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Friend friend = new Friend();
                friend.setFriendName(cursor.getString(cursor.
                        getColumnIndex("friendname")));
                list.add(friend);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    /**
     * 某个特定Friend
     */
    public Friend detailFriend(String friendName) {
        Friend friend = new Friend();
        Cursor cursor = db
                .query("friend_table", null, null,null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex("friendname"))
                        .equals(friendName)) {
                    friend.setFriendName(friendName);
                    if (TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex("sex")))) {
                        friend.setSex("男");
                    } else {
                        friend.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                    }
                    if (TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex("age")))) {
                        friend.setAge("20");
                    } else {
                        friend.setAge(cursor.getString(cursor.getColumnIndex("age")));
                    }
                    if (TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex("region")))) {
                        friend.setRegion("江苏南京");
                    } else {
                        friend.setRegion(cursor.getString(cursor.getColumnIndex("region")));
                    }
                    if (TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex("signature")))) {
                        friend.setSignature("空闲多多，欢迎来约~");
                    } else {
                        friend.setSignature(cursor.getString(cursor.getColumnIndex("signature")));
                    }
                    break;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return friend;
    }

    /**
     * 删除好友
     * @param friendName
     */
    public void deleteFriend(String friendName) {
        db.execSQL("DELETE FROM friend_table WHERE friendname = " + "'" + friendName + "'");
        Log.e("db", "delete friend success");
    }

    /**
     * 将发布的动态存入数据库
     */
    public void saveCircle(Circle circle) {
        if (circle != null) {
            ContentValues values = new ContentValues();
            values.put("author", circle.getAuthor());
            values.put("headUrl", circle.getHeadUrl());
            values.put("content", circle.getContent());
            values.put("createTime", circle.getCreateTime());
            db.insert("circle_table", null, values);
//            db.execSQL("insert into circle_table (author, headUrl, content, createTime) values(?, ?, ?, ?)",
//                    new String[] {circle.getAuthor(),
//                            circle.getHeadUrl(),
//                            circle.getContent(),
//                            circle.getCreateTime()});
        }
    }

    /**
     * 从数据库加载Circle列表
     */
    public List<Circle> loadCircles() {
        List<Circle> list = new ArrayList<>();
        Cursor cursor = db
                .query("circle_table", null, null,null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Circle circle = new Circle();
                circle.setAuthor(cursor.getString(cursor.
                        getColumnIndex("author")));
                circle.setHeadUrl(cursor.getString(cursor.
                        getColumnIndex("headUrl")));
                circle.setContent(cursor.getString(cursor.
                        getColumnIndex("content")));
                circle.setCreateTime(cursor.getString(cursor.
                        getColumnIndex("createtime")));
                list.add(circle);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    /**
     * 删除自己的一条动态
     */
    public void deleteCircle(String createTime) {
        db.execSQL("DELETE FROM circle_table WHERE createtime = " + "'" + createTime + "'");
        Log.e("db", "delete circle success");
    }

    /**
     * 日程安排Schedule存储到数据库
     */
    public void saveSchedule(Schedule schedule) {
        if (schedule != null) {
            ContentValues values = new ContentValues();
            values.put("workname", schedule.getWorkname());
            values.put("worktext", schedule.getWorktext());
            values.put("date", schedule.getDate());
            values.put("starttime", schedule.getStarttime());
            values.put("endtime", schedule.getEndtime());
            db.insert("schedule_table", null, values);
        }
    }

    /**
     * 日程安排Schedule从数据库删除
     */
    public void deleteSchedule(String date, int start, int end) {
        db.execSQL("DELETE FROM schedule_table WHERE " +
                "date='" +date+ "' and starttime= '" +start+ "' and endtime= '" +end + "'");
        Log.e("db", "delete schedule success");
    }

    /**
     * 从数据库加载Schedule列表
     */
    public List<Schedule> getSchedules() {
        List<Schedule> list = new ArrayList<>();
        Cursor cursor = db
                .query("schedule_table", null, null,null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule();
                schedule.setWorkname(cursor.getString(cursor.
                        getColumnIndex("workname")));
                schedule.setWorktext(cursor.getString(cursor.
                        getColumnIndex("worktext")));
                schedule.setDate(cursor.getString(cursor.
                        getColumnIndex("date")));
                schedule.setStarttime(cursor.getInt(cursor.
                        getColumnIndex("starttime")));
                schedule.setEndtime(cursor.getInt(cursor.
                        getColumnIndex("endtime")));
                list.add(schedule);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public void saveUserName(String username) {
        ContentValues values = new ContentValues();
        User.getInstance().setUsername(username);
        Log.e("db", "save username");
        values.put("username", username);
        db.insert("user_info", null, values);
    }
    public void saveUserPassword(String password) {
        ContentValues values = new ContentValues();
        User.getInstance().setPassword(password);
        values.put("password", password);
        db.insert("user_info", null, values);
    }
    public void saveUserHeadUrl(String path) {
        ContentValues values = new ContentValues();
        User.getInstance().setHeadUrl(path);
        values.put("headUrl", path);
        db.insert("user_info", null, values);
    }
}
