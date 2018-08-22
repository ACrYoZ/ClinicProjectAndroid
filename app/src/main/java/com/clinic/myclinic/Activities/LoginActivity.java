package com.clinic.myclinic.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.clinic.myclinic.Interfaces.SettingsInterface;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.JSONParser;
import com.clinic.myclinic.Utils.PersistantStorageUtils;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends AppCompatActivity
                           implements SettingsInterface {

    EditText edtUserName, edtPassword;
    TextView txtLogin;

    public static String language;
    public static String textSize;

    AuthorizationUtils userExist;

    @Override
    //Говорим о том, что состояние может быть null
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().hide();
        userExist = new AuthorizationUtils();

        edtUserName = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        txtLogin = findViewById(R.id.txtLogin);

        //Получаем актуальный язык
        language = PersistantStorageUtils.getLanguagePreferences(this);

        //Устанавливаем актуальный язык
        switch (language){
            case "ru":
                setRussianLocale();
                break;
            case "en":
                setEnglishLocale();
                break;
        }

        //Получаем актуальный размер шрифта
        textSize = PersistantStorageUtils.getTextSizePreferences(this);
        setTextSize();
    }

    public void onClick(View v){
        //Если подключение к интернету есть - тогда мы обрабатываем событие нажатия и входим в аккаунт
        if(isOnline()) {

            //Проверяем, существует ли пользователь и корректен ли e-mail
            int isExist = -1;



            while (true){
                isExist = userExist.checkForUserExist(edtUserName.getText().toString(), edtPassword.getText().toString(), this);
                if(isExist == 1 || isExist == 0) { break;}
            }

            if (isValidEmail(edtUserName.getText().toString()) && edtPassword.getText().toString() != null
                    && isExist == 1) {
                Log.i("AuthorizationUtils", "User exist");
                //Записываем login & password пользователя
                AuthorizationUtils.setEmail(this, edtUserName.getText().toString());
                AuthorizationUtils.setPassword(this, edtPassword.getText().toString());
                AuthorizationUtils.setAuthorized(this);
                Log.i("AuthorizationUtils", "User authorized");
                onLoginComplited();
            } else {
                setError();
            }//else-if
        } else {
            if(language.equals("ru")) {
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), R.string.offline_mode_en, Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.setAction("Ok", vl -> {
                    snackbar.dismiss();
                });
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), R.string.offline_mode_ru, Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.setAction("Ok", vl -> {
                    snackbar.dismiss();
                });
                snackbar.show();
            }
        }
    }

    private void setError() {
        Toasty.error(this, "Ошибка ввода. Проверьте корректность!", Toast.LENGTH_SHORT).show();
    }

    private void onLoginComplited() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //проверяет E-mail
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void setRussianLocale() {
        txtLogin.setText(R.string.login_ru);
    }

    @Override
    public void setEnglishLocale() {
        txtLogin.setText(R.string.login_en);
    }

    @Override
    public void setTextSize() {
        edtUserName.setTextSize(Integer.parseInt(textSize));
        edtPassword.setTextSize(Integer.parseInt(textSize));
        txtLogin.setTextSize(Integer.parseInt(textSize));
    }
}
