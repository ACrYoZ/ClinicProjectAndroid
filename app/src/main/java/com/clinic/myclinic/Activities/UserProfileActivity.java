package com.clinic.myclinic.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clinic.myclinic.Classes.DiagnosisList;
import com.clinic.myclinic.Classes.Doctor;
import com.clinic.myclinic.Classes.Doctors;
import com.clinic.myclinic.Classes.User;
import com.clinic.myclinic.Interfaces.SettingsInterface;
import com.clinic.myclinic.Interfaces.onCategoriesDataReceived;
import com.clinic.myclinic.Interfaces.onDoctorsDataReceived;
import com.clinic.myclinic.Interfaces.onUserDataReceived;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.CircularTransformation;
import com.clinic.myclinic.Utils.DBHelper;
import com.clinic.myclinic.Utils.JSONParser;
import com.clinic.myclinic.Utils.PersistantStorageUtils;
import com.clinic.myclinic.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class UserProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SettingsInterface,
        onUserDataReceived,
        onCategoriesDataReceived,
        onDoctorsDataReceived {

    public final static String SERVER = "myclinic.ddns.net:8080";

    private static String url_send_fcm_token = "http://" + SERVER + "/AndroidScripts/send_fcm_token.php";

    private static final String TAG_TOKEN = "token";
    private static final String TAG_PID = "pid";

    JSONParser jsonParser = new JSONParser();

    //Объект локальной бд
    DBHelper dbHelper;
    //Объект управления локальной бд
    SQLiteDatabase database;
    //Объект добавляемых данных в локальную бд
    ContentValues values;

    //Объект докторов
    Doctors doctors;

    Context ctx;

    public  String language;
    public  String textSize;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navView;
    private Menu navMenu;
    private MenuItem myacc, myschedule, mysettings, mylogout, myDoctors, clinicInfo, myDiagnoses;

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

        ctx = this;

        //Получаем актуальный язык
        language = PersistantStorageUtils.getLanguagePreferences(this);

        //Проверка. Авторизован ли пользователь? Нет? Выходим на активность входа.
        if (!AuthorizationUtils.isAuthorized(this)) {
            onLogout();
            return;
        }//if

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
        myDiagnoses = navMenu.findItem(R.id.nav_my_diagnoses);
        myDoctors = navMenu.findItem(R.id.nav_doctors);
        clinicInfo = navMenu.findItem(R.id.nav_info);

        //скрываем фото пользователя до его получения. Использую INVISIBLE т.к. он его просто не отображает, в то время как
        //GONE - полностью прячет слой.
        userPhoto.setVisibility(View.INVISIBLE);

        //Если сеть есть - берем данные с сервера. Если сети нет - создаем "null" пользователя
        if(isOnline()) {
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
                new sendToken().execute();
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

    private void startDoctorsActivity() {
        Intent intent = new Intent(this, AboutDoctorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void startAboutActicity() {
        Intent intent = new Intent(this, AboutClinicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void startDiagnosesActivity(){
        Intent intent = new Intent(this, DiagnosisListActivity.class);
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
                break;
            case R.id.nav_my_diagnoses:
                startDiagnosesActivity();
                break;
            case R.id.nav_logout:
                onLogout();
                break;
            case R.id.nav_settings:
                startSettingsActivity();
                break;
            case R.id.nav_doctors:
                startDoctorsActivity();
                break;
            case R.id.nav_info:
                startAboutActicity();
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
        myDiagnoses.setTitle(R.string.diagnoses_ru);
        mysettings.setTitle(R.string.settings_ru);
        myDoctors.setTitle(R.string.doctors_ru);
        clinicInfo.setTitle(R.string.about_clinic_ru);
    }

    @Override
    public void setEnglishLocale() {
        txtAge.setText(R.string.age_en);
        txtAdress.setText(R.string.adress_en);
        txtDiagnosis.setText(R.string.diagnosis_en);
        txtMedication.setText(R.string.medication_en);

        myacc.setTitle(R.string.my_profile_en);
        mylogout.setTitle(R.string.logout_en);
        myDiagnoses.setTitle(R.string.diagnoses_en);
        myschedule.setTitle(R.string.schedule_en);
        mysettings.setTitle(R.string.settings_en);
        myDoctors.setTitle(R.string.doctors_en);
        clinicInfo.setTitle(R.string.about_clinic_en);
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

    //TODO(programmer): адаптивная настройка размера ImageView
    @Override
    public void onUserDataReceivedUpdateComponents() {
        //Получаем разрешение экрана
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = (int)Utils.convertPxToDp(this, size.x);
        int height = (int)Utils.convertPxToDp(this, size.y);

        //!!! Важно !!!
        //Именно этот кусок кода, связанный с handler - позволяет работать с Picasso. Если убрать handler - то всё будет плачевно,
        //т.к. Picasso использует слабые ссылки на объекты, и с точки зрения безопасности, это нихрена не безопасно.
        //Но это только верхушка айсберга!
        //Дело в том, что компоненты интефрейса, могут быть изменены исключительно в своем потоке. Т.е. в жизненном цикле активности
        //onCreate, onResume, onPause и тд.
        //потому поток сразу же выбросит Exception: "java.lang.IllegalStateException: Method call should happen from the main thread." у Picasso
        //и "Only the original thread that created a view hierarchy can touch its views" у элементов типа TextView и т.д.
        //И будет прав. Так что для успешной работы - используйте Handler!
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
//                userPhoto.setScaleX(width);
//                userPhoto.setScaleY(height);
//
//                userPhoto.requestLayout(); // перестраиваем элемент
//
//                userPhotoNavigationDrawer.setScaleX(width/19);
//                userPhotoNavigationDrawer.setScaleY(height/19);

                Picasso.get()
                        .load(user.getUserPhoto())
                        .resize(300, 300)
                        .centerCrop()
                        .transform(new CircularTransformation())
                        .into(userPhoto);
                Picasso.get()
                        .load(user.getUserPhoto())
                        .resize(200, 200)
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

                userPhoto.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCategoriesDataReceivedUpdateComponents() {}

    @Override
    public void onDoctorsDataReceivedUpdateComponents() {
        new workWithDBAsycnc().execute();
    }

    private class workWithDBAsycnc extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {

            //Если бд существует - сносим её.
            if(ctx.getDatabasePath(dbHelper.DB_NAME) != null){
                ctx.deleteDatabase(dbHelper.DB_NAME);
            }

            //Создаем объект бд и саму базу данных
            dbHelper = new DBHelper(ctx);
            //Получаем доступ к базе. Writable - потому как нам нужен полный доступ. По чтению и записи.
            database = dbHelper.getWritableDatabase();

            //очищаем бд
            database.delete(dbHelper.DB_DOCTORS_TABLE, null, null);

            //Создаем объект передаваемых данных
            values = new ContentValues();

            if(dbHelper != null && values != null && database != null){
                ArrayList<Doctor> docs = doctors.getDoctors();

                //Записываем данные в специальный объект
                for(Doctor doc : docs) {
                    values.put(dbHelper.KEY_NAME, doc.getName());
                    values.put(dbHelper.KEY_POSITION, doc.getPosition());
                    values.put(dbHelper.KEY_PHONE, doc.getPhone());
                    values.put(dbHelper.KEY_PHOTO, doc.getPhotoURL());
                    values.put(dbHelper.KEY_RATING, doc.getRating());
                    values.put(dbHelper.KEY_PARLOR, doc.getParlor());
                    values.put(dbHelper.KEY_FROM, doc.getFrom());
                    values.put(dbHelper.KEY_TO, doc.getTo());
                    //Добавляем данные в таблицу
                    database.insert(DBHelper.DB_DOCTORS_TABLE, null, values);
                }
            }
            return null;
        }
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

                if(jsonRecord != null) {
                    // ответ от json о записи
                    Log.d("fcm", jsonRecord.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
