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
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class InsidePlaylist extends AppCompatActivity
{
    AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F); // Fading animation on button when clicked
    AlphaAnimation buttonClickRelease = new AlphaAnimation(0.8F, 1F); // Unfading animation on button when clicked

    boolean emptyLayout = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_playlist);

        // Toolbar icons setup
        FrameLayout homeLayout = findViewById(R.id.homeLayout);
        FrameLayout backLayout = findViewById(R.id.backLayout);
        FrameLayout deleteLayout = findViewById(R.id.deleteLayout);
        FrameLayout createLayout = findViewById(R.id.createLayout);
        FrameLayout swapLayout = findViewById(R.id.swapLayout);

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
        swapLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteMemory();
            }
        });
        createLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteMemory();
            }
        });

        buttonClick.setDuration(100);
        buttonClickRelease.setDuration(100);
        buttonClickRelease.setStartOffset(100);

        refreshLayout();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (emptyLayout)
        {
            refreshLayout();
        }
    }

    public void refreshLayout()
    {
        // Layout modification according to orientation of the device
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size); // size.x = device width - size.y = device height

        // Default values
        int marginSide = 0;
        int buttonHeight = 200;
        float textSize = 24;
        int marginTop = 0;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) // Portait orientation
        {
            marginTop = size.x / 24;
            marginSide = size.x / 35;
            buttonHeight = size.x / 12;
            textSize = size.x / 20;
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) // Landscape orientation
        {
            marginTop = size.x / 45;
            marginSide = size.x / 45;
            buttonHeight = size.x / 20;
            textSize = size.x / 28;
        }

        View.OnClickListener buttonEffect = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                v.startAnimation(buttonClickRelease);
            }
        };

        View.OnLongClickListener buttonSwap = new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                System.out.println("Now !");

                return true;
            }
        };

        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        PartitionActivity app = (PartitionActivity) getApplicationContext();
        ArrayList<Playlist> playlistList = app.getPlaylistList();

        int playlistNumber = getIntent().getIntExtra("playlistNumber",0);
        Playlist currentPlaylist = playlistList.get(playlistNumber);

        for (int i = 0 ; i < currentPlaylist.getPartitionList().size() ; i++)
        {
            // Create a new RelativeLayout
            RelativeLayout newButton = new RelativeLayout(this);
            newButton.setId(i);

            // Defining the RelativeLayout layout parameters
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

            int marginBottom = 0;

            if (i == currentPlaylist.getPartitionList().size() - 1)
            {
                marginBottom = marginTop;
            }

            buttonParams.setMargins(marginSide, marginTop, marginSide, marginBottom);
            buttonParams.height = buttonHeight;

            // Creating a new TextView
            TextView songName = new TextView(this);
            Partition currentPartition = currentPlaylist.getPartitionList().get(i);

            String namePartition;

            if (currentPartition.getArtist().length() > 0 && currentPartition.getTitle().length() > 0)
            {
                namePartition = currentPartition.getArtist() + " - " + currentPartition.getTitle();
            }
            else
            {
                namePartition = currentPartition.getFile().getName();
            }

            String toDisplay = String.valueOf(i + 1) + "  " + namePartition;

            songName.setText(toDisplay);
            songName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            songName.setTextColor(Color.WHITE);
            songName.setLines(1);
            /*
            songName.setHorizontallyScrolling(true);
            songName.setMarqueeRepeatLimit(-1);
            songName.setSelected(true);
            */

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
            newButton.setOnLongClickListener(buttonSwap);

            // Add the RelativeLayout to the main LinearLayout
            linearLayout.addView(newButton, i);
        }

        emptyLayout = false;
    }

    public void goToHome()
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void deleteMemory()
    {
        Toast.makeText(this, "Work in progress...", Toast.LENGTH_SHORT).show();

        /*
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove("partitionList");
        editor.remove("playlistList");
        editor.apply();

        PartitionActivity app = (PartitionActivity) getApplicationContext();
        app.savePartitionList(null);
        app.savePlaylistList(null);

        finish();
        */
    }
}

