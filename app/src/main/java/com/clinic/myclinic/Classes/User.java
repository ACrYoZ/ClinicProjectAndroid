package com.clinic.myclinic.Classes;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.clinic.myclinic.Activities.UserProfileActivity;
import com.clinic.myclinic.Interfaces.onUserDataReceived;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements onUserDataReceived {
    JSONParser jsonParser = new JSONParser();

    Context context;

    //адрес
    private static String url_get_patient_data = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/get_patient_data.php";

    //теги узлов JSON
    private static final String TAG_UID = "pid";
    private static final String TAG_LOGIN = "login";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_NAME = "name";
    private static final String TAG_PATRONYMIC = "patronymic";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_AGE = "age";
    private static final String TAG_ADRESS = "adress";
    private static final String TAG_DIAGNOSIS = "diagnosis";
    private static final String TAG_COUNT_DISAGREE = "disagree";
    private static final String TAG_MEDICATION = "medication";
    private static final String TAG_USER = "user";

    //Поля данных о пользователе
    private int id;
    private String userEmail;
    private String userAge;
    private String userAdress;
    private String userPhoto;
    private String userName;
    private String userSurname;
    private String userPatronymic;
    private int countDisagree;
   @Nullable private String userDiagnosis;
   @Nullable private String userMedication;

    // создаем поле объекта-колбэка
    private static onUserDataReceived dataReceived;

    //Constructors
    public User(int id, String userEmail, String userPhoto, String userName,
                String userSurname, String userPatronymic, String userAge,String userAdress,
                String userDiagnosis, String userMedication) {
        this.id = id;
        this.userEmail = userEmail;
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.userSurname = userSurname;
        this.userPatronymic = userPatronymic;
        this.userDiagnosis = userDiagnosis;
        this.userMedication = userMedication;
        this.userAge = userAge;
        this.userAdress = userAdress;
    }

    //Конструктор получающий данные с сервера
    public User(Context ctx) {
        context = ctx;
        new GetUserDataTask().execute();
    }

    //Getters and Setters

    public int getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserPatronymic() {
        return userPatronymic;
    }

    public void setUserPatronymic(String userPatronymic) {
        this.userPatronymic = userPatronymic;
    }

    public String getUserDiagnosis() {
        return userDiagnosis;
    }

    public void setUserDiagnosis(String userDiagnosis) {
        this.userDiagnosis = userDiagnosis;
    }

    public String getUserMedication() {
        return userMedication;
    }

    public void setUserMedication(String userMedication) {
        this.userMedication = userMedication;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserAdress() {
        return userAdress;
    }

    public void setUserAdress(String userAdress) {
        this.userAdress = userAdress;
    }

    public int getCountDisagree() {
        return countDisagree;
    }

    // AsyncTask для получения информации о пользователе
    private class GetUserDataTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            // проверяем тег success
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_LOGIN, AuthorizationUtils.getEmail(context)));
                params.add(new BasicNameValuePair(TAG_PASSWORD, AuthorizationUtils.getPassword(context)));

                // получаем информацию через запрос HTTP GET
                JSONObject json = jsonParser.makeHttpRequest(url_get_patient_data, "GET", params);

                // ответ от json о пользователе
                if(json != null) {
                    Log.d("User Data Json", json.toString());

                    // json success tag
                    success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        // если получили информацию о пользователе
                        JSONArray userObj = json.getJSONArray(TAG_USER);

                        // получим первый объект из массива JSON Array и установим необходимые поля
                        JSONObject user = userObj.getJSONObject(0);
                        id = user.getInt(TAG_UID);
                        userEmail = AuthorizationUtils.getEmail(context);
                        userAge = user.getString(TAG_AGE);
                        userAdress = user.getString(TAG_ADRESS);
                        userPhoto = user.getString(TAG_CONTENT);
                        userName = user.getString(TAG_NAME);
                        userSurname = user.getString(TAG_SURNAME);
                        countDisagree = user.getInt(TAG_COUNT_DISAGREE);
                        userPatronymic = user.getString(TAG_PATRONYMIC);
                        userMedication = user.getString(TAG_MEDICATION);
                        userDiagnosis = user.getString(TAG_DIAGNOSIS);

                        dataReceived.onUserDataReceivedUpdateComponents();
                    } else {
                        // не нашли пользователя
                    }
                }//if != null
            } catch (JSONException e) { e.printStackTrace(); }
            return null;
        }
    }

    @Override
    public void onUserDataReceivedUpdateComponents() {

    }

    public void setOnDataReceived(onUserDataReceived dataReceived){
        this.dataReceived = dataReceived;
    }
}
