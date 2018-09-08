package com.clinic.myclinic.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clinic.myclinic.Classes.Doctor;
import com.clinic.myclinic.Classes.Record;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.PersistantStorageUtils;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

public class DoctorsAdapter extends ArrayAdapter<Doctor> {

    private LayoutInflater inflater;             // для загрузки разметки элемента
    private int layout;                          // идентфикатор файла разметки
    private ArrayList<Doctor> doctors;           // коллекция выводимых данных

    public static String language;
    public static String textSize;

    TextView txtDocName, txtDescription;
    Context ctx;

    public DoctorsAdapter(Context context, int res, ArrayList<Doctor> doctors){
        super(context, res, doctors);

        this.inflater = LayoutInflater.from(context);
        this.layout = res;
        this.doctors = doctors;
        ctx = context;
    }
//TODO(programmer): Допилить, т.к. не сделано нихрена
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view = inflater.inflate(this.layout, parent, false);

        //Ссылки элементов
        txtDocName = view.findViewById(R.id.txtDoctorFullName);
        txtDescription = view.findViewById(R.id.txtDescription);

        language = PersistantStorageUtils.getLanguagePreferences(ctx);

        //ссылка на объект записей
        final Doctor item = doctors.get(position);
        

        txtDocName.setText(item.getName());
        txtDescription.setText(item.getPosition());

        //Получаем актуальный размер шрифта
        textSize = PersistantStorageUtils.getTextSizePreferences(ctx);
        setTextSize();

        //ссылка на готовый элемент
        return view;
    }

    private void setTextSize() {
        txtDocName.setTextSize(Integer.parseInt(textSize));
        txtDescription.setTextSize(Integer.parseInt(textSize));
    }

}
