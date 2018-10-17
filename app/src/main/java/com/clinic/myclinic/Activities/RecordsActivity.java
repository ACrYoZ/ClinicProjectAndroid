package com.clinic.myclinic.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clinic.myclinic.Adapters.RecordsAdapter;
import com.clinic.myclinic.Classes.Record;
import com.clinic.myclinic.Classes.Records;
import com.clinic.myclinic.Classes.User;
import com.clinic.myclinic.Interfaces.SettingsInterface;
import com.clinic.myclinic.Interfaces.onRecordsDataReceived;
import com.clinic.myclinic.Interfaces.onUserDataReceived;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.CircularTransformation;
import com.clinic.myclinic.Utils.PersistantStorageUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

import com.clinic.myclinic.Interfaces.onCircleButtonClickListener;

import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, onCircleButtonClickListener, SettingsInterface
        , onRecordsDataReceived, onUserDataReceived {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navView;
    private Menu navMenu;
    private MenuItem myacc, myschedule, mysettings, mylogout, myDoctors, navInfo, myDiagnoses;

    private Toolbar mToolbar;

    boolean flag;   //Вспомогательная переменная-флаг для слушателя OnScrollListener

    public String language;

    //Объявляем компоненты интерфейса
    ImageView userPhotoNavigationDrawer;
    TextView userEmail, userNameNavigationDrawer, recordsText;
    ListView lvRecords;

    FloatingActionButton fab_add, fab_add_by_pref;
    FloatingActionMenu fab_menu;

    //Объявляем пользователя
    User user = null;

    RecordsAdapter adapter;
    Records records;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        //Получаем актуальный язык
        language = PersistantStorageUtils.getLanguagePreferences(this);

        if(isOnline()) {

            //Создание пользователя
            user = new User(this);
            user.setOnDataReceived(this);
            user.onUserDataReceivedUpdateComponents();

            //Создание списка записей пользователя
            records = new Records(this);

            records.setOnRecordsDataReceived(this);
            records.onRecordsDataReceivedUpdateComponents();

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
        }

        //Устанавливаем toolbar
        mToolbar = findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);

        //инициализируем нашу шторку
        mDrawerLayout = findViewById(R.id.drawer_layout_records);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_en, R.string.close_en);

        navView = findViewById(R.id.nav_view_records);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        recordsText = findViewById(R.id.txtRecordsText);

        //получаем ссылки на элементы с header
        View hView = navView.getHeaderView(0);
        userPhotoNavigationDrawer = hView.findViewById(R.id.imgUserNavigationDrawer);
        userEmail = hView.findViewById(R.id.txtUserEmailNavigationDrawer);
        userNameNavigationDrawer = hView.findViewById(R.id.txtUserNameNavigationDrawer);
        navMenu = navView.getMenu();

        myacc = navMenu.findItem(R.id.nav_my_account);
        mylogout = navMenu.findItem(R.id.nav_logout);
        myschedule = navMenu.findItem(R.id.nav_my_schedules);
        myDiagnoses = navMenu.findItem(R.id.nav_my_diagnoses);
        mysettings = navMenu.findItem(R.id.nav_settings);
        myDoctors = navMenu.findItem(R.id.nav_doctors);
        navInfo = navMenu.findItem(R.id.nav_info);

        navView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Получаем FABs и menu
        fab_add = findViewById(R.id.fab_add_new_rec_item);
        fab_add_by_pref = findViewById(R.id.fab_add_new_rec_by_prefers_item);

        fab_menu = findViewById(R.id.fab_add_menu);

        fab_add.setOnClickListener(v -> {
            startAddNewRecordActivity();
        });

        fab_add_by_pref.setOnClickListener(v -> {
            startAddNewRecordActivityByPrefs();
        });


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

    private void startAddNewRecordActivityByPrefs() {
        Intent intent = new Intent(this, AddPrefRecord.class);
        startActivity(intent);
    }

    private void startAddNewRecordActivity() {
        Intent intent = new Intent(this, AddANewRecordActivity.class);
        startActivity(intent);
    }

    private void startUserProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void startDiagnosesActivity(){
        Intent intent = new Intent(this, DiagnosisListActivity.class);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Установка возможности клика по "гамбургеру". Т.е. без этого действия, клик по "гамбургеру ничего делать не булет"
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }//if
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_records);
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

        switch (id){
            case R.id.nav_my_account:
                startUserProfileActivity();
                break;
            case R.id.nav_my_schedules:
                break;
            case R.id.nav_logout:
                onLogout();
                break;
            case R.id.nav_my_diagnoses:
                startDiagnosesActivity();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_records);
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

    @Override
    public void onCircleButtonClick(View view, final int position) {
        if(records.removeRecordAt(position, user.getId(), user.getCountDisagree(), language, getWindow().getDecorView().getRootView())) {

            //Создаем анимацию удаления
            Animation anim = AnimationUtils.loadAnimation(
                    RecordsActivity.this, android.R.anim.fade_out
            );
            anim.setDuration(500);

            lvRecords.getChildAt(position).startAnimation(anim );

            adapter = new RecordsAdapter(this, R.layout.list_records_adapter_layout, records.getRecords());
            adapter.notifyDataSetChanged();
            lvRecords.setAdapter(adapter);
        }
    }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    @Override
    public void setRussianLocale() {
        myacc.setTitle(R.string.my_profile_ru);
        mylogout.setTitle(R.string.logout_ru);
        myschedule.setTitle(R.string.schedule_ru);
        mysettings.setTitle(R.string.settings_ru);
        myDoctors.setTitle(R.string.doctors_ru);
        recordsText.setText(R.string.records_ru);
        myDiagnoses.setTitle(R.string.diagnoses_ru);
        navInfo.setTitle(R.string.about_clinic_ru);

        fab_add.setLabelText(getString(R.string.fab_add_new_record_ru));
        fab_add_by_pref.setLabelText(getString(R.string.fab_add_record_by_personal_prefers_ru));
    }

    @Override
    public void setEnglishLocale() {
        myacc.setTitle(R.string.my_profile_en);
        mylogout.setTitle(R.string.logout_en);
        myschedule.setTitle(R.string.schedule_en);
        mysettings.setTitle(R.string.settings_en);
        myDiagnoses.setTitle(R.string.diagnoses_en);
        recordsText.setText(R.string.records_en);
        myDoctors.setTitle(R.string.doctors_en);
        navInfo.setTitle(R.string.about_clinic_en);

        fab_add.setLabelText(getString(R.string.fab_add_new_record_en));
        fab_add_by_pref.setLabelText(getString(R.string.fab_add_record_by_personal_prefers_en));
    }

    @Override
    public void setTextSize() {}

    @Override
    public void onRecordsDataReceivedUpdateComponents() {
        //Почему я использую handler - описано в классе UserProfileActivity.java в строке 334
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if(records != null) {
                    adapter = new RecordsAdapter(RecordsActivity.this, R.layout.list_records_adapter_layout, records.getRecords());

                    lvRecords = findViewById(R.id.lvRecords);

                    // подписываем нашу активити на события колбэка
                    adapter.setOnCircleButtonClickListener(RecordsActivity.this);
                    lvRecords.setAdapter(adapter);

                    //Устанавливаем слушателей прокрутки, для того, чтобы FAB автоматически убиралась
                    lvRecords.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            if (scrollState == SCROLL_STATE_IDLE) {
                                fab_menu.showMenu(true);
                                flag = true;
                            }
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            if (flag) {
                                fab_menu.hideMenu(true);
                                flag = false;
                            }
                        }
                    });
                }
            }
        });
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
}
