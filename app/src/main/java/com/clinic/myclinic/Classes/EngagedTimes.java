package com.clinic.myclinic.Classes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clinic.myclinic.Activities.UserProfileActivity;
import com.clinic.myclinic.Utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EngagedTimes {

    private static String url_get_journal = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/get_journal.php";

    private static final String TAG_JOURNAL = "journal";
    private static final String TAG_DATE = "date";
    private static final String TAG_DID = "docid";
    private static final String TAG_SUCCESS = "success";

    Context context;
    JSONParser jsonParser = new JSONParser();
    Doctors doctors;
    int position;

    ArrayList<EngagedTime> times;

    public EngagedTimes(Context ctx, int doctor_pos) {
        position = doctor_pos;
        this.doctors = new Doctors(ctx);
        context = ctx;
        times = new ArrayList<EngagedTime>();
        new GetJournalTask().execute();
    }


    // AsyncTask для получения данных о врачах
    private class GetJournalTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            // проверяем тег success
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_DID, Integer.toString(doctors.doctors.get(position).getId())));

                // получаем информацию через запрос HTTP GET
                JSONObject jsonJournal = jsonParser.makeHttpRequest(url_get_journal, "GET", params);

                // ответ от json о записях
                Log.d("Journal arr Json", jsonJournal.toString());

                // json success tag
                success = jsonJournal.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // если получили записи
                    JSONArray journalObj = jsonJournal.getJSONArray(TAG_JOURNAL);

                    for (int i = 0; i < journalObj.length(); i++) {
                        // получим первый объект из массива JSON Array и установим необходимые поля
                        JSONObject rec = journalObj.getJSONObject(i);

                        int id = rec.getInt(TAG_DID);
                        String datetime = rec.getString(TAG_DATE);

                        times.add(new EngagedTime(id, datetime));
                        Log.d("EngagedTimes: ", times.toString());
                    }
                } else {}
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
