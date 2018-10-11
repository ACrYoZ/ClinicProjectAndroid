package com.clinic.myclinic.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clinic.myclinic.Adapters.DiagnosesAdapter;
import com.clinic.myclinic.Classes.Diagnosis;
import com.clinic.myclinic.Classes.DiagnosisList;
import com.clinic.myclinic.Classes.User;
import com.clinic.myclinic.Interfaces.SettingsInterface;
import com.clinic.myclinic.Interfaces.onDiagnosesDataReceived;
import com.clinic.myclinic.Interfaces.onUserDataReceived;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.CircularTransformation;
import com.clinic.myclinic.Utils.PersistantStorageUtils;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class DiagnosisListActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener,
                                                              SettingsInterface, onDiagnosesDataReceived, onUserDataReceived {


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navView;
    private Menu navMenu;
    private MenuItem myacc, myschedule, mysettings, mylogout, myDoctors, navInfo, myDiagnoses;

    private Toolbar mToolbar;

    public String language;

    //Объявляем компоненты интерфейса
    ImageView userPhotoNavigationDrawer;
    TextView userEmail, userNameNavigationDrawer;
    ListView lvDiagnoses;

    TextView txtDiagnoses;

    DiagnosisList diagnoses;

    User user;

    DiagnosesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_list);

        //Получаем актуальный язык
        language = PersistantStorageUtils.getLanguagePreferences(this);

        if (isOnline()) {
            user = new User(this);
            user.setOnDataReceived(this);
            user.onUserDataReceivedUpdateComponents();

            //Уведомляем пользователя о том, что мы получаем данные и ему необходимо подождать.
            switch (language) {
                case "ru":
                    Toasty.info(this, "Получение данных. Пожалуйста, ожидайте...", Toast.LENGTH_SHORT).show();
                    break;

                case "en":
                    Toasty.info(this, "Receiving data. Please, wait...", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            user = new User(0, null, null, "Unknown User", null,
                    null, null, null, null, null);
            if (language.equals("ru")) {
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), R.string.offline_mode_ru, Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.setAction("Ok", vl -> {
                    snackbar.dismiss();
                });
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), R.string.offline_mode_en, Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.setAction("Ok", vl -> {
                    snackbar.dismiss();
                });
                snackbar.show();
            }
        }

        //Устанавливаем toolbar
        mToolbar = findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);

        //инициализируем нашу шторку
        mDrawerLayout = findViewById(R.id.drawer_layout_diagnosis);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_en, R.string.close_en);

        navView = findViewById(R.id.nav_view_diagnoses);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        //получаем ссылки на элементы с header
        View hView = navView.getHeaderView(0);
        userPhotoNavigationDrawer = hView.findViewById(R.id.imgUserNavigationDrawer);
        userEmail = hView.findViewById(R.id.txtUserEmailNavigationDrawer);
        userNameNavigationDrawer = hView.findViewById(R.id.txtUserNameNavigationDrawer);
        navMenu = navView.getMenu();

        myacc = navMenu.findItem(R.id.nav_my_account);
        mylogout = navMenu.findItem(R.id.nav_logout);
        myschedule = navMenu.findItem(R.id.nav_my_schedules);
        mysettings = navMenu.findItem(R.id.nav_settings);
        myDiagnoses = navMenu.findItem(R.id.nav_my_diagnoses);
        myDoctors = navMenu.findItem(R.id.nav_doctors);
        navInfo = navMenu.findItem(R.id.nav_info);

        txtDiagnoses = findViewById(R.id.txtDiagnosisListText);

        navView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Устанавливаем актуальный язык
        switch (language) {
            case "ru":
                setRussianLocale();
                break;
            case "en":
                setEnglishLocale();
                break;
        }

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

    private void startSettingsActivity() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    private void startMySchedulesActivity() {
        Intent intent = new Intent(this, RecordsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Установка возможности клика по "гамбургеру". Т.е. без этого действия, клик по "гамбургеру ничего делать не булет"
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }//if
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_diagnosis);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_my_account:
                startUserProfileActivity();
                break;
            case R.id.nav_my_diagnoses:
                break;
            case R.id.nav_my_schedules:
                startMySchedulesActivity();
                break;
            case R.id.nav_logout:
                onLogout();
                break;
            case R.id.nav_doctors:
                startDoctorsInfoActivity();
                break;
            case R.id.nav_settings:
                startSettingsActivity();
                break;
            case R.id.nav_info:
                startAboutActicity();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_diagnosis);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startAboutActicity() {
        Intent intent = new Intent(this, AboutClinicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void startDoctorsInfoActivity() {
        Intent intent = new Intent(this, AboutDoctorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void setRussianLocale() {
        myacc.setTitle(R.string.my_profile_ru);
        mylogout.setTitle(R.string.logout_ru);
        myschedule.setTitle(R.string.schedule_ru);
        myDiagnoses.setTitle(R.string.diagnoses_ru);
        txtDiagnoses.setText(R.string.diagnoses_ru);
        mysettings.setTitle(R.string.settings_ru);
        myDoctors.setTitle(R.string.doctors_ru);
        navInfo.setTitle(R.string.about_clinic_ru);
    }

    @Override
    public void setEnglishLocale() {
        myacc.setTitle(R.string.my_profile_en);
        mylogout.setTitle(R.string.logout_en);
        myschedule.setTitle(R.string.schedule_en);
        myDiagnoses.setTitle(R.string.diagnoses_en);
        mysettings.setTitle(R.string.settings_en);
        txtDiagnoses.setText(R.string.diagnoses_en);
        myDoctors.setTitle(R.string.doctors_en);
        navInfo.setTitle(R.string.about_clinic_en);
    }

    @Override
    public void setTextSize() {

    }

    @Override
    public void onDiagnosesDataReceivedUpdateComponents() {
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (diagnoses != null) {
                    adapter = new DiagnosesAdapter(DiagnosisListActivity.this, R.layout.list_diagnoses_adapter, diagnoses.getDiagnoses());

                    lvDiagnoses = findViewById(R.id.lvDiagnosis);
                    lvDiagnoses.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onUserDataReceivedUpdateComponents() {

        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                diagnoses = new DiagnosisList(user.getId());
                diagnoses.setOnDiagnosesDataReceivedComponents(DiagnosisListActivity.this);
                diagnoses.onDiagnosesDataReceivedUpdateComponents();

                userEmail.setText(user.getUserEmail());
                userNameNavigationDrawer.setText(user.getUserName());

                Picasso.get()
                        .load(user.getUserPhoto())
                        .resize(200, 200)
                        .centerCrop()
                        .transform(new CircularTransformation())
                        .into(userPhotoNavigationDrawer);
            }
        });
    }
}
