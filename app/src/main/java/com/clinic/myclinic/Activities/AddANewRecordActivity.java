package com.clinic.myclinic.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.clinic.myclinic.Classes.Doctors;
import com.clinic.myclinic.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AddANewRecordActivity extends AppCompatActivity {

    TextView txtSelectCategory, txtSelectDoctor, txtSelectDate, txtSelectTime;
    Spinner spCategory, spDoctors, spTimeSelecter;
    EditText edtAnnotation;
    Button btnSelectTime, btnConfirm;
    ArrayList<String> categories;

    Doctors doctors = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_new_record);

        txtSelectCategory = findViewById(R.id.txtSelectCategory);
        txtSelectDoctor = findViewById(R.id.txtSelectDoctor);
        txtSelectDate = findViewById(R.id.txtSelectedDate);
        txtSelectTime = findViewById(R.id.txtSelectedTime);
        spCategory = findViewById(R.id.spCategory);
        spDoctors = findViewById(R.id.spDoctors);
        spTimeSelecter = findViewById(R.id.spTimeSelecter);
        edtAnnotation = findViewById(R.id.edtCause);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnSelectTime = findViewById(R.id.btnSelectTime);

        doctors = new Doctors(this);

        spTimeSelecter.setEnabled(false);
        spDoctors.setEnabled(false);

        //Адаптер для категорий
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, doctors.getCategories());
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapterCategory);
        spCategory.setSelection(0);
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
            }
        });
    }

    private void setAdapterForDoctorsSP(String selectedCategorie) {
        //Адаптер для докторов
        ArrayAdapter<String> adapterDoc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, doctors.getNames(selectedCategorie));
        adapterDoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDoctors.setAdapter(adapterDoc);
        spDoctors.setSelection(0);
        spDoctors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_doc_name = spTimeSelecter.getSelectedItem().toString();
                //setAdapterForTimeSelectionSP(selected_doc_name);
                //spTimeSelecter.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spTimeSelecter.setEnabled(false);
            }
        });
    }

    //TODO: сделать!
//    private void setAdapterForTimeSelectionSP(String doc_name) {
//        //Адаптер для времени
//        ArrayAdapter<String> adapterTimes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, doctors.getTimeDuty(doc_name));
//
//        adapterTimes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spTimeSelecter.setAdapter(adapterTimes);
//        spTimeSelecter.setSelection(0);
//    }
}
