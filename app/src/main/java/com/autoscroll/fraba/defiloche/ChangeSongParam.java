package com.autoscroll.fraba.defiloche;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ChangeSongParam extends AppCompatActivity
{
    //TODO durée estimé de la musique + lien youtube de la musique
    private static final int FROM_CHANGE_SONG_PARAM = 12;
    private static final int RESULT_OK = 1;

    String audioFileName;

    boolean fileChoosen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_song_param_layout);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void goToParcourir(View view)
    {
        Intent intent = new Intent(this, Parcourir.class);
        intent.putExtra("PREVIOUS_ACTIVITY", "CHANGE_SONG_PARAM");
        startActivityForResult(intent, FROM_CHANGE_SONG_PARAM);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Log.e("changeSongParam","resultCode = " + resultCode + " requestCode = " + requestCode);
        if (resultCode == RESULT_OK)
        {
            //Use Data to get string
            String audioFilePath = data.getStringExtra("RESULT_STRING");
            audioFileName = audioFilePath.substring(audioFilePath.lastIndexOf("/") + 1);
            TextView audioFileNameView = (TextView) findViewById(R.id.audioFileNameView);
            audioFileNameView.setText(audioFileName);
            audioFileNameView.setTextColor(Color.BLACK);
            Log.e("changeSongParam","audio file name : " + audioFileName);
        }
    }

    public void validateButton(View view)
    {
        Toast.makeText(getApplicationContext(), "à enregistrer dans le cache", Toast.LENGTH_SHORT).show();
        finish();
    }
}
