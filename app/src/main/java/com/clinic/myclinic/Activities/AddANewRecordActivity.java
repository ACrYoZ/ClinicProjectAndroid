package com.clinic.myclinic.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clinic.myclinic.Classes.Doctors;
import com.clinic.myclinic.Classes.User;
import com.clinic.myclinic.Interfaces.SettingsInterface;
import com.clinic.myclinic.Interfaces.onCategoriesDataReceived;
import com.clinic.myclinic.Interfaces.onDoctorsDataReceived;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.JSONParser;
import com.clinic.myclinic.Utils.PersistantStorageUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class AddANewRecordActivity extends AppCompatActivity
                                   implements SettingsInterface
                                              , onCategoriesDataReceived
                                              , onDoctorsDataReceived {

    public  String language;
    public  String textSize;

    JSONParser jsonParser = new JSONParser();
    private static String url_send_record = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/send_record.php";
    private ProgressDialog pDialog;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DID = "did";
    private static final String TAG_PID = "pid";
    private static final String TAG_DATE = "date";
    private static final String TAG_PASSED = "passed";
    private static final String TAG_ANNOTATION = "annotation";
    private static final int MAX_DISAGREES = 15;

    TextView txtSelectCategory, txtSelectDoctor, txtSelectDate, txtSelectTime;
    Spinner spCategory, spDoctors, spTimeSelecter, spDateSelecter;
    EditText edtAnnotation;
    Button  btnConfirm;
    ArrayList<String> categories;
    Context ctx = null;

    Doctors doctors = null;
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_new_record);

        ctx = this;

        txtSelectCategory = findViewById(R.id.txtSelectCategory);
        txtSelectDoctor = findViewById(R.id.txtSelectDoctor);
        txtSelectDate = findViewById(R.id.txtSelectedDate);
        txtSelectTime = findViewById(R.id.txtSelectedTime);
        spCategory = findViewById(R.id.spCategory);
        spDoctors = findViewById(R.id.spDoctors);
        spTimeSelecter = findViewById(R.id.spTimeSelecter);
        spDateSelecter = findViewById(R.id.spDateSelecter);
        edtAnnotation = findViewById(R.id.edtCause);
        btnConfirm = findViewById(R.id.btnConfirm);

        spTimeSelecter.setEnabled(false);
        spDoctors.setEnabled(false);
        spDateSelecter.setEnabled(false);

        //Получаем актуальный язык
        language = PersistantStorageUtils.getLanguagePreferences(this);

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

        if(isOnline()) {

            user = new User(this);
            doctors = new Doctors(this);
            doctors.setOnCategoriesDataReceived(this);
            doctors.onCategoriesDataReceivedUpdateComponents();
            doctors.setOnDoctorsDataReceived(this);
            doctors.onDoctorsDataReceivedUpdateComponents();

            //Уведомляем пользователя о том, что мы получаем данные и ему необходимо подождать.
            switch (language) {
                case "ru":
                    Toasty.info(this, "Получение данных. Пожалуйста, ожидайте...", Toast.LENGTH_SHORT).show();
                    break;

                case "en":
                    Toasty.info(this, "Receiving data. Please, wait...", Toast.LENGTH_SHORT).show();
                    break;
            }

            btnConfirm.setOnClickListener(v -> {
                if (edtAnnotation.getText().toString().length() > 5 &&
                        spDateSelecter.getSelectedItem() != null &&
                        spDoctors.getSelectedItem() != null &&
                        spTimeSelecter.getSelectedItem() != null &&
                        spCategory.getSelectedItem() != null &&
                        user.getCountDisagree() <= MAX_DISAGREES) {
                    new sendRecData().execute();
                } else {
                    if(language.equals("ru")) {
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), R.string.exception_on_add_rec_ru, Snackbar.LENGTH_INDEFINITE);
                        snackbar.setActionTextColor(Color.WHITE);
                        snackbar.setAction("Ok", vl -> {
                            snackbar.dismiss();
                        });
                        snackbar.show();
                    } else {
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), R.string.exception_on_add_rec_en, Snackbar.LENGTH_INDEFINITE);
                        snackbar.setActionTextColor(Color.WHITE);
                        snackbar.setAction("Ok", vl -> {
                            snackbar.dismiss();
                        });
                        snackbar.show();
                    }
                }
            });
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
    }

    @Override
    public void setRussianLocale() {
        txtSelectCategory.setText(R.string.select_category_ru);
        txtSelectDoctor.setText(R.string.select_the_doctor_ru);
        txtSelectDate.setText(R.string.selected_date_ru);
        txtSelectTime.setText(R.string.selected_time_ru);
        edtAnnotation.setHint(R.string.cause_ru);
        btnConfirm.setText(R.string.confirm_ru);
    }

    @Override
    public void setEnglishLocale() {
        txtSelectCategory.setText(R.string.select_category_en);
        txtSelectDoctor.setText(R.string.select_the_doctor_en);
        txtSelectDate.setText(R.string.selected_date_en);
        txtSelectTime.setText(R.string.selected_time_en);
        edtAnnotation.setHint(R.string.cause_en);
        btnConfirm.setText(R.string.confirm_en);
    }

    @Override
    public void setTextSize() {
        txtSelectCategory.setTextSize(Integer.parseInt(textSize));
        txtSelectDoctor.setTextSize(Integer.parseInt(textSize));
        txtSelectDate.setTextSize(Integer.parseInt(textSize));
        txtSelectTime.setTextSize(Integer.parseInt(textSize));
        edtAnnotation.setTextSize(Integer.parseInt(textSize));
        btnConfirm.setTextSize(Integer.parseInt(textSize));
    }

    private class sendRecData extends AsyncTask<String, String, String> {
        // Сначала покажем диалоговое окно прогресса
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(AddANewRecordActivity.this);
            if (language.equals("ru")){
                pDialog.setMessage(getString(R.string.send_info_ru));
            } else {
                pDialog.setMessage(getString(R.string.send_info_en));
            }
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            StringBuilder sb = new StringBuilder();

            if(language.equals("ru")){
                sb.append(getRightDate(spDateSelecter.getSelectedItem().toString()));
            } else { sb.append(spDateSelecter.getSelectedItem().toString()); }

            sb.append(" " + spTimeSelecter.getSelectedItem().toString());
            String docName = spDoctors.getSelectedItem().toString();

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                //Устанавливаем параметры
                params.add(new BasicNameValuePair(TAG_DID, doctors.getDoctorIDByName(docName)));
                params.add(new BasicNameValuePair(TAG_PID, Integer.toString(user.getId())));
                params.add(new BasicNameValuePair(TAG_DATE, sb.toString()));
                params.add(new BasicNameValuePair(TAG_PASSED, "0"));
                params.add(new BasicNameValuePair(TAG_ANNOTATION, edtAnnotation.getText().toString()));

                // отправляем информацию через запрос HTTP POST
                JSONObject jsonRecord = jsonParser.makeHttpRequest(url_send_record, "GET", params);

                // ответ от json о записи
                Log.d("AddRecord Json", jsonRecord.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // закрываем диалоговое окно с индикатором прогресса
            pDialog.dismiss();

            Intent intent = new Intent(AddANewRecordActivity.this, RecordsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private String getRightDate(String datefull) {
        StringBuilder sb = new StringBuilder();
        String date = datefull.substring(0, 2);
        String month = datefull.substring(2, 5);
        String year = datefull.substring(6, 10);

        sb.append(year + "-" + month + "-" + date);

        return sb.toString();
    }

    private void setAdapterForCategoriesSP() {
        //Адаптер для категорий
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, doctors.getCategories());
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapterCategory);
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedCategorie = spCategory.getSelectedItem().toString();
                setAdapterForDoctorsSP(selectedCategorie);
                spDoctors.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spTimeSelecter.setEnabled(false);
                spDoctors.setEnabled(false);
                spTimeSelecter.setEnabled(false);
            }
        });
    }

    private void setAdapterForDoctorsSP(String selectedCategorie) {
        //Адаптер для докторов
        ArrayAdapter<String> adapterDoc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, doctors.getNames(selectedCategorie));
        adapterDoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDoctors.setAdapter(adapterDoc);
        spDoctors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_doc_name = spDoctors.getSelectedItem().toString();

                setAdapterForDateSelectionSP(selected_doc_name, position);
                setAdapterForTimeSelectionSP(selected_doc_name, position);

                spTimeSelecter.setEnabled(true);
                spDateSelecter.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spTimeSelecter.setEnabled(false);
                spDateSelecter.setEnabled(false);
            }
        });
    }

    private void setAdapterForDateSelectionSP(String doc_name, int pos) {
        //Адаптер для даты
        ArrayAdapter<String> adapterDates = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, doctors.getDateDuty(doc_name, language));
        adapterDates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDateSelecter.setAdapter(adapterDates);
    }

    private void setAdapterForTimeSelectionSP(String doc_name, int pos) {
        //Адаптер для времени
        ArrayAdapter<String> adapterTimes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, doctors.getTimeDuty(doc_name));
        adapterTimes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTimeSelecter.setAdapter(adapterTimes);
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onCategoriesDataReceivedUpdateComponents() {
        //Почему я использую handler - описано в классе UserProfileActivity.java в строке 334
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                //Устанавливаем адаптер. Дальнейшие необходимые адаптеры он установит сам. Подробнее: см. код метода
                setAdapterForCategoriesSP();
            }
        });
    }

    @Override
    public void onDoctorsDataReceivedUpdateComponents() {
    }
}
