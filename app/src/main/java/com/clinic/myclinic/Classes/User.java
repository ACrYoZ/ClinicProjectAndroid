package com.clinic.myclinic.Classes;

import android.support.annotation.Nullable;

public class User {
    private String userEmail;
    private String userPassword;
    private String userAge;
    private String userAdress;
    private String userPhoto;
    private String userName;
    private String userSurname;
    private String userPatronymic;
   @Nullable private String userDiagnosis;
   @Nullable private String userMedication;

    //Constructors
    public User(String userEmail, String userPassword, String userPhoto, String userName,
                String userSurname, String userPatronymic, String userAge,String userAdress,
                String userDiagnosis, String userMedication) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.userSurname = userSurname;
        this.userPatronymic = userPatronymic;
        this.userDiagnosis = userDiagnosis;
        this.userMedication = userMedication;
        this.userAge = userAge;
        this.userAdress = userAdress;
    }

    public User() {

    }

    //Getters and Setters
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserPatronymic() {
        return userPatronymic;
    }

    public void setUserPatronymic(String userPatronymic) {
        this.userPatronymic = userPatronymic;
    }

    public String getUserDiagnosis() {
        return userDiagnosis;
    }

    public void setUserDiagnosis(String userDiagnosis) {
        this.userDiagnosis = userDiagnosis;
    }

    public String getUserMedication() {
        return userMedication;
    }

    public void setUserMedication(String userMedication) {
        this.userMedication = userMedication;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserAdress() {
        return userAdress;
    }

    public void setUserAdress(String userAdress) {
        this.userAdress = userAdress;
    }

    //Методы для получения данных о пользователе с сервера. TODO: реализовать получение данных о пользователе с сервера.
    private String getUserPhotoSrv(){ return null; }
    private String getUserNameSrv(){ return null; }
    private String getUserSurnameSrv(){ return null; }
    private String getUserPatronymicSrv(){ return null; }
    private String getUserDiagnosisSrv(){ return null; }
    private String getUserMedicationSrv(){ return null; }

}
