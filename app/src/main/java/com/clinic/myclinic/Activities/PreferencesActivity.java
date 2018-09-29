package com.clinic.myclinic.Activities;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;

import com.clinic.myclinic.Interfaces.SettingsInterface;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.PersistantStorageUtils;

public class PreferencesActivity extends PreferenceActivity
                                 implements SettingsInterface{

    public  String language;
    PreferenceCategory pGeneralCat, pExtraCat;
    ListPreference pLanguage, pText;
    SwitchPreference pMapMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        PreferencesActivity pa = new PreferencesActivity();

        pLanguage = (ListPreference) findPreference("PREF_CHANGE_LANGUAGE");
        pText = (ListPreference) findPreference("PREF_CHANGE_TXT_SIZE");
        pGeneralCat = (PreferenceCategory) findPreference("GeneralCategoryKey");
        pExtraCat = (PreferenceCategory) findPreference("ExtraCategoryKey");
        pMapMode = (SwitchPreference) findPreference("PREF_CHANGE_MAP_MODE");

        //Получаем актуальный язык
        language = PersistantStorageUtils.getLanguagePreferences(this);

        switch (language){
            case "ru":
                setRussianLocale();
                break;
            case "en":
                setEnglishLocale();
                break;
        }
    }

    @Override
    public void setRussianLocale() {
        pLanguage.setTitle(R.string.pref_title_change_language_ru);
        pLanguage.setSummary(R.string.pref_summary_change_language_ru);

        pText.setTitle(R.string.pref_title_text_size_ru);
        pText.setSummary(R.string.pref_summary_text_size_ru);

        pMapMode.setTitle(R.string.pref_title_map_mode_ru);

        pGeneralCat.setTitle(R.string.pref_category_general_ru);
        pExtraCat.setTitle(R.string.pref_category_extra_ru);
    }

    @Override
    public void setEnglishLocale() {
        pLanguage.setTitle(R.string.pref_title_change_language_en);
        pLanguage.setSummary(R.string.pref_summary_change_language_en);

        pText.setTitle(R.string.pref_title_text_size_en);
        pText.setSummary(R.string.pref_summary_text_size_en);

        pMapMode.setTitle(R.string.pref_title_map_mode_en);

        pGeneralCat.setTitle(R.string.pref_category_general_en);

        pExtraCat.setTitle(R.string.pref_category_extra_en);
    }

    @Override
    public void setTextSize() {}
}
