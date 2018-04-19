package com.autoscroll.fraba.defiloche;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectSong extends AppCompatActivity
{
    private static final int ARTIST = 0;
    private static final int TITLE = 1;

    View selectedBackground;
    boolean setup = false;
    float backgroundSize = 0;

    int displayChoice = TITLE;
    int origin = 0;

    AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F); // Fading animation on button when clicked
    AlphaAnimation buttonClickRelease = new AlphaAnimation(0.8F, 1F); // Unfading animation on button when clicked

    RelativeLayout textTitle;
    RelativeLayout textArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song);

        FrameLayout homeLayout = findViewById(R.id.homeLayout);
        FrameLayout backLayout = findViewById(R.id.backLayout);
        FrameLayout deleteLayout = findViewById(R.id.deleteLayout);
        FrameLayout createLayout = findViewById(R.id.createLayout);

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
        deleteLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteMemory();
            }
        });

        textTitle = findViewById(R.id.textTitleLayout);
        textArtist = findViewById(R.id.textArtistLayout);

        textArtist.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!setup)
                {
                    backgroundSize = textArtist.getWidth();

                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textTitle.getLayoutParams();
                    layoutParams.width = Math.round(backgroundSize);
                    textTitle.setLayoutParams(layoutParams);

                    setup = true;
                }
            }
        });


        textTitle.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && displayChoice == ARTIST)
                {
                    textTitle.setBackgroundColor(getResources().getColor(R.color.darkcyan));
                    textArtist.setBackgroundColor(getResources().getColor(R.color.cyan));

                    displayChoice = TITLE;
                }

                return false;
            }
        });

        textArtist.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN && displayChoice == TITLE)
                {
                    textTitle.setBackgroundColor(getResources().getColor(R.color.cyan));
                    textArtist.setBackgroundColor(getResources().getColor(R.color.darkcyan));

                    displayChoice = ARTIST;
                }

                return false;
            }
        });

        // Layout modification according to orientation of the device
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size); // size.x = device width - size.y = device height

        // Default values
        int marginTop = 0;
        int marginSide = 0;
        int buttonHeight = 200;
        float textSize = 24;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) // Portait orientation
        {
            marginTop = size.x / 24;
            marginSide = size.x / 20;
            buttonHeight = size.x / 12;
            textSize = size.x / 20;
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) // Landscape orientation
        {
            marginTop = size.x / 45;
            marginSide = size.x / 35;
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

                goToPlay(v.getId());
            }
        };

        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        PartitionActivity app = (PartitionActivity) getApplicationContext();
        ArrayList<Partition> partitionList = app.getPartitionList();

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
                marginBottom = marginTop;
            }

            buttonParams.setMargins(marginSide, marginTop, marginSide, marginBottom);
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

    public void deleteMemory()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove("arrayList");
        editor.apply();

        PartitionActivity app = (PartitionActivity) getApplicationContext();
        app.savePartitionList(null);
    }

    public void goToHome()
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void goToPlay(int songNumber)
    {
        Intent intent = new Intent(this, Play.class);
        intent.putExtra("songNumber", songNumber);
        startActivity(intent);
    }
}
