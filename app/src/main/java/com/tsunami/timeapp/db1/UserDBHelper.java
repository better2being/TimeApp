package com.tsunami.timeapp.db1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * @author shenxiaoming
 */
public class UserDBHelper extends SQLiteOpenHelper {

    public static final String CREATE_USER_INFO = "create table user_info ("
            + "id integer primary key autoincrement, "
            + "username text, "
            + "password text, "
            + "headUrl text, "
            + "nickname text, "
            + "sex text, "
            + "age text, "
            + "signature text, "
            + "region text)";

    public static final String CREATE_FRIEND_TABLE = "create table friend_table ("
            + "id integer primary key autoincrement, "
            + "friendname text, "
            + "headUrl text, "
            + "nickname text, "
            + "sex text, "
            + "age text, "
            + "signature text, "
            + "region text)";

    public static final String CREATE_DAY_TIME_TABLE = "create table schedule_table ("
            + "id integer primary key autoincrement, "
            + "workname text, "
            + "worktext text, "
            + "date text, "
            + "starttime integer, "
            + "endtime integer)";

    private static final String CREATE_CIRCLE_TABLE = "create table circle_table ("
            + "id integer primary key autoincrement, "
            + "author text, "
            + "headUrl text, "
            + "content text, "
//            + "photos text, "
            + "createtime text)";
//            + "favorts text, "
//            + "comments text)";


    private Context mContext;

    public UserDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_INFO);
        db.execSQL(CREATE_FRIEND_TABLE);
        db.execSQL(CREATE_DAY_TIME_TABLE);
        db.execSQL(CREATE_CIRCLE_TABLE);
//        Toast.makeText(mContext, "create success", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
