package com.clinic.myclinic.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends AppCompatActivity {

    EditText edtUserName, edtPassword;

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
    }

    public void onClick(View v){
        //Проверяем, существует ли пользователь и корректен ли e-mail
        if(isValidEmail(edtUserName.getText().toString()) && edtPassword.getText().toString() != null
           && userExist.checkForUserExist(edtUserName.getText().toString(), edtPassword.getText().toString())){
            Log.i("AuthorizationUtils","User exist");
            //Записываем login & password пользователя
            AuthorizationUtils.setEmail(this, edtUserName.getText().toString());
            AuthorizationUtils.setPassword(this, edtPassword.getText().toString());
            AuthorizationUtils.setAuthorized(this);
            Log.i("AuthorizationUtils","User authorized");
            onLoginComplited();
        }else{
            setError();
        }//else-if
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
}
