package com.clinic.myclinic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clinic.myclinic.Classes.Record;

import java.util.ArrayList;
import com.clinic.myclinic.R;

import at.markushi.ui.CircleButton;

public class RecordsAdapter extends ArrayAdapter<Record> {
    private LayoutInflater inflater;             // для загрузки разметки элемента
    private int layout;                          // идентфикатор файла разметки
    private ArrayList<Record> records;           // коллекция выводимых данных

    public RecordsAdapter(Context context, int res, ArrayList<Record> records){
        super(context, res, records);

        this.inflater = LayoutInflater.from(context);
        this.layout = res;
        this.records = records;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = inflater.inflate(this.layout, parent, false);

        //Ссылки элементов
        TextView txtDoctorName = view.findViewById(R.id.txtDoctor);
        TextView txtDateTime = view.findViewById(R.id.txtDateTime);
        TextView txtCause = view.findViewById(R.id.txtCause);
        CircleButton cbReject = view.findViewById(R.id.cbReject);

        //ссылка на объект записей
        final Record item = records.get(position);


        txtDoctorName.setText(item.getDoctorName());
        txtDateTime.setText(item.getDateTime().toString());
        txtCause.setText(item.getCause());

        //ссылка на готовый элемент
        return view;
    }
}
