package com.clinic.myclinic.Utils;

/*
    Утилиты для работы с данными авторизации
 */

import android.content.Context;
import android.content.Intent;

import com.clinic.myclinic.Activities.LoginActivity;

public class AuthorizationUtils {

    private static final String PREFERENCES_AUTHORIZED_KEY = "isAuthorized";
    private static final String LOGIN_PREFERENCES = "LoginData";
    private static final String PREFERENCES_AUTHORIZATION_EMAIL = "user_email";
    private static final String PREFERENCES_AUTHORIZATION_PASSWORD = "user_password";

    //TODO: Получение идет с сервера php. Надо Думать
    public static boolean checkForUserExist() {
        return false;
    }

    //сохраняем данный во внутренний файл настроек приложения
    public static void setAuthorized(Context context) {
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREFERENCES_AUTHORIZED_KEY, true)
                //Можно использовать и commit(), но он является устаревшим т.к. в API9 появился
                //асинхронный метод apply().
                .apply();
    }

    //сохраняем введенный e-mail, т.к. в дальнейшем он нам понадобится для получения данных с сервера
    public static void setEmail(Context context, String email){
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(PREFERENCES_AUTHORIZATION_EMAIL, email)
                .apply();
    }

    //сохраняем введенный пароль, т.к. в дальнейшем он нам понадобится для получения данных с сервера
    public static void setPassword(Context context, String password){
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(PREFERENCES_AUTHORIZATION_PASSWORD, password)
                .apply();
    }

    public static String getEmail(Context context){
     return context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
             .getString(PREFERENCES_AUTHORIZATION_EMAIL, "unknown");
    }

    public static String getPassword(Context context){
        return context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .getString(PREFERENCES_AUTHORIZATION_PASSWORD, "unknown");
    }

    //Выход из аккаунта
    public static void logout(Context context) {
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREFERENCES_AUTHORIZED_KEY, false)
                .apply();
    }

    //Чекаем пользователя, авторизован ли он
    public static boolean isAuthorized(Context context) {
        return context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(PREFERENCES_AUTHORIZED_KEY, false);
    }


}