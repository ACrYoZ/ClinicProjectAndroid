package com.clinic.myclinic.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clinic.myclinic.Activities.RecordsActivity;
import com.clinic.myclinic.Classes.Record;

import java.util.ArrayList;

import com.clinic.myclinic.Interfaces.SettingsInterface;
import com.clinic.myclinic.Interfaces.onCircleButtonClickListener;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.PersistantStorageUtils;

import at.markushi.ui.CircleButton;

public class RecordsAdapter extends ArrayAdapter<Record>
                            implements onCircleButtonClickListener, SettingsInterface {
    private LayoutInflater inflater;             // для загрузки разметки элемента
    private int layout;                          // идентфикатор файла разметки
    private ArrayList<Record> records;           // коллекция выводимых данных

    TextView txtDoctorName, txtDateTime, txtCause;

    public static String language;
    public static String textSize;
    Context ctx;

    // создаем поле объекта-колбэка
    private static onCircleButtonClickListener cbListener;

    public RecordsAdapter(Context context, int res, ArrayList<Record> records){
        super(context, res, records);

        this.inflater = LayoutInflater.from(context);
        this.layout = res;
        this.records = records;
        ctx = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view = inflater.inflate(this.layout, parent, false);

        //Ссылки элементов
        txtDoctorName = view.findViewById(R.id.txtDoctor);
        txtDateTime = view.findViewById(R.id.txtDateTime);
        txtCause = view.findViewById(R.id.txtCause);
        CircleButton cbReject = view.findViewById(R.id.cbReject);

        language = PersistantStorageUtils.getLanguagePreferences(ctx);

        //ссылка на объект записей
        final Record item = records.get(position);

        cbReject.setOnClickListener(v -> {
            if(language.equals("ru")) {
                Snackbar snackbar = Snackbar.make(v, R.string.are_u_sure_ru, Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.setAction("Ok", vl -> {
                    cbListener.onCircleButtonClick(v, position);
                    snackbar.dismiss();
                });
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(v, R.string.are_u_sure_en, Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.setAction("Ok", vl -> {
                    cbListener.onCircleButtonClick(v, position);
                    snackbar.dismiss();
                });
                snackbar.show();
            }
        });

        txtDoctorName.setText(item.getDoctorName());
        txtDateTime.setText(item.getDateTime().toString());
        txtCause.setText(item.getAnnotation());

        //Получаем актуальный размер шрифта
        textSize = PersistantStorageUtils.getTextSizePreferences(ctx);
        setTextSize();

        //ссылка на готовый элемент
        return view;
    }

    // метод-сеттер для привязки колбэка к получателю событий
    public void setOnCircleButtonClickListener(onCircleButtonClickListener listener) {
        cbListener = listener;
    }

    @Override
    public void onCircleButtonClick(View view, int position) {

    }

    @Override
    public void setRussianLocale() {

    }

    @Override
    public void setEnglishLocale() {

    }

    @Override
    public void setTextSize() {
        txtDoctorName.setTextSize(Integer.parseInt(textSize));
        txtDateTime.setTextSize(Integer.parseInt(textSize));
        txtCause.setTextSize(Integer.parseInt(textSize));
    }
}
