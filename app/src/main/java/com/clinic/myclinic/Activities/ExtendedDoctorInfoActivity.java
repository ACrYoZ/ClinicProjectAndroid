package com.clinic.myclinic.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clinic.myclinic.Classes.Doctor;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.AuthorizationUtils;
import com.clinic.myclinic.Utils.CircularTransformation;
import com.clinic.myclinic.Utils.JSONParser;
import com.clinic.myclinic.Utils.PersistantStorageUtils;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ExtendedDoctorInfoActivity extends AppCompatActivity {

    private static String url_send_rating = "http://" + UserProfileActivity.SERVER + "/AndroidScripts/send_doctor_rating.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RATING = "rating";
    private static final String TAG_DOCTOR_ID = "docid";
    private static final String TAG_RESPONCE = "respon";

    JSONParser jsonParser = new JSONParser();

    String language, textSize;

    TextView txtDocName, txtDocCategory, txtParlorText, txtParlor, txtPhoneText,
             txtPhone, txtRatingText, txtSetYRating;

    RatingBar rtIndicator, rtRating;

    ImageView imgPhoto;

    double cur_rating;

    Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extended_doctor_info);

        //Получаем актуальный язык
        language = PersistantStorageUtils.getLanguagePreferences(this);

        //Важно!                   есть txtDoctorFullName и то, что указал я. В данном случае - второй вариант
        txtDocName = findViewById(R.id.txtDoctFName);
        txtDocCategory = findViewById(R.id.txtCategoryDoc);
        txtParlorText = findViewById(R.id.txtParlorText);
        txtParlor = findViewById(R.id.txtParlor);
        txtPhoneText = findViewById(R.id.txtPhoneText);
        txtPhone = findViewById(R.id.txtPhone);
        txtRatingText = findViewById(R.id.txtRatingText);
        txtSetYRating = findViewById(R.id.txtSetYourOwnRating);
        rtIndicator = findViewById(R.id.rtRatingIndicator);
        rtRating = findViewById(R.id.rtSetRating);
        imgPhoto = findViewById(R.id.imgDoctorPhoto);

        Intent intent = getIntent();
        doctor = (Doctor)intent.getSerializableExtra(AboutDoctorActivity.ID_DOCTORS_OBJ);

        txtDocName.setText(doctor.getName());
        txtDocCategory.setText(doctor.getPosition());
        txtParlor.setText(doctor.getParlor());
        txtPhone.setText("+38" + doctor.getPhone());

        rtIndicator.setIsIndicator(true);
        rtIndicator.setRating((float)doctor.getRating());

        rtRating.setRating(0);
        rtRating.setStepSize(1);
        rtRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                cur_rating = rating;
                new SendDoctorRating().execute();
            }
        });


        Picasso.get()
                .load(doctor.getPhotoURL())
                .resize(300, 300)
                .centerCrop()
                .transform(new CircularTransformation())
                .into(imgPhoto);

        //Получаем актуальный размер шрифта
        textSize = PersistantStorageUtils.getTextSizePreferences(this);
        setTextSize();

        //Устанавливаем актуальный язык
        switch (language){
            case "ru":
                setRussianLocale();
                break;
            case "en":
                setEnglishLocale();
                break;
        }
    }

    private void setEnglishLocale() {
        txtSetYRating.setText(R.string.set_rating_en);
        txtParlorText.setText(R.string.parlor_en);
        txtPhoneText.setText(R.string.phone_en);
        txtRatingText.setText(R.string.rating_en);
    }

    private void setRussianLocale() {
        txtSetYRating.setText(R.string.set_rating_ru);
        txtParlorText.setText(R.string.parlor_ru);
        txtPhoneText.setText(R.string.phone_ru);
        txtRatingText.setText(R.string.rating_ru);
    }

    private void setTextSize() {
        txtSetYRating.setTextSize(Integer.parseInt(textSize));
        txtParlorText.setTextSize(Integer.parseInt(textSize));
        txtPhoneText.setTextSize(Integer.parseInt(textSize));
        txtRatingText.setTextSize(Integer.parseInt(textSize));
        txtPhone.setTextSize(Integer.parseInt(textSize));
        txtParlor.setTextSize(Integer.parseInt(textSize));
        txtDocCategory.setTextSize(Integer.parseInt(textSize));
        txtDocName.setTextSize(Integer.parseInt(textSize));
    }

    // AsyncTask для получения данных о врачах
    private class SendDoctorRating extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            // проверяем тег success
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_RATING, Double.toString(cur_rating)));
                params.add(new BasicNameValuePair(TAG_DOCTOR_ID, Double.toString(doctor.getId())));

                // постим информацию через запрос HTTP POST
                JSONObject jsonResponce = jsonParser.makeHttpRequest(url_send_rating, "GET", params);

                // ответ от json о записях
                Log.d("RatingResponce", jsonResponce.toString());

                // json success tag
                success = jsonResponce.getInt(TAG_SUCCESS);
                if (success == 1) {
                   Log.d("RatingSuccess:", "true");
                } else {
                    Log.d("RatingSuccess:", "false");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
