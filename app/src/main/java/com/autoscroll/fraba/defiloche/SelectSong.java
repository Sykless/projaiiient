package com.autoscroll.fraba.defiloche;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectSong extends AppCompatActivity {
    View selectedBackground;
    boolean setup = false;
    float backgroundStart = 0;
    float backgroundSize = 0;

    int displayChoice = 0;

    AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F); // Fading animation on button when clicked
    AlphaAnimation buttonClickRelease = new AlphaAnimation(0.8F, 1F); // Unfading animation on button when clicked

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song);

        Toolbar toolbar = findViewById(R.id.toolbar);
        FrameLayout homeLayout = findViewById(R.id.homeLayout);
        homeLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToHome();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        RelativeLayout textTitle = findViewById(R.id.textTitleLayout);
        RelativeLayout textArtist = findViewById(R.id.textArtistLayout);

        selectedBackground = findViewById(R.id.selectedBackground);
        selectedBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!setup) {
                    backgroundStart = selectedBackground.getX();
                    backgroundSize = selectedBackground.getWidth();
                    setup = true;
                }
            }
        });

        textTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && displayChoice == 1) {
                    selectedBackground.setX(backgroundStart);
                    displayChoice = 0;
                }

                return false;
            }
        });

        textArtist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && displayChoice == 0) {
                    selectedBackground.setX(backgroundStart + backgroundSize);
                    displayChoice = 1;
                }

                return false;
            }
        });

        // Layout modification according to orientation of the device
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size); // size.x = device width - size.y = device height

        // Default values
        int margin = 30;
        int buttonHeight = 200;
        float textSize = 24;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) // Portait orientation
        {
            margin = size.x / 24;
            buttonHeight = size.x / 12;
            textSize = size.x / 20;
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) // Landscape orientation
        {
            margin = size.x / 45;
            buttonHeight = size.x / 20;
            textSize = size.x / 28;
        }

        buttonClick.setDuration(100);
        buttonClickRelease.setDuration(100);
        buttonClickRelease.setStartOffset(100);

        View.OnClickListener buttonEffect = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                v.startAnimation(buttonClickRelease);

                System.out.println("" + v.getId());
            }
        };

        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        PartitionActivity app = (PartitionActivity) getApplicationContext();
        ArrayList<Partition> partitionList = app.getPartitionList();

        // TODO If the ArrayList is empty, show a message (link to create)

        for (int i = 0 ; i < partitionList.size() ; i++)
        {
            // Create a new RelativeLayout
            RelativeLayout newButton = new RelativeLayout(this);
            newButton.setId(i);

            // Defining the RelativeLayout layout parameters.
            RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);

            int marginBottom = 0;

            if (i == partitionList.size() - 1)
            {
                marginBottom = margin;
            }

            buttonParams.setMargins(margin, margin, margin, marginBottom);
            buttonParams.height = buttonHeight;

            // Creating a new TextView
            TextView songName = new TextView(this);
            songName.setText(partitionList.get(i).getArtist() + " - " + partitionList.get(i).getTitle());
            songName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            songName.setTextColor(Color.WHITE);


            // Defining the layout parameters of the TextView
            RelativeLayout.LayoutParams textParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            textParameters.setMarginStart(8);

            // Setting the parameters on the TextView
            songName.setLayoutParams(textParameters);

            // Adding the TextView to the RelativeLayout as a child and make the layout clickable
            newButton.addView(songName);
            newButton.setGravity(Gravity.CENTER_VERTICAL);
            newButton.setLayoutParams(buttonParams);
            newButton.setBackgroundColor(getResources().getColor(R.color.cyan));
            newButton.setClickable(true);
            newButton.setFocusable(true);
            newButton.setFocusableInTouchMode(false);
            newButton.setOnClickListener(buttonEffect);

            // Add the RelativeLayout to the main LinearLayout
            linearLayout.addView(newButton, i);
        }
    }

    public void goToHome() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
