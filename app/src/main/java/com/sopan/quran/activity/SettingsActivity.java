package com.sopan.quran.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.sopan.quran.R;
import com.sopan.quran.util.settings.Config;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_container, new SettingsPreferenceFragment()).commit();
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //System.exit(0);
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onBackPressed() {
       // System.exit(0);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               // System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }

    public static String replaceBanglaText(Context context, String text) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = sharedPreferences.getString(Config.LANG, Config.defaultLang);

        if (lang.equals(Config.LANG_BN)) {
            return text.replaceAll("0", "০")
                    .replaceAll("1", "১")
                    .replaceAll("2", "২")
                    .replaceAll("3", "৩")
                    .replaceAll("4", "৪")
                    .replaceAll("5", "৫")
                    .replaceAll("6", "৬")
                    .replaceAll("7", "৭")
                    .replaceAll("8", "৮")
                    .replaceAll("9", "৯");
        }
        return text;
    }

}
