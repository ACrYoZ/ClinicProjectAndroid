package com.clinic.myclinic.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.clinic.myclinic.Adapters.RecordsAdapter;
import com.clinic.myclinic.Classes.Record;
import com.clinic.myclinic.Classes.User;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.CircularTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import es.dmoral.toasty.Toasty;

import com.clinic.myclinic.Interfaces.onCircleButtonClickListener;

public class RecordsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, onCircleButtonClickListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navView;

    private Toolbar mToolbar;

    boolean flag;   //Вспомогательная переменная-флаг для слушателя OnScrollListener

    //Объявляем компоненты интерфейса
    ImageView userPhotoNavigationDrawer;
    TextView userEmail, userNameNavigationDrawer;
    ListView lvRecords;
    FloatingActionButton fab;

    //Объявляем пользователя
    User user = null;

    RecordsAdapter adapter;
    ArrayList<Record> records;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        records = new ArrayList<>(Arrays.asList(
                new Record("Божко Н.Н.", "Беспокоит головная боль", "18.07.2018"),
                new Record("Темченко Е.А.", "Звон в ушах", "24.07.2018"),
                new Record("Афонасенко Д.Д.", "Перелом ноги", "30.07.2018"),
                new Record("Божко Н.Н.", "Беспокоит головная боль", "18.07.2018"),
                new Record("Темченко Е.А.", "Звон в ушах", "24.07.2018"),
                new Record("Божко Н.Н.", "Беспокоит головная боль", "18.07.2018"),
                new Record("Темченко Е.А.", "Звон в ушах", "24.07.2018"),
                new Record("Божко Н.Н.", "Беспокоит головная боль", "18.07.2018"),
                new Record("Темченко Е.А.", "Звон в ушах", "24.07.2018"),
                new Record("Божко Н.Н.", "Беспокоит головная боль", "18.07.2018"),
                new Record("Темченко Е.А.", "Звон в ушах", "24.07.2018"),
                new Record("Божко Н.Н.", "Беспокоит головная боль", "18.07.2018"),
                new Record("Темченко Е.А.", "Звон в ушах", "24.07.2018"),
                new Record("Божко Н.Н.", "Беспокоит головная боль", "18.07.2018"),
                new Record("Темченко Е.А.", "Звон в ушах", "24.07.2018"),
                new Record("Божко Н.Н.", "Беспокоит головная боль", "18.07.2018"),
                new Record("Темченко Е.А.", "Звон в ушах", "24.07.2018"),
                new Record("Божко Н.Н.", "Беспокоит головная боль", "18.07.2018"),
                new Record("Темченко Е.А.", "Звон в ушах", "24.07.2018"),
                new Record("Божко Н.Н.", "Беспокоит головная боль", "18.07.2018"),
                new Record("Темченко Е.А.", "Звон в ушах", "24.07.2018"),
                new Record("Божко Н.Н.", "Беспокоит головная боль", "18.07.2018"),
                new Record("Темченко Е.А.", "Звон в ушах", "24.07.2018"),
                new Record("Божко Н.Н.", "Беспокоит головная боль", "18.07.2018")
        ));


        //Создание пользователя TODO: должен получать данные с сервера, не вручную заполнять. И вообще, получать из старой активности или Presistant Storage
        user = new User(
                "mrtvzat2013@yandex.com",
                "https://pp.userapi.com/c836731/v836731946/4d031/i8MtY2l3c5Q.jpg",
                "Vladislav",
                "Tarapata",
                "Valeriyevich",
                "18",
                "Gertsina 12-B",
                "Good Health",
                "Haven't Medication"
        );

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

        navView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Picasso.get()
                .load(user.getUserPhoto())
                .resize(100, 100)
                .centerCrop()
                .transform(new CircularTransformation())
                .into(userPhotoNavigationDrawer);

        userNameNavigationDrawer.setText(user.getUserName() + " " + user.getUserPatronymic() + " " + user.getUserSurname());
        userEmail.setText(user.getUserEmail());


        //Получаем FAB
        fab = findViewById(R.id.fab_add);

        //Устанавливаем слушателя клика по нашей FAB TODO: Допилить
        fab.setOnClickListener(v -> {
            startAddNewRecordActivity();
        });

        adapter = new RecordsAdapter(this, R.layout.list_records_adapter_layout, records);

        lvRecords = findViewById(R.id.lvRecords);

        // подписываем нашу активити на события колбэка
        adapter.setOnCircleButtonClickListener(this);
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
                if (flag){
                    fab.animate().scaleX(0f).scaleY(0f).start();
                    flag = false;
                }
            }
        });

        Toasty.info(this, "Адаптер установлен", 100, true).show();
    }

    private void startAddNewRecordActivity() {
        Intent intent = new Intent(this, AddANewRecordActivity.class);
        startActivity(intent);
        finish();
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
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_records);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCircleButtonClick(View view, final int position) {
        records.remove(position);
        adapter = new RecordsAdapter(this, R.layout.list_records_adapter_layout, records);
        adapter.notifyDataSetChanged();
        lvRecords.setAdapter(adapter);
    }
}
