package com.clinic.myclinic.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clinic.myclinic.Classes.User;
import com.clinic.myclinic.Interfaces.SettingsInterface;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.CircularTransformation;
import com.clinic.myclinic.Utils.JSONParser;
import com.clinic.myclinic.Utils.PersistantStorageUtils;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SettingsInterface {

    public final static String SERVER = "myclinic.ddns.net:8080";

    private static String url_send_fcm_token = "http://" + SERVER + "/AndroidScripts/send_fcm_token.php";

    private static final String TAG_TOKEN = "token";
    private static final String TAG_PID = "pid";

    JSONParser jsonParser = new JSONParser();

    public  String language;
    public  String textSize;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navView;
    private Menu navMenu;
    private MenuItem myacc, myschedule, mysettings, mylogout;

    private Toolbar mToolbar;

    //Объявляем компоненты интерфейса
    ImageView userPhoto, userPhotoNavigationDrawer;
    TextView userName, userAge, userAdress, userDiagnosis, userMedication, userEmail, userNameNavigationDrawer,
             txtAge, txtAdress, txtDiagnosis, txtMedication;


    //Объявляем пользователя
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Получаем актуальный язык
        language = PersistantStorageUtils.getLanguagePreferences(this);


        //Проверка. Авторизован ли пользователь? Нет? Выходим на активность входа.
        if (!AuthorizationUtils.isAuthorized(this)) {
            onLogout();
            return;
        }//if

        //Если сеть есть - берем данные с сервера. Если сети нет - создаем "null" пользователя
        if(isOnline()) {
            //Создание пользователя
            user = new User(this);
            //Нереально дикий костыль времен динозавров.TODO: исправить
            //Используется для того, чтобы пользователь наверняка создался, а уже затем пошла инициализация элементов
            //Если убрать sleep, то все поля будут - NULL, т.к. на получение данных нужно время, а активность не ждет и
            //инициализирует ещё не существующие элементы класса User
            try { TimeUnit.SECONDS.sleep(1); }
            catch (Exception e){ e.printStackTrace(); }

            //Если токен не был отправлен на сервер - отправляем
            if(!PersistantStorageUtils.getTokenSended(this)){
                PersistantStorageUtils.storeTokenSended(true, this);
                new sendToken().execute(); }
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
        mDrawerLayout = findViewById(R.id.drawer_layout_user_profile);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_en, R.string.close_en);

        navView = findViewById(R.id.nav_view_user_profile);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        //получаем ссылки на элементы с header
        View hView = navView.getHeaderView(0);
        userPhotoNavigationDrawer = hView.findViewById(R.id.imgUserNavigationDrawer);
        userEmail = hView.findViewById(R.id.txtUserEmailNavigationDrawer);
        userNameNavigationDrawer = hView.findViewById(R.id.txtUserNameNavigationDrawer);
        navMenu = navView.getMenu();

        navView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Для профиля
        userPhoto = findViewById(R.id.imgUser);
        userName = findViewById(R.id.txtUserName);
        userAge = findViewById(R.id.txtAgeNum);
        userAdress = findViewById(R.id.txtAdressFull);
        userDiagnosis = findViewById(R.id.txtDiagnosisText);
        userMedication = findViewById(R.id.txtMedicationText);
        txtAge = findViewById(R.id.txtAge);
        txtAdress = findViewById(R.id.txtAdress);
        txtDiagnosis = findViewById(R.id.txtDiagnosis);
        txtMedication = findViewById(R.id.txtMedication);
        myacc = navMenu.findItem(R.id.nav_my_account);
        mylogout = navMenu.findItem(R.id.nav_logout);
        myschedule = navMenu.findItem(R.id.nav_my_schedules);
        mysettings = navMenu.findItem(R.id.nav_settings);

        Picasso.get()
                .load(user.getUserPhoto())
                .resize(100, 100)
                .centerCrop()
                .transform(new CircularTransformation())
                .into(userPhoto);
         Picasso.get()
                 .load(user.getUserPhoto())
                 .resize(100, 100)
                 .centerCrop()
                 .transform(new CircularTransformation())
                 .into(userPhotoNavigationDrawer);

        userName.setText(user.getUserName() + " " + user.getUserPatronymic() + " " + user.getUserSurname());
        userAge.setText(user.getUserAge());
        userAdress.setText(user.getUserAdress());
        userDiagnosis.setText(user.getUserDiagnosis());
        userMedication.setText(user.getUserMedication());
        userNameNavigationDrawer.setText(user.getUserName() + " " + user.getUserPatronymic() + " " + user.getUserSurname());
        userEmail.setText(user.getUserEmail());

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

    }//onCreate

    protected void onResume(){
        language = PersistantStorageUtils.getLanguagePreferences(this);
        //Устанавливаем актуальный язык
        switch (language){
            case "en":
                setEnglishLocale();
                break;
            case "ru":
                setRussianLocale();
                break;
        }

        //Устанавливаем актуальный размер шрифта
        textSize = PersistantStorageUtils.getTextSizePreferences(this);
        setTextSize();

        super.onResume();
    }

    private void startRecordsActivity(){
        Intent intent = new Intent(this, RecordsActivity.class);
        startActivity(intent);
        finish();
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
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
                break;
            case R.id.nav_logout:
                onLogout();
                break;
            case R.id.nav_settings:
                startSettingsActivity();
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

    @Override
    public void setRussianLocale() {
        txtAge.setText(R.string.age_ru);
        txtAdress.setText(R.string.adress_ru);
        txtDiagnosis.setText(R.string.diagnosis_ru);
        txtMedication.setText(R.string.medication_ru);
        myacc.setTitle(R.string.my_profile_ru);
        mylogout.setTitle(R.string.logout_ru);
        myschedule.setTitle(R.string.schedule_ru);
        mysettings.setTitle(R.string.settings_ru);
    }

    @Override
    public void setEnglishLocale() {
        txtAge.setText(R.string.age_en);
        txtAdress.setText(R.string.adress_en);
        txtDiagnosis.setText(R.string.diagnosis_en);
        txtMedication.setText(R.string.medication_en);

        myacc.setTitle(R.string.my_profile_en);
        mylogout.setTitle(R.string.logout_en);
        myschedule.setTitle(R.string.schedule_en);
        mysettings.setTitle(R.string.settings_en);
    }

    @Override
    public void setTextSize() {
        userName.setTextSize(Integer.parseInt(textSize));
        userAge.setTextSize(Integer.parseInt(textSize));
        userAdress.setTextSize(Integer.parseInt(textSize));
        userDiagnosis.setTextSize(Integer.parseInt(textSize));
        userMedication.setTextSize(Integer.parseInt(textSize));
        txtAge.setTextSize(Integer.parseInt(textSize));
        txtAdress.setTextSize(Integer.parseInt(textSize));
        txtDiagnosis.setTextSize(Integer.parseInt(textSize));
        txtMedication.setTextSize(Integer.parseInt(textSize));
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class sendToken extends AsyncTask<String, String, String> {

        protected String doInBackground(String... strings) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                //Устанавливаем параметры
                params.add(new BasicNameValuePair(TAG_PID, Integer.toString(user.getId())));
                params.add(new BasicNameValuePair(TAG_TOKEN, PersistantStorageUtils.getToken(getApplicationContext())));

                // отправляем информацию через запрос HTTP POST
                JSONObject jsonRecord = jsonParser.makeHttpRequest(url_send_fcm_token, "GET", params);

                // ответ от json о записи
                Log.d("fcm", jsonRecord.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
