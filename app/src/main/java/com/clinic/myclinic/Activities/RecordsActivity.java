package com.clinic.myclinic.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clinic.myclinic.Adapters.RecordsAdapter;
import com.clinic.myclinic.Classes.Records;
import com.clinic.myclinic.Classes.User;
import com.clinic.myclinic.Interfaces.SettingsInterface;
import com.clinic.myclinic.Interfaces.onRecordsDataReceived;
import com.clinic.myclinic.Interfaces.onUserDataReceived;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.CircularTransformation;
import com.clinic.myclinic.Utils.PersistantStorageUtils;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

import com.clinic.myclinic.Interfaces.onCircleButtonClickListener;

public class RecordsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, onCircleButtonClickListener, SettingsInterface
        , onRecordsDataReceived, onUserDataReceived {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navView;
    private Menu navMenu;
    private MenuItem myacc, myschedule, mysettings, mylogout;

    private Toolbar mToolbar;

    boolean flag;   //Вспомогательная переменная-флаг для слушателя OnScrollListener

    public  String language;

    //Объявляем компоненты интерфейса
    ImageView userPhotoNavigationDrawer;
    TextView userEmail, userNameNavigationDrawer;
    ListView lvRecords;
    FloatingActionButton fab;

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

        navView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Получаем FAB
        fab = findViewById(R.id.fab_add);

        //Устанавливаем слушателя клика по нашей FAB
        fab.setOnClickListener(v -> {
            startAddNewRecordActivity();
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
            case R.id.nav_settings:
                startSettingsActivity();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_records);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCircleButtonClick(View view, final int position) {
        if(records.removeRecordAt(position, user.getId(), user.getCountDisagree(), language, getWindow().getDecorView().getRootView())) {
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
    }

    @Override
    public void setEnglishLocale() {
        myacc.setTitle(R.string.my_profile_en);
        mylogout.setTitle(R.string.logout_en);
        myschedule.setTitle(R.string.schedule_en);
        mysettings.setTitle(R.string.settings_en);
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

                    //Устанавливаем слушателей прокрутки, для того, чтобы FAB автоматически убералась
                    lvRecords.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            if (scrollState == SCROLL_STATE_IDLE) {
                                fab.animate().scaleX(1f).scaleY(1f).start();
                                flag = true;
                            }
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            if (flag) {
                                fab.animate().scaleX(0f).scaleY(0f).start();
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
