package com.autoscroll.fraba.defiloche;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_partition_layout);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Layout modification according to orientation of the device
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size); // size.x = device width - size.y = device height

        // Set the button at 20% of the screen width
        double buttonWidth = new Double(size.x * 0.2);
        int intButtonWidth = (int) buttonWidth;
        Button ParcourirButton = (Button) findViewById(R.id.ParcourirButton);
        ParcourirButton.setWidth(intButtonWidth);

        // TextView PDF setup
        TextView partitionNameView = (TextView) findViewById(R.id.partitionNameView);
        partitionNameView.setPadding(30, 0, 0, 0);
        if (!pdfChoosen) PDFName = "Partition format PDF";
        partitionNameView.setText(PDFName);

        //Button validateButton = (Button) findViewById(R.id.validateButton);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FROM_CHANGE_PARTITION) {
            if (resultCode == RESULT_OK) {
                pdfChoosen = true;
                //Use Data to get string
                resultFromParcourir = data.getStringExtra("RESULT_STRING");
                PDFName = resultFromParcourir.substring(resultFromParcourir.lastIndexOf("/") + 1);
                Log.e("changePartition", "file name = " + PDFName);
                TextView partitionNameView = (TextView) findViewById(R.id.partitionNameView);
                partitionNameView.setText(PDFName);
                partitionNameView.setTextColor(Color.BLACK);
            }
        }
    }

    public void goToParcourir(View view)
    {
        Intent intent = new Intent(this, Parcourir.class);
        startActivityForResult(intent, FROM_CHANGE_PARTITION);
    }

    public void validateButton(View view)
    {
        if(resultFromParcourir != null) {
            File targetedFile = new File(resultFromParcourir);
            try {copy(targetedFile);}
            catch (IOException e) {Log.e("AlertBox OnItemclick", e.getMessage());}
            Toast.makeText(getApplicationContext(), "copy OK !", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(getApplicationContext(), "Vous n'avez sélectionné aucun fichier", Toast.LENGTH_SHORT).show();
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