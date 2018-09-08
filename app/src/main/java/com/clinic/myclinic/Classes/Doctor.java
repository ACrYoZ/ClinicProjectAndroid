package com.clinic.myclinic.Classes;

import android.os.AsyncTask;
import android.util.Log;

import com.clinic.myclinic.Utils.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Doctor implements Serializable {
    private int id;                 //Идентификатор врача в бд
    String name;                    //Имя Отчество Фамилия
    String position;                //Должность(специализация(врач окулист/хиругр/лор))
    String phone;                   //Номер телефона врача
    String photoURL;                //Ссылка на фотографию врача
    double rating;                  //Рейтинг врача
    String parlor;                  //Кабинет врача
    //*** ПРИМЕЧАНИЕ ПО ПОВОДУ ГРАФИКОВ РАБОТЫ ***
    //График указывается в таком формате: с какого дня и  времени врач ведет прием
    //                                    по какой день и время врач ведет прием
    //При том время - начало и окончание прием на каждый день
    //Выглядит это так: from = 2018-08-01 08:00:00;
    //                  to = 2018-08-03 17:00:00;
    String from;                    //График дежурств (поле "с")
    String to;                      //График дежурств (поле "до")

    public Doctor(int id, String name, String position, String phone, String photoURL, String from, String to, double rating, String parlor) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.phone = phone;
        this.photoURL = photoURL;
        this.from = from;
        this.to = to;
        this.parlor = parlor;
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getParlor() {
        return parlor;
    }

    public void setParlor(String parlor) {
        this.parlor = parlor;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public int getId() {
        return id;
    }

     public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
