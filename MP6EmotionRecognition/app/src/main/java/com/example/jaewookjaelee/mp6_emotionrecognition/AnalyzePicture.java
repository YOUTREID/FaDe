package com.example.jaewookjaelee.mp6_emotionrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class AnalyzePicture extends AppCompatActivity{

    public MainActivity mainActivity;
    private ListView listview;
    private String[] values = new String[] { "Gender: " + mainActivity.getGender(),
                                             "Age: " + mainActivity.getAge(),
                                             "Smile: " + mainActivity.getSmile() * 100 + "%",
                                             "Glasses: " + mainActivity.getGlasses(),
                                             "Happiness: " + mainActivity.getHappiness() * 100 + "%",
                                             "Neutral: " + mainActivity.getNeutral() * 100 + "%",
                                             "Sadness: " + mainActivity.getSadness() * 100 + "%",
                                             "Anger: " + mainActivity.getAnger() * 100 + "%",
                                             "Fear: " + mainActivity.getFear() * 100 + "%",
                                             "Make Up: " + mainActivity.isMakeUp(),
                                             "Baldness: " + mainActivity.getBaldness() * 100 + "%",
                                             "Moustache: " + mainActivity.getMoustache() * 100 + "%",
                                             "Beard: " + mainActivity.getBeard() * 100 + "%" };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_details);

        listview = (ListView)findViewById(R.id.ListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.text_view, R.id.textView, values);
        listview.setAdapter(arrayAdapter);

        Button returnButton = (Button) findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
