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
    private static final String PREF_KEY_TOKEN = "PREF_PERSONAL_TOKEN";
    private static final String PREF_KEY_TOKEN_SENDED = "PREF_PERSONAL_TOKEN_SENDED";
    private static final String PREF_KEY_MAP_MODE = "PREF_CHANGE_MAP_MODE";

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
    public static String getProperty( String name){
        if( settings == null ){
            init();
        }
        return settings.getString( name, null );
    }

    public static Boolean getMapPreferences(Context ctx){
        defaultPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return defaultPref.getBoolean(PREF_KEY_MAP_MODE, false);
    }

    public static String getLanguagePreferences(Context ctx){
        defaultPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return defaultPref.getString(PREF_KEY_LANGUAGE, PREF_DEF_LANGUAGE);
    }

    public static String getTextSizePreferences(Context ctx){
        defaultPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return defaultPref.getString(PREF_KEY_TEXT_SIZE, PREF_DEF_TEXT_SIZE);
    }

    public static void storeToken(String token, Context ctx){
        ctx.getSharedPreferences(PREF_KEY_TOKEN, Context.MODE_PRIVATE)
                .edit()
                .putString(PREF_KEY_TOKEN, token)
                .apply();
    }

    public static String getToken(Context ctx){
        return ctx.getSharedPreferences(PREF_KEY_TOKEN, Context.MODE_PRIVATE)
                .getString(PREF_KEY_TOKEN, null);
    }

    public static void storeTokenSended(boolean is_sended, Context ctx){
        ctx.getSharedPreferences(PREF_KEY_TOKEN_SENDED, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREF_KEY_TOKEN_SENDED, is_sended)
                .apply();
    }

    public static Boolean getTokenSended(Context ctx){
        return ctx.getSharedPreferences(PREF_KEY_TOKEN_SENDED, Context.MODE_PRIVATE)
                .getBoolean(PREF_KEY_TOKEN_SENDED, false);
    }
}