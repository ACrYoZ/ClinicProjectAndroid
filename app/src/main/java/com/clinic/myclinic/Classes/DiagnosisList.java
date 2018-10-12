package com.clinic.myclinic.Classes;

import android.os.AsyncTask;
import android.util.Log;

import com.clinic.myclinic.Activities.UserProfileActivity;
import com.clinic.myclinic.Interfaces.onDiagnosesDataReceived;
import com.clinic.myclinic.Utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DiagnosisList implements onDiagnosesDataReceived {
    JSONParser jsonParser = new JSONParser();

    private static String url_get_diagnosis = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/get_diagnosis.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DIAGN = "diagn";
    private static final String TAG_DIAGNOS = "diagnos";
    private static final String TAG_DATE = "date";
    private static final String TAG_RESPONCE = "diagnosis";
    private static final String TAG_UID = "uid";

    private static onDiagnosesDataReceived diagnosesDataReceived;

    int user_id;

    ArrayList<Diagnosis> diagnoses = new ArrayList<>();

    public DiagnosisList(int user_id) {
        this.user_id = user_id;

        new GetDiagnosisTask().execute();
    }

    public ArrayList<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    @Override
    public void onDiagnosesDataReceivedUpdateComponents() {

    }

    public void setOnDiagnosesDataReceivedComponents(onDiagnosesDataReceived received){
        diagnosesDataReceived = received;
    }

    public void sort(){
        Collections.sort(diagnoses, new Comparator<Diagnosis>() {
            public int compare(Diagnosis o1, Diagnosis o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
    }

    // AsyncTask для получения данных о диагнозах
    private class GetDiagnosisTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            // проверяем тег success
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair(TAG_UID, Integer.toString(user_id)));

                // получаем информацию через запрос HTTP GET
                JSONObject jsonCategorie = jsonParser.makeHttpRequest(url_get_diagnosis, "GET", params);

                // ответ от json о записях
                Log.d("Diagnoses arr Json", jsonCategorie.toString());

                // json success tag
                success = jsonCategorie.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // если получили записи
                    JSONArray diagnosisObj = jsonCategorie.getJSONArray(TAG_RESPONCE);

                    for (int i = 0; i < diagnosisObj.length(); i++) {
                        // получим первый объект из массива JSON Array и установим необходимые поля
                        JSONObject diagn = diagnosisObj.getJSONObject(i);

                        //получаем данные из массива и записываем их в отдельную переменную
                        String diagnosis = diagn.getString(TAG_DIAGNOS);
                        String date = diagn.getString(TAG_DATE);

                        diagnoses.add(new Diagnosis(diagnosis, date));

                    }
                } else {}
                diagnosesDataReceived.onDiagnosesDataReceivedUpdateComponents();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
