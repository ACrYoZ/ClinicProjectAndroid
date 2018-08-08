package com.clinic.myclinic.Interfaces;

//Интерфейс для установки языков, так как у нас пока присутствует только русская и английская локализация
public interface SettingsInterface {
    void setRussianLocale();
    void setEnglishLocale();

    void setTextSize();
}
