package com.clinic.myclinic.Classes;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.clinic.myclinic.Activities.UserProfileActivity;
import com.clinic.myclinic.Interfaces.onRecordsDataReceived;
import com.clinic.myclinic.Interfaces.onUserDataReceived;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Records implements onRecordsDataReceived {

    JSONParser jsonParser = new JSONParser();
    Context context;

    //адрес
    private static String url_get_records = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/get_patient_records.php";
    private static String url_remove_record = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/remove_record.php";

    //теги узлов JSON
    private static final String TAG_LOGIN = "login";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RID = "rid";
    private static final String TAG_UID = "uid";
    private static final String TAG_COUNT_DISAGREE = "disagree";
    private static final String TAG_RECORDS = "records";
    private static final String TAG_NAME = "name";
    private static final String TAG_PATRONYMIC = "patronymic";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_ANNOTATION = "annotation";
    private static final String TAG_DATE = "date";
    private static final String TAG_MESSAGE = "message";
    private static final int MAX_DISAGREES = 15;

    @Nullable ArrayList<Record> records = null;
    Record recordToRemove = null;

    int countDisagree;
    int uid;

    // создаем поле объекта-колбэка
    private static onRecordsDataReceived dataReceived;

    //Constructor
    public Records(Context ctx) {
        records = new ArrayList<Record>();
        context = ctx;
        new GetRecordsTask().execute();
    }

    @Nullable
    public ArrayList<Record> getRecords() { return records; }

    // AsyncTask для получения записей
    private class GetRecordsTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            // проверяем тег success
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_LOGIN, AuthorizationUtils.getEmail(context)));
                params.add(new BasicNameValuePair(TAG_PASSWORD, AuthorizationUtils.getPassword(context)));

                // получаем информацию через запрос HTTP GET
                JSONObject jsonRecords = jsonParser.makeHttpRequest(url_get_records, "GET", params);

                // ответ от json о записях
                Log.d("User Records Json", jsonRecords.toString());

                // json success tag
                success = jsonRecords.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // если получили записи
                    JSONArray recordsObj = jsonRecords.getJSONArray(TAG_RECORDS);

                    for (int i = 0; i < recordsObj.length(); i++) {
                        // получим первый объект из массива JSON Array и установим необходимые поля
                        JSONObject user = recordsObj.getJSONObject(i);

                        //получаем данные из массива и записываем их в отдельную переменную
                        String name = user.getString(TAG_NAME);
                        String patronymic = user.getString(TAG_PATRONYMIC);
                        String surname = user.getString(TAG_SURNAME);
                        String annotation = user.getString(TAG_ANNOTATION);
                        String date = user.getString(TAG_DATE);
                        int id = user.getInt(TAG_RID);

                        records.add(new Record(name + " " + patronymic + " " + surname, annotation, date, id));
                    }
                    dataReceived.onRecordsDataReceivedUpdateComponents();
                } else {}
            } catch (JSONException e) { e.printStackTrace(); }
            return null;
        }
    }

    public boolean removeRecordAt(int index, int uid, int countDisagree, String language, View v){

        this.countDisagree = countDisagree;
        this.uid = uid;

        if(countDisagree <= MAX_DISAGREES) {
            //Удаляем с сервера
            recordToRemove = records.get(index);
            new RemoveRecord().execute();
            //Удаляем из списска дабы не обращаться повторно к серверу за обновленными данными
            records.remove(index);
            return true;
        } else {
            if(language.equals("ru")) {
                Snackbar snackbar = Snackbar.make(v, R.string.cannot_remove_record_ru, Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.setAction("Ok", vl -> {
                    snackbar.dismiss();
                });
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(v, R.string.cannot_remove_record_en, Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.setAction("Ok", vl -> {
                    snackbar.dismiss();
                });
                snackbar.show();
            }
            return false;
        }
    }//removeRecordAt

    // AsyncTask для получения записей
    private class RemoveRecord extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            // проверяем тег success
            int success;
            try {
                //переводим id в строку
                StringBuffer sb = new StringBuffer();
                sb.append(recordToRemove.getId());

                //Увеличиваем счетчик отмененных записей и добавляем в параметры запроса
                countDisagree += 1;

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_RID, sb.toString()));
                params.add(new BasicNameValuePair(TAG_UID, Integer.toString(uid)));
                params.add(new BasicNameValuePair(TAG_COUNT_DISAGREE, Integer.toString(countDisagree)));

                // получаем информацию через запрос HTTP GET
                JSONObject jsonRecords = jsonParser.makeHttpRequest(url_remove_record, "POST", params);

                String message = jsonRecords.getString(TAG_MESSAGE);
                // ответ от json о записях
                Log.d("Remove Records JSON", message);

                // json success tag
                success = jsonRecords.getInt(TAG_SUCCESS);
                if (success == 1) {
                        Log.i("SERVER: ", "Your record successfully removed");
                    } else { Log.e("SERVER: ", "Something went wrong...");}
            } catch (JSONException e) { e.printStackTrace(); }
            return null;
        }
    }

    @Override
    public void onRecordsDataReceivedUpdateComponents() {

    }

    public void setOnRecordsDataReceived(onRecordsDataReceived dataReceived){
        this.dataReceived = dataReceived;
    }

}
