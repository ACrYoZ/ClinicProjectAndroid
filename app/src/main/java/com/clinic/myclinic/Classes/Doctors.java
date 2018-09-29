package com.clinic.myclinic.Classes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clinic.myclinic.Activities.UserProfileActivity;
import com.clinic.myclinic.Interfaces.onCategoriesDataReceived;
import com.clinic.myclinic.Interfaces.onDoctorsDataReceived;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.JSONParser;
import com.clinic.myclinic.Utils.PersistantStorageUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Doctors implements onDoctorsDataReceived, onCategoriesDataReceived {
    JSONParser jsonParser = new JSONParser();
    Context context;

    //адрес
    private static String url_get_doctors = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/get_doctors.php";
    private static String url_get_doctors_expanded = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/get_doctors_expanded.php";
    private static String url_get_categories = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/get_categories.php";

    //теги узлов JSON
    private static final String TAG_LOGIN = "login";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DOCTORS = "doctors";
    private static final String TAG_DID = "did";
    private static final String TAG_NAME = "name";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_PHOTO_URL = "photo";
    private static final String TAG_PATRONYMIC = "patronymic";
    private static final String TAG_RATING = "rating";
    private static final String TAG_PARLOR = "parlor";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_POSITION = "position_name";
    private static final String TAG_DUTY_FROM = "from";
    private static final String TAG_DUTY_TO = "to";
    private static final String TAG_CATEG_NAME = "categname";
    private static final String TAG_CATEGORIES = "categories";
    private static final String TAG_MESSAGE = "message";

    ArrayList<Doctor> doctors = null;
    ArrayList<String> categories = null;
    ArrayList<String> names = null;
    int doc_position;

    //поля callback
    private static onCategoriesDataReceived categoriesDataReceived;
    private static onDoctorsDataReceived doctorsDataReceived;

    public Doctors(Context ctx) {
        context = ctx;
        doctors = new ArrayList<Doctor>();
        new GetDoctorsTask().execute();
        initCategories();
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
                        String phone = doctor.getString(TAG_PHONE);
                        String photo = doctor.getString(TAG_PHOTO_URL);
                        String pos = doctor.getString(TAG_POSITION);
                        String parlor = doctor.getString(TAG_PARLOR);
                        double rating = doctor.getDouble(TAG_RATING);
                        String duty_from = doctor.getString(TAG_DUTY_FROM);
                        String duty_to = doctor.getString(TAG_DUTY_TO);

                        doctors.add(new Doctor(id,  name + " " + patronymic + " " + surname, pos, phone, photo, duty_from, duty_to, rating, parlor));
                    }
                } else {
                }
                doctorsDataReceived.onDoctorsDataReceivedUpdateComponents();
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
                categoriesDataReceived.onCategoriesDataReceivedUpdateComponents();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }

    public void initCategories() {
        categories = new ArrayList<String>();
        new GetCategoriesTask().execute();
    }

    public String[] getCategories() {
        String[] arr = new String[categories.size()];
        return categories.toArray(arr);
    }

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

    public Doctor getDoctorByID(int id){
     for(Doctor doc : doctors){
         if(doc.getId() == id) { return doc; }
     }
     return null;
    }

    public Doctor getDoctorByPhone(String phone){
        for(Doctor doc : doctors){
            if(doc.getPhone().equals(phone)) {return doc;}
        }
        return null;
    }

    public String getDoctorIDByName(String name){
     int id = 0;

        for (Doctor doc : doctors) {
            if(doc.getName().equals(name)){ id = doc.getId(); }
        }
        return Integer.toString(id);
    }

    public String[] getTimeDuty(String doc_name, int doc_postition) {
        this.doc_position = doc_postition;

        Doctor doc;
        ArrayList<String> newTimeDutyList = new ArrayList<String>();
        String[] newtimes = new String[newTimeDutyList.size()];

        for (Doctor f : doctors) {
            if (f.getName().equals(doc_name)) {
                doc = f;

                String from, to;
                from = doc.getFrom();
                to = doc.getTo();

                int fromi, toi;

                int tmp;
                tmp = Integer.parseInt(from.substring(11, 13));
                if(tmp == 0) { fromi = Integer.parseInt(from.substring(12, 13)); } else { fromi = tmp; }

                tmp = Integer.parseInt(to.substring(11, 13));
                if(tmp == 0) { toi = Integer.parseInt(from.substring(12, 13)); } else { toi = tmp; }

                StringBuilder sb;

                for (int i = toi-1; i >= fromi; i--) {
                        sb = new StringBuilder();
                        if(i < 10){
                            sb.append("0" + i + ":" + "00" + ":" + "00");
                        } else {
                            sb.append(i + ":" + "00" + ":" + "00");
                        }

                        newTimeDutyList.add(sb.toString());
                }
                newtimes = new String[newTimeDutyList.size()];
            }
        }
        return newTimeDutyList.toArray(newtimes);
    }

    public String[] getDateDuty(String doc_name, int doc_postition, Context ctx) {
        this.doc_position = doc_postition;

        Doctor doc;
        ArrayList<String> newDateDutyList = new ArrayList<String>();
        String[] newdates = new String[newDateDutyList.size()];

        String language = PersistantStorageUtils.getLanguagePreferences(ctx);

        for (Doctor f : doctors) {
            if (f.getName().equals(doc_name)) {
                doc = f;

                String from, to;
                from = doc.getFrom();
                to = doc.getTo();

                int fromi, toi;

                int tmp;

                if (language.equals("en")) {
                    tmp = Integer.parseInt(from.substring(8, 10));
                    if (tmp == 0) {
                        fromi = Integer.parseInt(from.substring(9, 10));
                    } else {
                        fromi = tmp;
                    }

                    tmp = Integer.parseInt(to.substring(8, 10));
                    if (tmp == 0) {
                        toi = Integer.parseInt(from.substring(9, 10));
                    } else {
                        toi = tmp;
                    }

                    StringBuilder sb;
                    String template = from.substring(0, 8);

                    for (int i = toi; i >= fromi; i--) {
                        sb = new StringBuilder();
                        if (i < 10) {
                            sb.append(template + "0" + i);
                        } else {
                            sb.append(template + i);
                        }
                        newDateDutyList.add(sb.toString());
                    }
                    newdates = new String[newDateDutyList.size()];
                } else {
                    tmp = Integer.parseInt(from.substring(8, 10));
                    if (tmp == 0) {
                        fromi = Integer.parseInt(from.substring(9, 10));
                    } else {
                        fromi = tmp;
                    }

                    tmp = Integer.parseInt(to.substring(8, 10));
                    if (tmp == 0) {
                        toi = Integer.parseInt(from.substring(9, 10));
                    } else {
                        toi = tmp;
                    }

                    //2018-
                    StringBuilder sb;
                    String year = from.substring(0, 4);
                    String month = from.substring(4, 7);

                    for (int i = toi; i >= fromi; i--) {
                        sb = new StringBuilder();
                        if (i < 10) {
                            sb.append("0" + i + month + "-" + year);
                        } else {
                            sb.append(i + month + "-" + year);
                        }
                        newDateDutyList.add(sb.toString());
                    }
                    newdates = new String[newDateDutyList.size()];
                }
            }
        }
        return newDateDutyList.toArray(newdates);
    }

    @Override
    public void onDoctorsDataReceivedUpdateComponents() {

    }

    @Override
    public void onCategoriesDataReceivedUpdateComponents() {

    }

    public void setOnDoctorsDataReceived(onDoctorsDataReceived received){
        doctorsDataReceived = received;
    }

    public void setOnCategoriesDataReceived(onCategoriesDataReceived received){
        categoriesDataReceived = received;
    }
}

