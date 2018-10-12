package com.clinic.myclinic.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    //Указываем текущую версию БД
    public static final int DB_VERSION = 1;
    //Указываем имя базы
    public static final String DB_NAME = "cliniclv";
    //Указываем имя таблиц
    public static final String DB_DOCTORS_TABLE = "doctors";
    //Указываем названия полей в базе
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_POSITION = "position";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_RATING = "rating";
    public static final String KEY_PARLOR = "parlor";
    public static final String KEY_FROM = "from_txt";
    public static final String KEY_TO = "to_txt";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);


    }

    //Создание БД
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DB_DOCTORS_TABLE + "("     + KEY_ID        + " INTEGER primary key,"
                                                                + KEY_NAME      + " TEXT,"
                                                                + KEY_POSITION + " TEXT,"
                                                                + KEY_PHONE     + " TEXT,"
                                                                + KEY_PHOTO     + " TEXT,"
                                                                + KEY_RATING    + " REAL,"
                                                                + KEY_PARLOR    + " TEXT,"
                                                                + KEY_FROM      + " TEXT,"
                                                                + KEY_TO        + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
