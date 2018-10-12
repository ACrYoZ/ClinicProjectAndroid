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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.clinic.myclinic.Adapters.DoctorsAdapter;
import com.clinic.myclinic.Classes.Doctor;
import com.clinic.myclinic.Classes.Doctors;
import com.clinic.myclinic.Classes.User;
import com.clinic.myclinic.Interfaces.onDoctorsDataReceived;
import com.clinic.myclinic.Interfaces.onUserDataReceived;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.CircularTransformation;
import com.clinic.myclinic.Utils.PersistantStorageUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class AboutDoctorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, onUserDataReceived, onDoctorsDataReceived {

    public final static String ID_DOCTORS_OBJ = "doctor_obj";
    public final static String ID_USER_OBJ = "user_obj";

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
    ListView lvDoctors;

    //Объявляем пользователя
    User user = null;

    DoctorsAdapter adapter;
    Doctors doctors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_doctor);

        //Получаем актуальный язык
        language = PersistantStorageUtils.getLanguagePreferences(this);

        if(isOnline()){

            //Создание пользователя
            user = new User(this);
            user.setOnDataReceived(this);
            user.onUserDataReceivedUpdateComponents();

            doctors = new Doctors(this);
            doctors.setOnDoctorsDataReceived(this);
            doctors.onDoctorsDataReceivedUpdateComponents();
            doctors.setOnCategoriesDataReceived(this::onDoctorsDataReceivedUpdateComponents);

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
            if(language.equals("ru")) {
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

            doctors = new Doctors(this, true);

            if (doctors != null){
                adapter = new DoctorsAdapter(AboutDoctorActivity.this, R.layout.list_doctors_adapter_layout, doctors.getDoctors());

                lvDoctors = findViewById(R.id.lvDoctors);

                lvDoctors.setAdapter(adapter);

                lvDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView txt_phone = view.findViewById(R.id.txtPhoneCard);
                        String phone = txt_phone.getText().toString();

                        startAboutDocActivity(doctors.getDoctorByPhone(phone));
                    }
                });
            }

        }
        //Устанавливаем toolbar
        mToolbar = findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);

        //инициализируем нашу шторку
        mDrawerLayout = findViewById(R.id.drawer_layout_doctors);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_en, R.string.close_en);

        navView = findViewById(R.id.nav_view_doc);

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
        myDoctors = navMenu.findItem(R.id.nav_doctors);
        myDiagnoses = navMenu.findItem(R.id.nav_my_diagnoses);
        navInfo = navMenu.findItem(R.id.nav_info);

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

    private void setEnglishLocale() {
        myacc.setTitle(R.string.my_profile_en);
        myDiagnoses.setTitle(R.string.diagnoses_en);
        mylogout.setTitle(R.string.logout_en);
        myschedule.setTitle(R.string.schedule_en);
        mysettings.setTitle(R.string.settings_en);
        myDoctors.setTitle(R.string.doctors_en);
        navInfo.setTitle(R.string.about_clinic_en);
    }

    private void setRussianLocale() {
        myacc.setTitle(R.string.my_profile_ru);
        mylogout.setTitle(R.string.logout_ru);
        myschedule.setTitle(R.string.schedule_ru);
        myDiagnoses.setTitle(R.string.diagnoses_ru);
        mysettings.setTitle(R.string.settings_ru);
        myDoctors.setTitle(R.string.doctors_ru);
        navInfo.setTitle(R.string.about_clinic_ru);
    }

    private void startDiagnosesActivity(){
        Intent intent = new Intent(this, DiagnosisListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_my_account:
                startUserProfileActivity();
                break;
            case R.id.nav_my_schedules:
                startActivitySchedules();
                break;
            case R.id.nav_logout:
                onLogout();
                break;
            case R.id.nav_doctors:
                break;
            case R.id.nav_my_diagnoses:
                startDiagnosesActivity();
                break;
            case R.id.nav_settings:
                startSettingsActivity();
                break;
            case R.id.nav_info:
                startAboutActicity();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_doctors);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startAboutActicity() {
        Intent intent = new Intent(this, AboutClinicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Установка возможности клика по "гамбургеру". Т.е. без этого действия, клик по "гамбургеру ничего делать не булет"
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }//if
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //Прикручиваем поисковую строку к action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onUserDataReceivedUpdateComponents() {
        //Почему я использую handler - описано в классе UserProfileActivity.java в строке 334
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Picasso.get()
                        .load(user.getUserPhoto())
                        .resize(200, 200)
                        .centerCrop()
                        .transform(new CircularTransformation())
                        .into(userPhotoNavigationDrawer);

                userNameNavigationDrawer.setText(user.getUserName() + " " + user.getUserPatronymic() + " " + user.getUserSurname());
                userEmail.setText(user.getUserEmail());
            }
        });
    }

    @Override
    public void onDoctorsDataReceivedUpdateComponents() {
        //Почему я использую handler - описано в классе UserProfileActivity.java в строке 334
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (doctors != null){
                 adapter = new DoctorsAdapter(AboutDoctorActivity.this, R.layout.list_doctors_adapter_layout, doctors.getDoctors());

                 lvDoctors = findViewById(R.id.lvDoctors);

                    lvDoctors.setAdapter(adapter);

                    lvDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TextView txt_phone = view.findViewById(R.id.txtPhoneCard);
                            String phone = txt_phone.getText().toString();

                            startAboutDocActivity(doctors.getDoctorByPhone(phone));
                        }
                    });
                }
            }
        });
    }

    private void startAboutDocActivity(Doctor doctor) {
        Intent intent = new Intent(this, ExtendedDoctorInfoActivity.class);
        intent.putExtra(ID_DOCTORS_OBJ, doctor);
        intent.putExtra(ID_USER_OBJ, user.getId());
        startActivity(intent);
    }

    private void startUserProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void startActivitySchedules() {
        Intent intent = new Intent(this, RecordsActivity.class);
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

}
