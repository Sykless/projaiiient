package com.autoscroll.fraba.defiloche;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
    final File userDir = new File(DCIMParentDir.getAbsolutePath() + "/Défileur de partitions");

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
    int imageViewHeight;
    int imageViewWidth;
    int PersonalRatingBarHeigth;
    int speedSelected = 11;

    float policeSize = 16;
    float buttonPoliceSize;
    float ratingBarWidth;

    MyLinearLayout MyPersonalRatingBar;

    ArrayList<ImageView> listImageView = new ArrayList<>();

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
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //---------------------------------------test---------------------------------------------//

        final File[] appsDir= ContextCompat.getExternalFilesDirs(this,null);
        final ArrayList<File> extRootPaths=new ArrayList<>();
        for(final File file : appsDir) {
            extRootPaths.add(file.getParentFile().getParentFile().getParentFile().getParentFile());
        }

        /*
        for(final File file : extRootPaths)
            System.out.println(file.getPath());
            */

        File [] externalFichiers = extRootPaths.get(1).listFiles();
        /*
        for(final  File file : externalFichiers)
            System.out.println("external dir = " + file.getName());
            */
        /*
        File testExt = new File(extRootPaths.get(1).getPath(), "testExt.txt");
        System.out.println("testExt.getPath() = " + testExt.getPath());
        System.out.println("testExt.mkdir() = " + testExt.mkdirs());

        File [] externalFichiers = extRootPaths.get(1).listFiles();
        for(final  File file : externalFichiers)
            System.out.println("external dir = " + file.getName());

        try //copy and clear the activity
        {
            copy(externalFichiers[2]);
        }
        catch (IOException e)
        {
            Log.e("AlertBox OnItemclick", e.getMessage());
        }
        */
        //---------------------------------------test---------------------------------------------//

        //create a personnal Layout wich extends LinearLayout
        MyPersonalRatingBar = new MyLinearLayout(this);
        MyPersonalRatingBar.setClickable(true);

        // Layout modification according to orientation of the device
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size); // size.x = device width - size.y = device height

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) // Portait orientation
        {
            buttonPoliceSize = convertToFloat(size.x, 0.02);
            policeSize = convertToFloat(size.x, 0.02);
            parcouriButtonHeight = (int) (size.x * 0.1);
            paramButtonHeight = (int) (size.x * 0.07);
            paramButtonWidth = (int) (size.x * 0.5);
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
            imageViewHeight = (int) (size.x/22);
            imageViewWidth = (int) (size.x/22);
            PersonalRatingBarHeigth = (int) ((size.x/21) + 0.05 * size.x);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) // Landscape orientation
        {
            buttonPoliceSize = convertToFloat(size.y, 0.02);
            policeSize = convertToFloat(size.y, 0.02);
            parcouriButtonHeight = (int) (size.y * 0.1);
            paramButtonHeight = (int) (size.y * 0.07);
            paramButtonWidth = (int) (size.y * 0.5);
            extremeMargin = (int) (size.y * 0.022);
            titreLayoutHeight = (int) (size.y * 0.38);
            titreLayoutWidth = (int) (size.y * 0.6);
            titreHeight = (int) (size.y * 0.08);
            titreMargin = (int) (size.y * 0.017);
            EDHeight = titreHeight; //EDHeight = (int) (size.y* 0.093);
            EDMargin = (int) (size.x * 0.012);
            titreEDHeight = titreHeight; //titreEDHeight = (int) (size.y* 0.1);
            validateHeight = (int) (size.y * 0.1);
            validateWidth = (int) (size.y * 0.18);
            imageViewHeight = (int) (size.x/24);
            imageViewWidth = (int) (size.x/24);
            PersonalRatingBarHeigth = (int) ((size.y/22) + 0.05 * size.y);
        }

        // Set the button at 25% "parcourir" line
        Button ParcourirButton = (Button) findViewById(R.id.ParcourirButton);
        ParcourirButton.setWidth((int) (size.x * 0.25));
        ParcourirButton.setTextSize(buttonPoliceSize);

        //set the size of the "parcourir" line
        RelativeLayout relativeLayoutParcourir = (RelativeLayout) findViewById(R.id.relativeLayoutParcourir);
        relativeLayoutParcourir.getLayoutParams().height = parcouriButtonHeight;
        ConstraintLayout.LayoutParams parcourirLineParam = (ConstraintLayout.LayoutParams) relativeLayoutParcourir.getLayoutParams();
        parcourirLineParam.setMargins(0, extremeMargin, 0, 0);

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
        partitionNameView.setText("Partition format PDF");
        partitionNameView.setTextSize(buttonPoliceSize);

        // EditText artiste setup
        EditText artisteED = (EditText) findViewById(R.id.artisteED);
        artisteED.setTextSize(policeSize);

        // EditText titre setup
        EditText titreED = (EditText) findViewById(R.id.titreED);
        titreED.setTextSize(policeSize);

        // TextView artiste setup
        TextView artisteTV = (TextView) findViewById(R.id.artisteTV);
        artisteTV.setTextSize(buttonPoliceSize);
        artisteTV.getLayoutParams().height = titreHeight;

        // Set TextView Titre position
        TextView titreTV = (TextView) findViewById(R.id.titreTV);
        titreTV.setTextSize(buttonPoliceSize);
        titreTV.getLayoutParams().height = titreHeight;

        // Set "titre" and "artiste" RelativeLayout size
        RelativeLayout titreLayout = (RelativeLayout) findViewById(R.id.titreLayout);
        titreLayout.getLayoutParams().width = titreLayoutWidth;

        //define the constraint layout below the toolbar
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);

        //SpeedTV setup
        TextView speedTV = findViewById(R.id.speedTV);
        // + " = " + (speedSelected - 20)*
        int secondesTotal = (21 - speedSelected) * 20;
        int minutes = secondesTotal / 60;
        int secondes = secondesTotal % 60;
        speedTV.setText("Vitesse de défilement = " +  minutes + ":" + secondes + " minutes");
        //speedTV.setText("Vitesse de défilement : " + speedSelected + " - " +  minutes + ":" + secondes + " minutes");
        speedTV.setTextSize(policeSize);

        //Add the PersonalRatingBar to the layout and define it's constraints
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, PersonalRatingBarHeigth);
        MyPersonalRatingBar.setGravity(View.TEXT_ALIGNMENT_CENTER);
        MyPersonalRatingBar.setLayoutParams(params);
        MyPersonalRatingBar.setOrientation(LinearLayout.HORIZONTAL);
        //set the constraints
        constraintLayout.addView(MyPersonalRatingBar);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(MyPersonalRatingBar.getId(),ConstraintSet.TOP,R.id.speedTV,ConstraintSet.BOTTOM,0);
        constraintSet.connect(MyPersonalRatingBar.getId(),ConstraintSet.BOTTOM,R.id.validateButton,ConstraintSet.TOP,5);
        constraintSet.connect(MyPersonalRatingBar.getId(),ConstraintSet.LEFT,R.id.constraintLayout,ConstraintSet.LEFT,5);
        constraintSet.connect(MyPersonalRatingBar.getId(),ConstraintSet.RIGHT,R.id.constraintLayout,ConstraintSet.RIGHT,5);
        constraintSet.applyTo(constraintLayout);
        MyPersonalRatingBar.setVisibility(View.VISIBLE);

        //Personnal ratingBar setup
        for(int i = 0 ; i<= 20 ; i++)
        {
            //ImageView Setup
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.test_24dp); //setting image resource
            imageView.setClickable(true);
            imageView.setId(i);
            int width = imageViewWidth;
            int height = imageViewHeight;
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
            imageView.setLayoutParams(parms);
            imageView.getLayoutParams().height = imageViewHeight;
            imageView.getLayoutParams().width = imageViewWidth;

            //Add the image view to an arrayList
            listImageView.add(imageView);
            MyPersonalRatingBar.addView(imageView,i);
            if(i <= speedSelected) listImageView.get(i).setColorFilter(Color.parseColor("#00B0F0")); //color the circle (init)
        }

        //get the width of the PersonalRatingBar
        final ViewTreeObserver observer = MyPersonalRatingBar.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ratingBarWidth = MyPersonalRatingBar.getWidth();
                    }
                });

        //Set The touchListener of the PersonalRatingBar
        MyPersonalRatingBar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                if (event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    float x = event.getX();
                    float circleWidth = ratingBarWidth / 21;
                    speedSelected = (int) (x / circleWidth);
                    if (speedSelected > 20 ) speedSelected = 20;
                    if (speedSelected < 0 ) speedSelected = 0;
                    //color the circles
                    if (speedSelected == 0)
                    {
                        for (int i = 0; i <= 20; i++)
                        {
                            if(i == 0) listImageView.get(i).setColorFilter(Color.RED);
                            else listImageView.get(i).setColorFilter(Color.parseColor("#000000"));
                        }
                    }
                    else
                    {
                        for (int i = 0; i <= 20; i++)
                        {
                            if(i <= speedSelected) listImageView.get(i).setColorFilter(Color.parseColor("#00B0F0"));
                            else listImageView.get(i).setColorFilter(Color.parseColor("#000000"));
                        }
                    }
                    TextView speedTV = findViewById(R.id.speedTV);
                    int secondesTotal = (21 - speedSelected) * 20;
                    int minutes = secondesTotal / 60;
                    int secondes = secondesTotal % 60;
                    if (speedSelected == 0) speedTV.setText("Vitesse de défilement = " + "à l'arrêt");
                    else if (minutes <= 1) speedTV.setText("Vitesse de défilement = " +  minutes + ":" + secondes + " minute");
                    else if (minutes == 0) speedTV.setText("Vitesse de défilement = 0:" + secondes + " minutes");
                    else if (secondes == 0) speedTV.setText("Vitesse de défilement = " +  minutes + ":" + secondes + "0" +" minutes");
                    else speedTV.setText("Vitesse de défilement = "+  minutes + ":" + secondes + " minutes");
                }
                return false;
            }
        });
    }

    public class MyLinearLayout extends LinearLayout
    {
        public MyLinearLayout(Context context){
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev)
        {
            // do whatever you want with the event
            // and return true so that children don't receive it
            return true;
        }

        @Override
        public boolean performClick() {
            // Calls the super implementation, which generates an AccessibilityEvent
            // and calls the onClick() listener on the view, if any
            super.performClick();
            // Handle the action for the custom click here
            return true;
        }
    }

    public void goToHome() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public float convertToFloat(int nbA, double percentage) {
        double temp = new Double(nbA * percentage);
        float result = (float) temp;
        return result;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FROM_CHANGE_PARTITION)
        {
            if (resultCode == RESULT_OK)
            {
                //Use Data to get string
                pdfChoosen = true;
                resultFromParcourir = data.getStringExtra("RESULT_STRING");
                PDFName = resultFromParcourir.substring(resultFromParcourir.lastIndexOf("/") + 1);
                TextView partitionNameView = (TextView) findViewById(R.id.partitionNameView);
                partitionNameView.setText(PDFName);
                partitionNameView.setTextColor(Color.BLACK);

                //fill the artiste and titre fields
                EditText artisteED = findViewById(R.id.artisteED);
                EditText titreED = findViewById(R.id.titreED);
                if(artisteED.length() == 0 && titreED.length() == 0) setArtisteTitre(PDFName);
            }
        }
    }

    public void goToParcourir(View view) {
        Intent intent = new Intent(this, Parcourir.class);
        intent.putExtra("PREVIOUS_ACTIVITY", "CHANGE_PARTITION");
        startActivityForResult(intent, FROM_CHANGE_PARTITION);
    }

    public void goToParam(View view) {
        Intent intent = new Intent(this, ChangeSongParam.class);
        startActivity(intent);
    }

    public void validateButton(View view) {
        PartitionActivity app = (PartitionActivity) getApplicationContext();
        ArrayList<Partition> list = app.getPartitionList();

        if (list == null) {
            list = new ArrayList<>();
        }
        //create a partition
        EditText artisteED = findViewById(R.id.artisteED);
        EditText titreED = findViewById(R.id.titreED);
        int secondesTotal = (21 - speedSelected) * 20;
        if (speedSelected == 0) secondesTotal=0;
        list.add(new Partition(artisteED.getText().toString(), titreED.getText().toString(), secondesTotal, PDFName));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(list);

        editor.putString("partitionList", json);
        editor.apply();
        app.savePartitionList(list);

        if (resultFromParcourir != null)
        {
            File targetedFile = new File(resultFromParcourir);
            boolean fileInDirectory = false;
            File [] userFiles = userDir.listFiles();

            //check if the file is in the "défileur de partitions" directory
            for(int i = 0; i< userFiles.length ; i++)
            {
                if (targetedFile.getName().equals(userFiles[i].getName()))
                {
                    fileInDirectory = true;
                    //Toast.makeText(getApplicationContext(), "Ce fichier est déjà dans le dossier [Défileur de partitions]", Toast.LENGTH_SHORT).show();
                }
            }
            if(!fileInDirectory)
            {
                try //copy and clear the activity
                {
                    copy(targetedFile);
                    Toast.makeText(getApplicationContext(), "le ficher " + '"' + targetedFile.getName() + '"' + " fait désormais partie de l'application", Toast.LENGTH_SHORT).show();
                    TextView partitionNameView = findViewById(R.id.partitionNameView);
                    partitionNameView.setText("Partition format PDF");
                    artisteED.setText("");
                    titreED.setText("");
                    partitionNameView.setTextColor(Color.parseColor("#808080"));
                }
                catch (IOException e)
                {
                    Log.e("AlertBox OnItemclick", e.getMessage());
                }
            }
            else
            {
                //clear the activity
                Toast.makeText(getApplicationContext(), "le ficher " + '"' + targetedFile.getName() + '"' + " fait désormais partie de l'application", Toast.LENGTH_SHORT).show();
                TextView partitionNameView = findViewById(R.id.partitionNameView);
                partitionNameView.setText("Partition format PDF");
                artisteED.setText("");
                titreED.setText("");
                partitionNameView.setTextColor(Color.parseColor("#808080"));
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Vous n'avez sélectionné aucun fichier", Toast.LENGTH_SHORT).show();
        }

    }

    public void setArtisteTitre(String fileName)
    {
        //take the choice from the settings activity
        PartitionActivity app = (PartitionActivity) getApplicationContext();
        boolean ArtisteTitreChoice = app.getArtisteTitreParam();
        String artiste;
        String titre;

        if(fileName.lastIndexOf("-") == -1)
        {
            if(fileName.lastIndexOf("\u00af") != -1)
            {
                // "\u00af" => "-" longer
                artiste = fileName.substring(0,fileName.lastIndexOf("\u00af"));
                titre = fileName.substring(fileName.lastIndexOf("\u00af") + 1, fileName.lastIndexOf("."));
            }
            else
            {
                artiste = "";
                titre = "";
            }
        }
        else if (fileName.lastIndexOf("-") > 0)
        {
            artiste = fileName.substring(0,fileName.lastIndexOf("-"));
            titre = fileName.substring(fileName.lastIndexOf("-") + 1, fileName.lastIndexOf("."));
        }
        else
        {
            artiste = "";
            titre = "";
        }


        if (!ArtisteTitreChoice)
        {
            String buffer = artiste;
            artiste = titre;
            titre = buffer;
        }
        EditText artisteED = findViewById(R.id.artisteED);
        artisteED.setText(artiste);

        EditText titreED = findViewById(R.id.titreED);
        titreED.setText(titre);
    }

    //This code comes from StackOverflow : copying a file
    public void copy(File src) throws IOException {
        // TODO PRENDRE EN COMPTE LA COPIE DEPUIS UNE CARTE SD
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
