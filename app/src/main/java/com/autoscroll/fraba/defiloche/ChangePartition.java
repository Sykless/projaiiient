package com.autoscroll.fraba.defiloche;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ChangePartition extends AppCompatActivity {
    private static final int FROM_CHANGE_PARTITION = 11;
    private static final int RESULT_OK = 1;

    boolean pdfChoosen = false;

    final File DCIMDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    final File DCIMParentDir = DCIMDir.getParentFile();
    //TODO changer le nom du dossier ici ET DANS PARCOURIR
    final File userDir = new File(DCIMParentDir.getAbsolutePath() + "/DepuisAndroid");

    String PDFName;
    String resultFromParcourir = null;

    //settings wich change regardind the screen orientation
    int parcouriButtonHeight = 100;
    int paramButtonHeight;
    int paramButtonWidth;
    int extremeMargin = 16;
    int titreLayoutHeight = 200;
    int titreLayoutWidth = 200;
    int titreHeight = 60;
    int titreMargin = 15;
    int EDHeight = 40;
    int titreEDHeight = 40;
    int validateHeight;
    int validateWidth;
    int EDMargin;


    float policeSize = 16;
    float buttonPoliceSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_partition_layout);

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
            paramButtonHeight = (int)(size.x * 0.07);
            paramButtonWidth = (int)(size.x * 0.5);
            extremeMargin = (int) (size.x * 0.022);
            titreLayoutHeight = (int) (size.x * 0.38);
            titreLayoutWidth = (int) (size.x * 0.6);
            titreHeight = (int) (size.x * 0.08);
            titreMargin = (int) (size.x * 0.017);
            EDHeight = titreHeight; //EDHeight = (int) (size.y* 0.093);
            EDMargin = (int) (size.x * 0.012); //titreEDHeight = (int) (size.y* 0.1);
            titreEDHeight = titreHeight;
            validateHeight = (int) (size.x * 0.1);
            validateWidth = (int) (size.x * 0.18);

        }
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) // Landscape orientation
        {
            buttonPoliceSize = convertToFloat(size.y,0.02);
            policeSize = convertToFloat(size.y,0.02);
            parcouriButtonHeight = (int)(size.y*0.1);
            paramButtonHeight = (int)(size.y * 0.07);
            paramButtonWidth = (int)(size.y * 0.5);
            extremeMargin = (int) (size.y * 0.022);
            titreLayoutHeight = (int) (size.y * 0.38);
            titreLayoutWidth = (int) (size.y * 0.6);
            titreHeight = (int) (size.y * 0.08);
            titreMargin = (int) (size.y * 0.017);
            EDHeight = titreHeight ; //EDHeight = (int) (size.y* 0.093);
            EDMargin = (int) (size.x * 0.012);
            titreEDHeight = titreHeight; //titreEDHeight = (int) (size.y* 0.1);
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

        // buttons param setup
        Button paramButton = (Button) findViewById(R.id.paramButton);
        paramButton.setWidth(paramButtonWidth);
        paramButton.setHeight(paramButtonHeight);
        paramButton.setTextSize(buttonPoliceSize);

        // buttons validate setup
        Button validateButton = (Button) findViewById(R.id.validateButton);
        validateButton.setTextSize(buttonPoliceSize);
        validateButton.setWidth(validateWidth);
        validateButton.setHeight(validateHeight);

        // TextView PDF setup
        TextView partitionNameView = (TextView) findViewById(R.id.partitionNameView);
        partitionNameView.setPadding(30, 0, 0, 0);
        if (!pdfChoosen) PDFName = "Partition format PDF";
        partitionNameView.setText(PDFName);
        partitionNameView.setTextSize(buttonPoliceSize);

        // EditText artiste setup
        EditText artisteED = (EditText) findViewById(R.id.artisteED);
        artisteED.setTextSize(policeSize);
        //artisteED.getLayoutParams().height = EDHeight;
        //artisteED.getLayoutParams().width = EDHeight;

        // EditText titre setup
        EditText titreED = (EditText) findViewById(R.id.titreED);
        titreED.setTextSize(policeSize);
        //titreED.getLayoutParams().height = titreEDHeight;
        //titreED.getLayoutParams().width = EDHeight;

        // TextView artiste setup
        TextView artisteTV = (TextView) findViewById(R.id.artisteTV);
        artisteTV.setTextSize(buttonPoliceSize);
        artisteTV.getLayoutParams().height = titreHeight;

        // Set TextView Titre position
        TextView titreTV = (TextView) findViewById(R.id.titreTV);
        titreTV.setTextSize(buttonPoliceSize);
        RelativeLayout.LayoutParams titreTVParam = (RelativeLayout.LayoutParams)titreTV.getLayoutParams();
        //titreTVParam.setMargins(0, EDHeight/3, 0 ,0);
        //titreTVParam.setMargins(0, titreMargin, 0 ,0);
        titreTV.getLayoutParams().height = titreHeight;
        //titreTV.getLayoutParams().width = (int) (size.x*0.13);

        // Set "titre" and "artiste" RelativeLayout size
        RelativeLayout titreLayout = (RelativeLayout) findViewById(R.id.titreLayout);
        //titreLayout.getLayoutParams().height = 2 * (EDHeight + EDMargin);
        //titreLayout.getLayoutParams().height = 600;
        titreLayout.getLayoutParams().width = titreLayoutWidth;
    }

    public void goToHome()
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public float convertToFloat(int nbA, double percentage)
    {
        double temp = new Double(nbA * percentage);
        float result = (float) temp;
        return result;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FROM_CHANGE_PARTITION)
        {
            if (resultCode == RESULT_OK)
            {
                pdfChoosen = true;
                //Use Data to get string
                resultFromParcourir = data.getStringExtra("RESULT_STRING");
                PDFName = resultFromParcourir.substring(resultFromParcourir.lastIndexOf("/") + 1);
                TextView partitionNameView = (TextView) findViewById(R.id.partitionNameView);
                partitionNameView.setText(PDFName);
                partitionNameView.setTextColor(Color.BLACK);
            }
        }
    }

    public void goToParcourir(View view)
    {
        Intent intent = new Intent(this, Parcourir.class);
        intent.putExtra("PREVIOUS_ACTIVITY", "CHANGE_PARTITION");
        startActivityForResult(intent, FROM_CHANGE_PARTITION);
    }

    public void goToParam(View view)
    {
        Intent intent = new Intent(this, ChangeSongParam.class);
        startActivity(intent);
    }

    public void validateButton(View view)
    {
        PartitionActivity app = (PartitionActivity) getApplicationContext();
        ArrayList<Partition> list = app.getPartitionList();

        if (list == null)
        {
            list = new ArrayList<>();
        }

        list.add(new Partition("Axel Bauer","eteins la lumiere",0,"Axel Bauer - eteins la lumiere.pdf"));
        list.add(new Partition("Bob Dylan","Knockin’ on Heavens Door",0,"Bob Dylan – Knockin’ on Heavens Door.pdf"));
        list.add(new Partition("Eric Clapton","COCAINE",0,"COCAINE - Eric Clapton menu.pdf"));
        list.add(new Partition("The Rolling Stones","Honky Tonk Woman",0,"Honky Tonk Woman - The Rolling Stones.pdf"));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(list);

        editor.putString("partitionList", json);
        editor.apply();
        app.savePartitionList(list);

        if(resultFromParcourir != null) {
            File targetedFile = new File(resultFromParcourir);
            try {copy(targetedFile);}
            catch (IOException e) {Log.e("AlertBox OnItemclick", e.getMessage());}
            Toast.makeText(getApplicationContext(), "copy OK !", Toast.LENGTH_SHORT).show();
        }
        // else Toast.makeText(getApplicationContext(), "Vous n'avez sélectionné aucun fichier", Toast.LENGTH_SHORT).show();
    }

    //This code comes from StackOverflow : copying a file
    public void copy(File src) throws IOException {
        File dst = new File(userDir.getPath() + "/" + PDFName);
        dst.createNewFile();

        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
}