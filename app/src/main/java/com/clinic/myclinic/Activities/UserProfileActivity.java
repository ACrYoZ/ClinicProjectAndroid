package com.clinic.myclinic.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.clinic.myclinic.Classes.User;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class UserProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Toolbar mToolbar;

    //Объявляем компоненты интерфейса
    ImageView userPhoto, userPhotoNavigationDrawer;
    TextView userName, userAge, userAdress, userDiagnosis, userMedication, userEmail, userNameNavigationDrawer;


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

        mToolbar = findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout_user_profile);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_en, R.string.close_en);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Для профиля
        userPhoto = findViewById(R.id.imgUser);
        userName = findViewById(R.id.txtUserName);
        userAge = findViewById(R.id.txtAgeNum);
        userAdress = findViewById(R.id.txtAdressFull);
        userDiagnosis = findViewById(R.id.txtDiagnosisText);
        userMedication = findViewById(R.id.txtMedicationText);

        //Для Navigation Drawer TODO: пофиксить. Ошибка с тем, что элементы находятся в другом layout. Как до них достучаться?
        //userPhotoNavigationDrawer = findViewById(R.id.imgUserNavigationDrawer);
        //userEmail = findViewById(R.id.txtUserEmailNavigationDrawer);
        //userNameNavigationDrawer = findViewById(R.id.txtUserNameNavigationDrawer);

        //Устанавливаем картинку из интернета TODO: временное решение. Переделать чтобы грузилась с сервера
        Picasso.get()
                .load(user.getUserPhoto())
                .resize(100, 100)
                .centerCrop()
                .into(userPhoto);
       // Picasso.get()
       //         .load(user.getUserPhoto())
       //         .resize(100, 100)
       //         .centerCrop()
       //         .into(userPhotoNavigationDrawer);


        userName.setText(user.getUserName() + " " + user.getUserPatronymic() + " " + user.getUserSurname());
        userAge.setText(user.getUserAge());
        userAdress.setText(user.getUserAdress());
        userDiagnosis.setText(user.getUserDiagnosis());
        userMedication.setText(user.getUserMedication());
//      userNameNavigationDrawer.setText(user.getUserName() + " " + user.getUserPatronymic() + " " + user.getUserSurname());
//      userEmail.setText(user.getUserEmail());
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_my_account:
                break;
            case R.id.nav_my_schedules:
                startRecordsActivity();
            case R.id.nav_logout:
                onLogout();
            case R.id.nav_settings:
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_user_profile);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_user_profile);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Установка возможности клика по "гамбургеру". Т.е. без этого действия, клик по "гамбургеру ничего делать не булет"
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }//if
        return super.onOptionsItemSelected(item);
    }


}
