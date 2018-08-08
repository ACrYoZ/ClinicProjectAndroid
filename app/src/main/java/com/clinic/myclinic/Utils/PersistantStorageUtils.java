package com.clinic.myclinic.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.clinic.myclinic.R;

/*
    Утилиты для работы с Хранилищем
 */

public class PersistantStorageUtils {

    public static final String STORAGE_NAME = "StorageName";
    private static final String PREF_KEY_LANGUAGE = "PREF_CHANGE_LANGUAGE";
    private static final String PREF_DEF_LANGUAGE = "en";
    private static final String PREF_DEF_TEXT_SIZE = "14";
    private static final String PREF_KEY_TEXT_SIZE = "PREF_CHANGE_TXT_SIZE";

    private static SharedPreferences settings = null;
    private static SharedPreferences.Editor editor = null;
    private static SharedPreferences defaultPref = null;
    private static Context context = null;

    //Получить контекст
    public static void init( Context cntxt ){
        context = cntxt;
    }

    //Инициализировать настройки
    private static void init(){
        settings = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        defaultPref = PreferenceManager.getDefaultSharedPreferences(context);
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

    public static String getLanguagePreferences(Context ctx){
        defaultPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return defaultPref.getString(PREF_KEY_LANGUAGE, PREF_DEF_LANGUAGE);
    }

    public static String getTextSizePreferences(Context ctx){
        defaultPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return defaultPref.getString(PREF_KEY_TEXT_SIZE, PREF_DEF_TEXT_SIZE);
    }
}