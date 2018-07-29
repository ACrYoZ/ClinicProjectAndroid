package com.clinic.myclinic.Utils;

/*
    Утилиты для работы с данными авторизации
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.clinic.myclinic.Activities.UserProfileActivity;
import com.clinic.myclinic.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuthorizationUtils {

    JSONParser jParser = new JSONParser();

    //адрес
    private static String url_check_for_user_exist = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/check_for_user_exist.php";
    //теги узлов JSON
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_LOGIN = "login";
    private static final String TAG_PASSWORD = "password";

    protected String login, password;

    protected boolean isExist = false;


    private static final String PREFERENCES_AUTHORIZED_KEY = "isAuthorized";
    private static final String PREFERENCES_LOGIN = "LoginData";
    private static final String PREFERENCES_AUTHORIZATION_EMAIL = "user_email";
    private static final String PREFERENCES_AUTHORIZATION_PASSWORD = "user_password";

    public boolean checkForUserExist(String login, String password) {
        this.login = login; this.password = password;
        ArrayList<HashMap<String, String>> userData = new ArrayList<HashMap<String, String>>();
        new CheckForUserExistTask().execute();

        return isExist;
    }

    //сохраняем данный во внутренний файл настроек приложения
    public static void setAuthorized(Context context) {
        context.getSharedPreferences(PREFERENCES_LOGIN, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREFERENCES_AUTHORIZED_KEY, true)
                //Можно использовать и commit(), но он является устаревшим т.к. в API9 появился
                //асинхронный метод apply().
                .apply();
    }

    //сохраняем введенный e-mail, т.к. в дальнейшем он нам понадобится для получения данных с сервера
    public static void setEmail(Context context, String email){
        context.getSharedPreferences(PREFERENCES_LOGIN, Context.MODE_PRIVATE)
                .edit()
                .putString(PREFERENCES_AUTHORIZATION_EMAIL, email)
                .apply();
    }

    //сохраняем введенный пароль, т.к. в дальнейшем он нам понадобится для получения данных с сервера
    public static void setPassword(Context context, String password){
        context.getSharedPreferences(PREFERENCES_LOGIN, Context.MODE_PRIVATE)
                .edit()
                .putString(PREFERENCES_AUTHORIZATION_PASSWORD, password)
                .apply();
    }

    public static String getEmail(Context context){
     return context.getSharedPreferences(PREFERENCES_LOGIN, Context.MODE_PRIVATE)
             .getString(PREFERENCES_AUTHORIZATION_EMAIL, "unknown");
    }

    public static String getPassword(Context context){
        return context.getSharedPreferences(PREFERENCES_LOGIN, Context.MODE_PRIVATE)
                .getString(PREFERENCES_AUTHORIZATION_PASSWORD, "unknown");
    }

    //Выход из аккаунта
    public static void logout(Context context) {
        context.getSharedPreferences(PREFERENCES_LOGIN, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREFERENCES_AUTHORIZED_KEY, false)
                .apply();
    }

    //Чекаем пользователя, авторизован ли он
    public static boolean isAuthorized(Context context) {
        return context.getSharedPreferences(PREFERENCES_LOGIN, Context.MODE_PRIVATE)
                .getBoolean(PREFERENCES_AUTHORIZED_KEY, false);
    }

    // Задача в другом потоке для загрузки данных о пользователе через HTTP Request
    class CheckForUserExistTask extends AsyncTask<String, String, String> {

        // получим данные о пользователе через url
        protected String doInBackground(String... args) {
            // Строим параметры
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_LOGIN, login));
            params.add(new BasicNameValuePair(TAG_PASSWORD, password));

            // отправим данные через запрос POST
            JSONObject json = jParser.makeHttpRequest(url_check_for_user_exist, "GET", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                // если пользователь существует - устанавливаем флаг в true, если нет - ничего не делаем
                if (success == 1) { isExist = true; }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
