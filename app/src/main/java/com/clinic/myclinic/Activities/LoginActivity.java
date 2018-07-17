package com.clinic.myclinic.Activities;

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
import android.widget.Toast;

import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends AppCompatActivity {

    EditText edtUserName, edtPassword;
    CheckBox chbRemember;

    @Override
    //Говорим о том, что состояние может быть null
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().hide();

        edtUserName = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
    }

    public void onClick(View v){
        if(isValidEmail(edtUserName.getText().toString())){
            //Проверяем, существует ли пользователь TODO: не реализовано, реализовать
            AuthorizationUtils.checkForUserExist();
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
