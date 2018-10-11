package com.clinic.myclinic.Activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.clinic.myclinic.Interfaces.SettingsInterface;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.JSONParser;
import com.clinic.myclinic.Utils.PersistantStorageUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AboutClinicActivity extends FragmentActivity implements OnMapReadyCallback, SettingsInterface {

    private static String url_get_clinic_info = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/get_clinic_info.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CLINIC = "clinic";
    private static final String TAG_LATITUDE = "lat";
    private static final String TAG_LONGITUDE = "long";
    private static final String TAG_TRANSPORT = "trans";
    private static final String TAG_PHONE = "phone";

    private GoogleMap clinicMap;

    JSONParser jsonParser = new JSONParser();

    float lat, longit;
    String trans, phone;

    public  String language;
    public  String textSize;

    TextView txtPhones, txtTransport, txtPhonesText, txtTransportText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_clinic);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if(isOnline()) { 
            //Начинаем получение данных о клинике
            new GetClinicInfo().execute();
        }

        txtPhones = findViewById(R.id.txtAvaliablePhones);
        txtTransport = findViewById(R.id.txtNearestTransport);
        txtPhonesText = findViewById(R.id.txtAvaliablePhonesText);
        txtTransportText = findViewById(R.id.txtNearestTransportText);

        //Получаем актуальный язык
        language = PersistantStorageUtils.getLanguagePreferences(this);
        //Получаем актуальный размер шрифта
        textSize = PersistantStorageUtils.getTextSizePreferences(this);

        switch (language){
            case "ru":
                setRussianLocale();
                break;
            case "en":
                setEnglishLocale();
                break;
        }

        setTextSize();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        clinicMap = googleMap;

        //Разрешаем/запрещаем (в зависимости от настроек) пользователю доступ к полному управлению картой.
        clinicMap.getUiSettings().setAllGesturesEnabled(PersistantStorageUtils.getMapPreferences(this));

        LatLng clinic = new LatLng(48.0219, 37.7985);
        clinicMap.addMarker(new MarkerOptions().position(clinic).title("Clinic!")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.heart)));
        /* CameraPosition имеет атрибуты:
        - target, тип LatLng с полями-координатами: latitude, longitude. Это точка, на которую смотрит камера.
        - bearing, угол поворота камеры от севера по часовой
        - tilt, угол наклона камеры
        - zoom, текущий уровень зума */
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(clinic)
                .zoom(15)
                .bearing(45)
                .tilt(20)
                .build();
        clinicMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void setRussianLocale() {
        txtTransportText.setText(R.string.nearest_transport_ru);
        txtPhonesText.setText(R.string.avaliable_phones_ru);
    }

    @Override
    public void setEnglishLocale() {
        txtTransportText.setText(R.string.nearest_transport_en);
        txtPhonesText.setText(R.string.avaliable_phones_en);
    }

    @Override
    public void setTextSize() {
        txtPhonesText.setTextSize(Integer.parseInt(textSize));
        txtPhones.setTextSize(Integer.parseInt(textSize));
        txtTransportText.setTextSize(Integer.parseInt(textSize));
        txtTransport.setTextSize(Integer.parseInt(textSize));
    }

    // AsyncTask для получения данных о клинике
    private class GetClinicInfo extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            // проверяем тег success
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                // получаем информацию через запрос HTTP GET
                JSONObject jsonAboutClinic = jsonParser.makeHttpRequest(url_get_clinic_info, "GET", params);

                if(jsonAboutClinic != null) {
                    // ответ от json о записях
                    Log.d("ClinicInfo arr Json", jsonAboutClinic.toString());


                    JSONArray clinicObj = jsonAboutClinic.getJSONArray(TAG_CLINIC);

                    for (int i = 0; i < clinicObj.length(); i++) {
                        // получим первый объект из массива JSON Array и установим необходимые поля
                        JSONObject categ = clinicObj.getJSONObject(i);

                        //получаем данные из массива и записываем их в отдельную переменную
                        lat = categ.getLong(TAG_LATITUDE);
                        longit = categ.getLong(TAG_LONGITUDE);
                        trans = categ.getString(TAG_TRANSPORT);
                        phone = categ.getString(TAG_PHONE);
                    }
                    setData();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void setData() {
        txtTransport.setText(trans);
        txtPhones.setText(phone);
    }
}
