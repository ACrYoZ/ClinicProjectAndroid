package com.clinic.myclinic.Classes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clinic.myclinic.Activities.UserProfileActivity;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Doctors {
    JSONParser jsonParser = new JSONParser();
    Context context;

    //адрес
    private static String url_get_doctors = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/get_doctors.php";
    private static String url_get_categories = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/get_categories.php";
    private static String url_get_journal = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/get_journal.php";

    //теги узлов JSON
    private static final String TAG_LOGIN = "login";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DOCTORS = "doctors";
    private static final String TAG_DID = "did";
    private static final String TAG_NAME = "name";
    private static final String TAG_PATRONYMIC = "patronymic";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_POSITION = "position_name";
    private static final String TAG_DUTY_FROM = "from";
    private static final String TAG_DUTY_TO = "to";
    private static final String TAG_CATEG_NAME = "categname";
    private static final String TAG_CATEGORIES = "categories";
    private static final String TAG_JOURNAL = "journal";
    private static final String TAG_DOCID = "docid";
    private static final String TAG_DATE = "date";
    private static final String TAG_MESSAGE = "message";

    ArrayList<Doctor> doctors = null;
    ArrayList<String> categories = null;
    ArrayList<String> names = null;
    EngagedTimes timesEngag;
    int doc_position;

    public Doctors(Context ctx) {
        context = ctx;
        doctors = new ArrayList<Doctor>();
        new GetDoctorsTask().execute();
    }

    // AsyncTask для получения данных о врачах
    private class GetDoctorsTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            // проверяем тег success
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_LOGIN, AuthorizationUtils.getEmail(context)));
                params.add(new BasicNameValuePair(TAG_PASSWORD, AuthorizationUtils.getPassword(context)));

                // получаем информацию через запрос HTTP GET
                JSONObject jsonDoctors = jsonParser.makeHttpRequest(url_get_doctors, "GET", params);

                // ответ от json о записях
                Log.d("Doctors arr Json", jsonDoctors.toString());

                // json success tag
                success = jsonDoctors.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // если получили записи
                    JSONArray doctorsObj = jsonDoctors.getJSONArray(TAG_DOCTORS);

                    for (int i = 0; i < doctorsObj.length(); i++) {
                        // получим первый объект из массива JSON Array и установим необходимые поля
                        JSONObject doctor = doctorsObj.getJSONObject(i);

                        //получаем данные из массива и записываем их в отдельную переменную
                        int id = Integer.parseInt(doctor.getString(TAG_DID));
                        String name = doctor.getString(TAG_NAME);
                        String patronymic = doctor.getString(TAG_PATRONYMIC);
                        String surname = doctor.getString(TAG_SURNAME);
                        String pos = doctor.getString(TAG_POSITION);
                        String duty_from = doctor.getString(TAG_DUTY_FROM);
                        String duty_to = doctor.getString(TAG_DUTY_TO);

                        doctors.add(new Doctor(name + " " + patronymic + " " + surname, pos, duty_from, duty_to, id));
                    }
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // AsyncTask для получения данных о категориях
    private class GetCategoriesTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            // проверяем тег success
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                // получаем информацию через запрос HTTP GET
                JSONObject jsonCategorie = jsonParser.makeHttpRequest(url_get_categories, "GET", params);

                // ответ от json о записях
                Log.d("Categories arr Json", jsonCategorie.toString());

                // json success tag
                success = jsonCategorie.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // если получили записи
                    JSONArray categorieObj = jsonCategorie.getJSONArray(TAG_CATEGORIES);

                    for (int i = 0; i < categorieObj.length(); i++) {
                        // получим первый объект из массива JSON Array и установим необходимые поля
                        JSONObject categ = categorieObj.getJSONObject(i);

                        //получаем данные из массива и записываем их в отдельную переменную
                        String categorieName = categ.getString(TAG_CATEG_NAME);

                        categories.add(categorieName);
                    }
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String[] getCategories() {
        categories = new ArrayList<String>();
        new GetCategoriesTask().execute();
        //TODO: исправить
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] arr = new String[categories.size()];
        return categories.toArray(arr);
    }

    //Не работает TODO: Починить
    public String[] getNames(String categorie) {
        names = new ArrayList<String>();
        for (Doctor doctor : doctors) {
            if (doctor.getPosition().equals(categorie)) {
                names.add(doctor.getName());
            }
        }//for-each

        String[] arr = new String[names.size()];
        return names.toArray(arr);
    }



    public String[] getTimeDuty(String doc_name, int doc_postition) {
        this.doc_position = doc_postition;

        Doctor doc;
        ArrayList<String> newTimeDutyList = new ArrayList<String>();
        String[] newtimes = new String[newTimeDutyList.size()];

        for (Doctor f : doctors) {
            if (f.getName() == doc_name) {
                doc = f;

                String from, to;
                from = doc.getFrom();
                to = doc.getTo();

                timesEngag = new EngagedTimes(context, doc_postition);
                //TODO: исправить
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int fromi = Integer.parseInt(from.substring(11, 12));
                String  engagedToi;
                StringBuilder sb;

                for (int toi = Integer.parseInt(to.substring(11, 12)); toi >= fromi; toi--) {
                    for (int i = 0; i < timesEngag.times.size(); i++) {
                        sb = new StringBuilder();
                        sb.append(toi + ":" + "00" + ":" + "00");
                        engagedToi = timesEngag.times.get(i).datetime;

                        if (sb.equals(engagedToi)) { newTimeDutyList.add(sb.toString()); }
                    }
                }
                newtimes = new String[newTimeDutyList.size()];
            }
        }
        return newTimeDutyList.toArray(newtimes);
    }
}

