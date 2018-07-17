package com.clinic.myclinic.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/*
    Утилиты для работы с Хранилищем
 */

public class PersistantStorageUtils {

    public static final String STORAGE_NAME = "StorageName";

    private static SharedPreferences settings = null;
    private static SharedPreferences.Editor editor = null;
    private static Context context = null;

    //Получить контекст
    public static void init( Context cntxt ){
        context = cntxt;
    }

    //Инициализировать настройки
    private static void init(){
        settings = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    //Добавить параметр
    public static void addProperty( String name, String value ){
        if( settings == null ){
            init();
        }
        editor.putString( name, value );
        editor.commit();
    }

    //Получить параметр
    public static String getProperty( String name ){
        if( settings == null ){
            init();
        }
        return settings.getString( name, null );
    }
}