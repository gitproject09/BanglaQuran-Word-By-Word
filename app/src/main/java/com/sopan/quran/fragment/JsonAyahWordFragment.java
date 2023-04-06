package com.sopan.quran.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.sopan.quran.R;
import com.sopan.quran.adapter.JsonAyahWordAdapter;
import com.sopan.quran.database.datasource.AyahWordDataSource;
import com.sopan.quran.database.datasource.SurahDataSource;
import com.sopan.quran.model.AyahWord;
import com.sopan.quran.util.settings.Config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

import static com.sopan.quran.database.datasource.AyahWordDataSource.QURAN_BANGLA;


/**
 * A simple {@link Fragment} subclass.
 */
public class JsonAyahWordFragment extends Fragment {


    long surah_id;
    long ayah_number;
    String lang;
    String suraFileName = "001";
    private ArrayList<AyahWord> ayahWordArrayList;
    private RecyclerView mRecyclerView;
    private JsonAyahWordAdapter ayahWordAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public JsonAyahWordFragment() {
        // Required empty public constructor
    }

    public static JsonAyahWordFragment newInstance(Bundle bundle) {
        JsonAyahWordFragment ayahWordFragment = new JsonAyahWordFragment();
        ayahWordFragment.setArguments(bundle);
        return ayahWordFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        lang = sp.getString(Config.LANG, Config.defaultLang);
        surah_id = getArguments().getLong(SurahDataSource.SURAH_ID_TAG);
        ayah_number = getArguments().getLong(SurahDataSource.SURAH_AYAH_NUMBER);

        // Work here
        //  ayahWordArrayList = getAyahWordsBySurah(surah_id, ayah_number);
        if (surah_id <= 9) {
            suraFileName = "00" + surah_id;
        } else if (surah_id >= 10 && surah_id < 100) {
            suraFileName = "0" + surah_id;
        } else {
            suraFileName = "" + surah_id;
        }
        Log.w(JsonAyahWordFragment.class.getSimpleName(), "Sura File Name : " + suraFileName);

        ayahWordArrayList = getAyahWordsBySurah(surah_id, ayah_number);
        //ayahWordArrayList = getAyahWordsBySurahFromJson(suraFileName, ayah_number);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ayah_word, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_ayah_word_view);

        //for fast scroll
        VerticalRecyclerViewFastScroller fastScroller = view.findViewById(R.id.fast_scroller);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(mRecyclerView);

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        mRecyclerView.setOnScrollListener(fastScroller.getOnScrollListener());

        ayahWordAdapter = new JsonAyahWordAdapter(ayahWordArrayList, getActivity(), surah_id);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        //set Adapter with Animation
        //  ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(ayahWordAdapter);
        //  scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
        mRecyclerView.setAdapter(ayahWordAdapter);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setVerticalScrollBarEnabled(true);


        //set headerview
        RecyclerViewHeader recyclerViewHeader = view.findViewById(R.id.header);
        TextView headerTextView = recyclerViewHeader.findViewById(R.id.headerTextView);
        headerTextView.setText(getString(R.string.bismillah));

        TextView headerTextTranslate = recyclerViewHeader.findViewById(R.id.headerTextTranslate);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        headerTextTranslate.setTextSize(Integer.parseInt(sharedPreferences.getString(Config.FONT_SIZE_TRANSLATION, Config.defaultFontSizeTranslation)));

        recyclerViewHeader.attachTo(mRecyclerView, true);

    }

    public ArrayList<AyahWord> getAyahWordsBySurah(long surah_id, long ayah_number) {
        ArrayList<AyahWord> ayahWordArrayList = new ArrayList<AyahWord>();
        AyahWordDataSource ayahWordDataSource = new AyahWordDataSource(getActivity());
        //ayahWordArrayList = ayahWordDataSource.getEnglishAyahWordsBySurahVerse(surah_id, ayah_number);

        switch (lang) {
            case Config.LANG_BN:
                ayahWordArrayList = ayahWordDataSource.getBanglaAyahWordsBySurah(surah_id, ayah_number);
                break;
            case Config.LANG_INDO:
                ayahWordArrayList = ayahWordDataSource.getIndonesianAyahWordsBySurah(surah_id, ayah_number);
                break;
            case Config.LANG_EN:
                ayahWordArrayList = ayahWordDataSource.getEnglishAyahWordsBySurah(surah_id, ayah_number);
                break;
        }

        return ayahWordArrayList;
    }

    private ArrayList<AyahWord> getAyahWordsBySurahFromJson(String suraNumber, long ayah_number) {

        ArrayList<AyahWord> ayahWordArrayList = new ArrayList<AyahWord>();

        try {

            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(suraNumber));

            String nameEnglish = (String) jsonObject.get("nameEnglish");
            String nameTrans = (String) jsonObject.get("nameTrans");
            String nameArabic = (String) jsonObject.get("nameArabic");
            int chapterNumber = (int) jsonObject.get("chapterNumber");
            int totalVerses = (int) jsonObject.get("nVerses");

            Log.d(JsonAyahWordFragment.class.getSimpleName(), "Json Log : " + nameEnglish + "Chapter : " + chapterNumber);

            JSONArray jsonArray = (JSONArray) jsonObject.get("verses");
            Log.w(JsonAyahWordFragment.class.getSimpleName(), "Json Length : " + jsonArray.length());

            for (int i = 1; i < jsonArray.length(); i++) {
                // long ayatId = jsonArray.getJSONObject(i).getLong("verseNumber");
                AyahWord ayahWord = new AyahWord();
                ayahWord.setQuranVerseId(jsonArray.getJSONObject(i).getLong("verseNumber"));
                ayahWord.setQuranArabic(jsonArray.getJSONObject(i).getString("verseString"));
               // ayahWord.setQuranTranslate(jsonArray.getJSONObject(i).getString("verseStringB") + "\n\n" +jsonArray.getJSONObject(i).getString("verseStringE"));
                ayahWord.setQuranTranslate(jsonArray.getJSONObject(i).getString("verseStringB"));
                ayahWordArrayList.add(ayahWord);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ayahWordArrayList;
    }

    public String loadJSONFromAsset(String suraNumber) {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("quran/" + suraNumber + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}