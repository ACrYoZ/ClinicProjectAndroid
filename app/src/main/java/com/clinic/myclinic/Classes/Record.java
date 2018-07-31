package com.clinic.myclinic.Classes;

public class Record {
    String doctorName, annotation;     //Имя доктора и причина записи
    String dateTime;                   //Дата и время записи
    private int id;                            //id записи в бд

    public Record(String doctorName, String annotation, String dateTime, int id) {
        this.doctorName = doctorName;
        this.annotation = annotation;
        this.dateTime = dateTime;
        this.id = id;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getId() { return id; }
}
