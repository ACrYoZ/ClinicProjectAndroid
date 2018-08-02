package com.clinic.myclinic.Classes;

public class EngagedTime {
    int id;             //id доктора
    String datetime;    //время и дата существующей записи

    public EngagedTime(int id, String datetime) {
        this.id = id;
        this.datetime = datetime;
    }
}
