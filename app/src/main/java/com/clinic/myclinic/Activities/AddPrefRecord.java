package com.clinic.myclinic.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.TimeUnit;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clinic.myclinic.Adapters.DoctorsAdapter;
import com.clinic.myclinic.Classes.Doctor;
import com.clinic.myclinic.Classes.Doctors;
import com.clinic.myclinic.Classes.User;
import com.clinic.myclinic.Interfaces.SettingsInterface;
import com.clinic.myclinic.Interfaces.onCategoriesDataReceived;
import com.clinic.myclinic.Interfaces.onDoctorsDataReceived;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.JSONParser;
import com.clinic.myclinic.Utils.PersistantStorageUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AddPrefRecord extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
                                                                com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener,
                                                                onCategoriesDataReceived,
                                                                onDoctorsDataReceived,
                                                                SettingsInterface {

    private static String url_send_record = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/send_record.php";

    private static final String TAG_DID = "did";
    private static final String TAG_PID = "pid";
    private static final String TAG_DATE = "date";
    private static final String TAG_PASSED = "passed";
    private static final String TAG_ANNOTATION = "annotation";
    private static final int MAX_DISAGREES = 15;

    JSONParser jsonParser = new JSONParser();

    Button btnSelectDate, btnSelectTime, btnSearch;
    TextView txtDate, txtTime, txtRecordText, txtSelectCategory;
    Spinner spCategory;
    ListView lvAvaliableDoctors;

    private ProgressDialog pDialog;

    Context ctx;
    View v;

    Calendar now = Calendar.getInstance();

    String date, time;

    Doctors doctors;
    Doctors filteredDocs;
    Doctor doc;

    User user;

    DoctorsAdapter adapter;

    String language;
    String textSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pref_record);

        language = PersistantStorageUtils.getLanguagePreferences(this);
        textSize = PersistantStorageUtils.getTextSizePreferences(this);

        ctx = this;
        v = getWindow().getDecorView().getRootView();

        btnSearch = findViewById(R.id.btnSearch);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);

        txtDate = findViewById(R.id.txtSDate);
        txtTime = findViewById(R.id.txtSTime);
        txtRecordText = findViewById(R.id.txtRecordText1);
        txtSelectCategory = findViewById(R.id.txtSelectCategory1);

        spCategory = findViewById(R.id.spSCateg);

        spCategory.setBackgroundColor(Color.WHITE);

        lvAvaliableDoctors = findViewById(R.id.lvAvaliableDoctors);

        language = PersistantStorageUtils.getLanguagePreferences(this);

        switch (language){
            case "ru":
                setRussianLocale();
                break;
            case "en":
                setEnglishLocale();
                break;
        }

        textSize = PersistantStorageUtils.getTextSizePreferences(this);

        setTextSize();

        lvAvaliableDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView txt_phone = view.findViewById(R.id.txtPhoneCard);
                String phone = txt_phone.getText().toString();
                doc = doctors.getDoctorByPhone(phone);

                if (user.getCountDisagree() < MAX_DISAGREES) {
                    if (language.equals("en")) {
                        Snackbar snackbar = Snackbar.make(v, R.string.create_the_record_en, Snackbar.LENGTH_INDEFINITE);
                        snackbar.setActionTextColor(Color.WHITE);
                        snackbar.setAction("OK", vl -> {
                            new sendRecData().execute();
                            snackbar.dismiss();
                        });
                        snackbar.show();
                    } else {
                        Snackbar snackbar = Snackbar.make(v, R.string.create_the_record_ru, Snackbar.LENGTH_INDEFINITE);
                        snackbar.setActionTextColor(Color.WHITE);
                        snackbar.setAction("OK", vl -> {
                            new sendRecData().execute();
                            snackbar.dismiss();
                        });
                        snackbar.show();
                    }
                } else {
                    if (language.equals("ru")) {
                        Snackbar snackbar = Snackbar.make(v, R.string.you_cant_do_it_ru, Snackbar.LENGTH_INDEFINITE);
                        snackbar.setActionTextColor(Color.WHITE);
                        snackbar.setAction("OK", vl -> {
                            snackbar.dismiss();
                        });
                        snackbar.show();
                    } else {
                        Snackbar snackbar = Snackbar.make(v, R.string.you_cant_do_it_en, Snackbar.LENGTH_INDEFINITE);
                        snackbar.setActionTextColor(Color.WHITE);
                        snackbar.setAction("OK", vl -> {
                            snackbar.dismiss();
                        });
                        snackbar.show();
                    }
                }//else-if
            }
        });

        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    AddPrefRecord.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.setVersion(DatePickerDialog.Version.VERSION_2);
            dpd.show(getFragmentManager(), "");
        });

        btnSelectTime.setOnClickListener(v -> {


            TimePickerDialog tpd = TimePickerDialog.newInstance(
                    AddPrefRecord.this,
                    true
            );
            tpd.setVersion(TimePickerDialog.Version.VERSION_2);
            tpd.show(getFragmentManager(), "");
        });

        btnSearch.setOnClickListener(v -> {
            beginSearch();
        });

        if (isOnline()) {
            doctors = new Doctors(this);
            doctors.setOnDoctorsDataReceived(this);
            doctors.onDoctorsDataReceivedUpdateComponents();
            doctors.setOnCategoriesDataReceived(this);
            doctors.onCategoriesDataReceivedUpdateComponents();

            user = new User(this);

        } else {
            if (language.equals("ru")) {
                Snackbar snackbar = Snackbar.make(v, R.string.offline_mode_ru, Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.setAction("Ok", vl -> {
                    snackbar.dismiss();
                });
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(v, R.string.offline_mode_en, Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.setAction("Ok", vl -> {
                    snackbar.dismiss();
                });
                snackbar.show();
            }
        }
    }

    private void beginSearch() {
        ArrayList<Doctor> docs_tmp = new ArrayList<>();

        if (txtDate.getText().length() > 0 && txtTime.getText().length() > 0) {

            for (Doctor doc : doctors.getDoctors()) {
                String[] timeDuty = doctors.getTimeDuty(doc.getName());
                String[] dateDuty = doctors.getDateDuty(doc.getName(), "en");

                for (int i = 0; i < timeDuty.length; i++) {
                    if (spCategory.getSelectedItem().toString().equals(doc.getPosition())) {
                        if (txtTime.getText().toString().equals(timeDuty[i])) {
                            for (int j = 0; j < dateDuty.length; j++) {
                                if (txtDate.getText().toString().equals(dateDuty[j])) {
                                    docs_tmp.add(doc);
                                }//if
                            }//for
                        }//if
                    }//for
                }//if
            }//for
            if(docs_tmp.size() == 0) {
                if(language.equals("en")) {
                    Snackbar.make(v, R.string.doctor_wasnt_founded_en, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(v, R.string.doctor_wasnt_founded_ru, Snackbar.LENGTH_SHORT).show();
                }
            }
        }//if
        filteredDocs = new Doctors(docs_tmp);

        adapter = new DoctorsAdapter(AddPrefRecord.this, R.layout.list_doctors_adapter_layout, filteredDocs.getDoctors());

        lvAvaliableDoctors.setAdapter(adapter);

    }//beginSearch

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        StringBuilder dateBuilder = new StringBuilder();

        dateBuilder.append(year + "-");

        monthOfYear++;
        if (monthOfYear < 10) {
            dateBuilder.append("0" + monthOfYear + "-");
        } else {
            dateBuilder.append(monthOfYear + "-");
        }

        if (dayOfMonth < 10) {
            dateBuilder.append("0" + dayOfMonth);
        } else {
            dateBuilder.append(dayOfMonth);
        }

        this.date = dateBuilder.toString();
        txtDate.setText(date);
    }

    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (hourOfDay < 10) {
            time = "0" + Integer.toString(hourOfDay) + ":00:00";
        } else {
            time = Integer.toString(hourOfDay) + ":00:00";
        }
        if (minute != 0) {
            if(language.equals("en")) {
                Toasty.warning(ctx, getString(R.string.warning_time_en), Toast.LENGTH_SHORT, true).show();
            } else {
                Toasty.warning(ctx, getString(R.string.warning_time_ru), Toast.LENGTH_SHORT, true).show();
            }
        }
        txtTime.setText(time);
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

    private void setAdapterForCategoriesSP() {
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, doctors.getCategories());
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapterCategory);
    }

    @Override
    public void setRussianLocale() {
        btnSelectTime.setText(R.string.select_time_btn_ru);
        btnSelectDate.setText(R.string.select_date_btn_ru);
        btnSearch.setText(R.string.search_text_ru);
        txtSelectCategory.setText(R.string.select_category_ru);
        txtRecordText.setText(R.string.fab_add_new_record_ru);
    }

    @Override
    public void setEnglishLocale() {
        btnSelectTime.setText(R.string.select_time_btn_en);
        btnSelectDate.setText(R.string.select_date_btn_en);
        btnSearch.setText(R.string.search_text_en);
        txtSelectCategory.setText(R.string.select_category_en);
        txtRecordText.setText(R.string.fab_add_new_record_en);
    }

    @Override
    public void setTextSize() {
        btnSearch.setTextSize(Integer.parseInt(textSize));
        btnSelectTime.setTextSize(Integer.parseInt(textSize));
        btnSelectDate.setTextSize(Integer.parseInt(textSize));
        txtSelectCategory.setTextSize(Integer.parseInt(textSize));
        txtRecordText.setTextSize(Integer.parseInt(textSize));
    }

    private class sendRecData extends AsyncTask<String, String, String> {
        // Сначала покажем диалоговое окно прогресса
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(AddPrefRecord.this);
            if (language.equals("ru")) {
                pDialog.setMessage(getString(R.string.send_info_ru));
            } else {
                pDialog.setMessage(getString(R.string.send_info_en));
            }
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            StringBuilder date_time = new StringBuilder();

            date_time.append(date).append(" " + time);

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                //Устанавливаем параметры
                params.add(new BasicNameValuePair(TAG_DID, Integer.toString(doc.getId())));
                params.add(new BasicNameValuePair(TAG_PID, Integer.toString(user.getId())));
                params.add(new BasicNameValuePair(TAG_DATE, date_time.toString()));
                params.add(new BasicNameValuePair(TAG_PASSED, "0"));
                params.add(new BasicNameValuePair(TAG_ANNOTATION, "No annotation"));

                // отправляем информацию через запрос HTTP POST
                JSONObject jsonRecord = jsonParser.makeHttpRequest(url_send_record, "GET", params);

                // ответ от json о записи
                Log.d("AddRecord Json", jsonRecord.toString());

                if(language.equals("en")) {
                    Snackbar snackbar = Snackbar.make(v, R.string.record_sending_en, Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(v, R.string.record_sending_ru, Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // закрываем диалоговое окно с индикатором прогресса
            pDialog.dismiss();

            finish();
        }
    }
}
