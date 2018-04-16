package com.autoscroll.fraba.defiloche;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class SelectSong extends AppCompatActivity
{
    View selectedBackground;
    boolean setup = false;
    float backgroundStart = 0;
    float backgroundSize = 0;

    int displayChoice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song);

        Toolbar toolbar = findViewById(R.id.toolbar);
        FrameLayout homeLayout = findViewById(R.id.homeLayout);
        homeLayout.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                goToHome();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        RelativeLayout textTitle = findViewById(R.id.textTitleLayout);
        RelativeLayout textArtist = findViewById(R.id.textArtistLayout);

        selectedBackground = findViewById(R.id.selectedBackground);
        selectedBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                if (!setup)
                {
                    backgroundStart = selectedBackground.getX();
                    backgroundSize = selectedBackground.getWidth();
                    setup = true;
                }
            }
        });

        textTitle.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN && displayChoice == 1)
                {
                    selectedBackground.setX(backgroundStart);
                    displayChoice = 0;
                }

                return false;
            }
        });

        textArtist.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN && displayChoice == 0)
                {
                    selectedBackground.setX(backgroundStart + backgroundSize);
                    displayChoice = 1;
                }

                return false;
            }
        });
    }

    public void goToHome()
    {
        Intent intent = new Intent(this,Home.class);
        startActivity(intent);
    }


}
