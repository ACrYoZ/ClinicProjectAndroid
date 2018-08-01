package com.clinic.myclinic.Classes;

import android.os.AsyncTask;
import android.util.Log;

import com.clinic.myclinic.Utils.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Doctor {
    String name;                    //Имя Отчество Фамилия
    String position;                //Должность(специализация(врач окулист/хиругр/лор))
    //*** ПРИМЕЧАНИЕ ПО ПОВОДУ ГРАФИКОВ РАБОТЫ ***
    //График указывается в таком формате: с какого дня и  времени врач ведет прием
    //                                    по какой день и время врач ведет прием
    //При том время - начало и окончание прием на каждый день
    //Выглядит это так: from = 2018-08-01 08:00:00;
    //                  to = 2018-08-03 17:00:00;
    String from;                    //График дежурств (поле "с")
    String to;                      //График дежурств (поле "до")
    private int id;                 //Идентификатор врача в бд

    public Doctor(String name, String position, String from, String to, int id) {
        this.name = name;
        this.position = position;
        this.from = from;
        this.to = to;
        this.id = id;
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
