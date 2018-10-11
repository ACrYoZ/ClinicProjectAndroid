package com.clinic.myclinic.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clinic.myclinic.Classes.Diagnosis;
import com.clinic.myclinic.Classes.Record;
import com.clinic.myclinic.Interfaces.SettingsInterface;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.PersistantStorageUtils;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

public class DiagnosesAdapter extends ArrayAdapter<Diagnosis> implements SettingsInterface {

    private LayoutInflater inflater;             // для загрузки разметки элемента
    private int layout;                          // идентфикатор файла разметки
    private ArrayList<Diagnosis> diagnoses;      // коллекция выводимых данных


    TextView txtDate, txtDiagnosis;

    public static String textSize;
    Context ctx;

    String language;

    public DiagnosesAdapter(Context context, int res, ArrayList<Diagnosis> diagnoses) {
        super(context, res, diagnoses);

        this.inflater = LayoutInflater.from(context);
        this.layout = res;
        this.diagnoses = diagnoses;
        ctx = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view = inflater.inflate(this.layout, parent, false);

        //Ссылки элементов
        txtDate = view.findViewById(R.id.txtDate);
        txtDiagnosis = view.findViewById(R.id.txtDiagn);

        language = PersistantStorageUtils.getLanguagePreferences(ctx);

        //ссылка на объект диагнозов
        final Diagnosis item = diagnoses.get(position);


        txtDiagnosis.setText(item.getDiagnosis());


        if(language.equals("en")) {
            txtDate.setText(item.getDate());
        } else {
            String date = item.getDate();

            String year = date.substring(0, 4);
            String month = date.substring(4, 7);
            String day = date.substring(8,10);

            txtDate.setText(day + month + "-" + year);
        }

        //Получаем актуальный размер шрифта
        textSize = PersistantStorageUtils.getTextSizePreferences(ctx);
        setTextSize();

        //ссылка на готовый элемент
        return view;
    }

    @Override
    public void setRussianLocale() {

    }

    @Override
    public void setEnglishLocale() {

    }

    @Override
    public void setTextSize() {
        txtDiagnosis.setTextSize(Integer.parseInt(textSize));
        txtDate.setTextSize(Integer.parseInt(textSize));
    }
}
