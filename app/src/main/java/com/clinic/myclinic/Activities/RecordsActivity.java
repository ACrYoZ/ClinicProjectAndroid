package com.clinic.myclinic.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.clinic.myclinic.Adapters.RecordsAdapter;
import com.clinic.myclinic.Classes.Record;
import com.clinic.myclinic.Classes.User;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import java.util.ArrayList;
import java.util.Collection;

import es.dmoral.toasty.Toasty;

public class RecordsActivity extends AppCompatActivity {

    //Объявляем пользователя
    User user = null;

    RecordsAdapter adapter;
    ArrayList<Record> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        records = new ArrayList<>();

        //Создание пользователя TODO: должен получать данные с сервера, не вручную заполнять. И вообще, получать из старой активности или Presistant Storage
        user = new User(
                "mrtvzat2013@yandex.com",
                "ch",
                "https://github.com/ACrYoZ/ClinicProjectAndroid/blob/e8fa2f0fbc2a8ed5840b4d3cae61892b1df232ec/app/src/main/res/drawable/profile_photo.jpg",
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

        adapter = new RecordsAdapter(this, R.id.lvRecords, records);

        ListView lvRecords = findViewById(R.id.lvRecords);
        lvRecords.setAdapter(adapter);
        Toasty.info(this, "Адаптер установлен", 100, true);
    }

    private void startUserProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

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
