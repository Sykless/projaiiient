package com.autoscroll.fraba.defiloche;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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

    String FileName;
    boolean fileChoosen = false;

    //settings wich change regardind the screen orientation
    int parcouriButtonHeight = 100;
    int extremeMargin = 16;
    int validateHeight;
    int validateWidth;


    float policeSize = 16;
    float buttonPoliceSize;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_song_param_layout);

        // Toolbar icons setup
        FrameLayout homeLayout = findViewById(R.id.homeLayout);
        FrameLayout backLayout = findViewById(R.id.backLayout);

        homeLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToHome();
            }
        });
        backLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        // Layout modification according to orientation of the device
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size); // size.x = device width - size.y = device height

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) // Portait orientation
        {
            buttonPoliceSize = convertToFloat(size.x,0.02);
            policeSize = convertToFloat(size.x,0.02);
            parcouriButtonHeight = (int)(size.x*0.1);
            extremeMargin = (int) (size.x * 0.022);
            validateHeight = (int) (size.x * 0.1);
            validateWidth = (int) (size.x * 0.18);
        }
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) // Landscape orientation
        {
            buttonPoliceSize = convertToFloat(size.y,0.02);
            policeSize = convertToFloat(size.y,0.02);
            parcouriButtonHeight = (int)(size.y*0.1);
            extremeMargin = (int) (size.y * 0.022);
            validateHeight = (int) (size.y * 0.1);
            validateWidth = (int) (size.y * 0.18);
        }

        // Set the button at 25% "parcourir" line
        Button ParcourirButton = (Button) findViewById(R.id.ParcourirButton);
        ParcourirButton.setWidth((int)(size.x * 0.25));
        ParcourirButton.setTextSize(buttonPoliceSize);

        //set the size of the "parcourir" line
        RelativeLayout relativeLayoutParcourir = (RelativeLayout) findViewById(R.id.relativeLayoutParcourir);
        relativeLayoutParcourir.getLayoutParams().height = parcouriButtonHeight;
        ConstraintLayout.LayoutParams parcourirLineParam = (ConstraintLayout.LayoutParams)relativeLayoutParcourir.getLayoutParams();
        parcourirLineParam.setMargins(0, extremeMargin, 0 ,0);

        // buttons validate setup
        Button validateButton = (Button) findViewById(R.id.validateButton);
        validateButton.setTextSize(buttonPoliceSize);
        validateButton.setWidth(validateWidth);
        validateButton.setHeight(validateHeight);

        // TextView PDF setup
        TextView audioFileNameView = (TextView) findViewById(R.id.audioFileNameView);
        audioFileNameView.setPadding(30, 0, 0, 0);
        if (!fileChoosen) FileName = "Musique associé à la partition";
        audioFileNameView.setText(FileName);
        audioFileNameView.setTextSize(buttonPoliceSize);
    }

    public void goToHome()
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
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

    public float convertToFloat(int nbA, double percentage)
    {
        double temp = new Double(nbA * percentage);
        float result = (float) temp;
        return result;
    }

    public void validateButton(View view)
    {
        Toast.makeText(getApplicationContext(), "à enregistrer dans le cache", Toast.LENGTH_SHORT).show();
        finish();
    }
}
