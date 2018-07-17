package com.clinic.myclinic.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.clinic.myclinic.Classes.User;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class UserProfileActivity extends AppCompatActivity {

    //Объявляем компоненты интерфейса
    ImageView userPhoto;
    TextView userName, userAge, userAdress, userDiagnosis, userMedication;


    //Объявляем пользователя
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Проверка. Авторизован ли пользователь? Нет? Выходим на активность входа.
        if (!AuthorizationUtils.isAuthorized(this)) {
            onLogout();
            return;
        }//if

        //Создание пользователя TODO: должен получать данные с сервера, не вручную заполнять
        user = new User(
                //TODO: E-mail и Passowrd получать не из сервера, а из presistant storage. Или всё же сервер?
                "mrtvzat2013@yandex.com",
                "ch",
                "https://pp.userapi.com/c836731/v836731946/4d031/i8MtY2l3c5Q.jpg",
                "Vladislav",
                "Tarapata",
                "Valeriyevich",
                "18",
                "Gertsina 12-B",
                "Good Health",
                "Haven't Medication"
        );

        // Handle Toolbar
        //final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        userPhoto = findViewById(R.id.imgUser);
        userName = findViewById(R.id.txtUserName);
        userAge = findViewById(R.id.txtAgeNum);
        userAdress = findViewById(R.id.txtAdressFull);
        userDiagnosis = findViewById(R.id.txtDiagnosisText);
        userMedication = findViewById(R.id.txtMedicationText);


        //Устанавливаем картинку из интернета TODO: временное решение. Переделать чтобы грузилась с сервера
        Picasso.get()
                .load(user.getUserPhoto())
                .resize(100, 100)
                .centerCrop()
                .into(userPhoto);


        userName.setText(user.getUserName() + " " + user.getUserPatronymic() + " " + user.getUserSurname());
        userAge.setText(user.getUserAge());
        userAdress.setText(user.getUserAdress());
        userDiagnosis.setText(user.getUserDiagnosis());
        userMedication.setText(user.getUserMedication());
    }//onCreate

    private void startRecordsActivity(){
        Intent intent = new Intent(this, RecordsActivity.class);
        startActivity(intent);
        finish();
    }

    //Если пользователь не авторизован - завершаем главную активность
    private void onLogout() {
        Intent login = new Intent(this, LoginActivity.class);
        //Если всё же авторизован, но нам нужно сделать logout - вызываем соответсвующий метод
        AuthorizationUtils.logout(this);
        //Intent.FLAG_ACTIVITY_CLEAR_TOP -- полностью завершает активности открытые ранее.
        //В нашем случаее - UserProfileActivity
        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login);
        finish();
    }
}
