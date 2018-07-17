package com.clinic.myclinic.Classes;

import java.util.Date;

public class Record {
    String doctorName, cause;   //Имя доктора и причина записи
    Date dateTime;              //Дата и время записи

    public Record(String doctorName, String cause, Date dateTime) {
        this.doctorName = doctorName;
        this.cause = cause;
        this.dateTime = dateTime;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}
