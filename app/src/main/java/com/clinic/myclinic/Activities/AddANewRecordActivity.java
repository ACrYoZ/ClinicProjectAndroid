package com.clinic.myclinic.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class AddANewRecordActivity extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    private static String url_send_record = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/send_record.php";
    private ProgressDialog pDialog;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DID = "did";
    private static final String TAG_PID = "pid";
    private static final String TAG_DATE = "date";
    private static final String TAG_PASSED = "passed";
    private static final String TAG_ANNOTATION = "annotation";

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

        user = new User(this);
        doctors = new Doctors(this);

        spTimeSelecter.setEnabled(false);
        spDoctors.setEnabled(false);
        spDateSelecter.setEnabled(false);

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

        btnConfirm.setOnClickListener(v->{
            if(edtAnnotation.getText().toString().length() > 5 &&
               spDateSelecter.getSelectedItem() != null &&
               spDoctors.getSelectedItem() != null &&
               spTimeSelecter.getSelectedItem() != null &&
               spCategory.getSelectedItem() != null){
               new sendRecData().execute();

               //TODO: заменить
                Toasty.info(ctx, "Ваша запись отправлена в регистратуру", Toast.LENGTH_SHORT).show();

            }
               else { Toasty.warning(this, "Не все поля заданы корректно", Toast.LENGTH_SHORT).show(); }
        });
    }

    private class sendRecData extends AsyncTask<String, String, String> {
        // Сначала покажем диалоговое окно прогресса
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(AddANewRecordActivity.this);
            pDialog.setMessage("Отправка. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            StringBuilder sb = new StringBuilder();
            sb.append(spDateSelecter.getSelectedItem().toString() + " " + spTimeSelecter.getSelectedItem().toString());
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
                JSONObject jsonRecord = jsonParser.makeHttpRequest(url_send_record, "POST", params);

                // ответ от json о записи
                Log.d("AddRecord Json", jsonRecord.toString());

                //TODO: не работает toast, нужно исправить
                //Toasty.info(ctx, "Ваша запись отправлена в регистратуру", Toast.LENGTH_SHORT).show();
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
        ArrayAdapter<String> adapterDates = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, doctors.getDateDuty(doc_name, pos));
        adapterDates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDateSelecter.setAdapter(adapterDates);
    }


    //TODO: сделать!
    private void setAdapterForTimeSelectionSP(String doc_name, int pos) {
        //Адаптер для времени
        ArrayAdapter<String> adapterTimes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, doctors.getTimeDuty(doc_name, pos));
        adapterTimes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTimeSelecter.setAdapter(adapterTimes);
    }
}
