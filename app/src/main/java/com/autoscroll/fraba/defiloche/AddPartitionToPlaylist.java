package com.autoscroll.fraba.defiloche;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AddPartitionToPlaylist extends AppCompatActivity
{
    private static final int ARTIST = 0;
    private static final int TITLE = 1;

    boolean setup = false;
    float backgroundSize = 0;

    int displayChoice = TITLE;
    int origin = 0;
    int partitionSize;

    int idPartition = 1;

    AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F); // Fading animation on button when clicked
    AlphaAnimation buttonClickRelease = new AlphaAnimation(0.8F, 1F); // Unfading animation on button when clicked

    RelativeLayout textTitle;
    RelativeLayout textArtist;

    ArrayList<Integer> playlist = new ArrayList<>();
    int[] numberPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_partition_to_playlist);

        FrameLayout backLayout = findViewById(R.id.backLayout);
        FrameLayout validateLayout = findViewById(R.id.validateLayout);

        backLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        validateLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createPlaylist();
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
                    textTitle.setBackgroundColor(getResources().getColor(R.color.darkgreen));
                    textArtist.setBackgroundColor(getResources().getColor(R.color.green));

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
                    textTitle.setBackgroundColor(getResources().getColor(R.color.green));
                    textArtist.setBackgroundColor(getResources().getColor(R.color.darkgreen));

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
            marginSide = size.x / 40;
            buttonHeight = size.x / 12;
            textSize = size.x / 20;
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) // Landscape orientation
        {
            marginTop = size.x / 45;
            marginSide = size.x / 65;
            buttonHeight = size.x / 20;
            textSize = size.x / 28;
        }

        buttonClick.setDuration(100);
        buttonClickRelease.setDuration(100);
        buttonClickRelease.setStartOffset(100);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        PartitionActivity app = (PartitionActivity) getApplicationContext();
        ArrayList<Partition> partitionList = app.getPartitionList();
        partitionSize = partitionList.size();

        for (int i = 0 ; i < partitionSize ; i++)
        {
            // Create a new RelativeLayout
            LinearLayout numberButton = new LinearLayout(this);
            RelativeLayout newButton = new RelativeLayout(this);
            RelativeLayout newNumber = new RelativeLayout(this);
            numberButton.setId(i);

            // Defining the RelativeLayout layout parameters.
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            LinearLayout.LayoutParams numberParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0);
            RelativeLayout.LayoutParams buttonNumberParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);

            int marginBottom = 0;

            if (i == partitionList.size() - 1)
            {
                marginBottom = marginTop;
            }

            buttonNumberParams.setMargins(marginSide, marginTop, marginSide, marginBottom);
            numberParams.setMargins(0,0,marginSide,0);
            buttonNumberParams.height = buttonHeight;
            numberParams.height = buttonHeight;
            buttonParams.height = buttonHeight;

            // Creating a new TextView
            TextView songName = new TextView(this);

            String toDisplay;

            if (partitionList.get(i).getArtist().length() > 0 && partitionList.get(i).getTitle().length() > 0)
            {
                toDisplay = partitionList.get(i).getArtist() + " - " + partitionList.get(i).getTitle();
            }
            else
            {
                toDisplay = partitionList.get(i).getFile().getName();
            }

            songName.setText(toDisplay);
            songName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            songName.setTextColor(Color.WHITE);

            TextView number = new TextView(this);
            number.setText("0");
            number.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            number.setTextColor(Color.WHITE);

            // Defining the layout parameters of the TextView
            RelativeLayout.LayoutParams textParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            textParameters.setMarginStart(8);

            RelativeLayout.LayoutParams numberParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            numberParameters.setMarginStart(12);
            numberParameters.setMarginEnd(12);

            // Setting the parameters on the TextView
            songName.setLayoutParams(textParameters);
            number.setLayoutParams(numberParameters);

            // Adding the TextView to the RelativeLayout as a child and make the layout clickable
            newButton.addView(songName,0);
            newButton.setGravity(Gravity.CENTER_VERTICAL);
            newButton.setLayoutParams(buttonParams);
            newButton.setBackgroundColor(getResources().getColor(R.color.cyan));

            newNumber.addView(number,0);
            newNumber.setGravity(Gravity.CENTER);
            newNumber.setLayoutParams(numberParams);
            newNumber.setBackgroundColor(getResources().getColor(R.color.green));
            newNumber.setVisibility(View.INVISIBLE);

            numberButton.setLayoutParams(buttonNumberParams);
            numberButton.addView(newNumber,0);
            numberButton.addView(newButton,1);
            numberButton.setClickable(true);
            numberButton.setFocusable(true);
            numberButton.setFocusableInTouchMode(false);
            numberButton.setOnClickListener(buttonEffect);

            // Add the RelativeLayout to the main LinearLayout
            linearLayout.addView(numberButton, i);
        }

        numberPlaylist = new int[partitionSize];
    }

    View.OnClickListener buttonEffect = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            v.startAnimation(buttonClick);
            v.startAnimation(buttonClickRelease);

            if (playlist.contains(v.getId()))
            {
                playlist.remove(Integer.valueOf(v.getId()));

                ViewParent mainLayout = v.getParent();

                for (int i = 0 ; i < partitionSize ; i++)
                {
                    if (numberPlaylist[i] > 0 && numberPlaylist[i] > numberPlaylist[v.getId()])
                    {
                        numberPlaylist[i]--;

                        View mainRelative = ((ViewGroup) mainLayout).getChildAt(i);
                        View numberLayout = ((ViewGroup) mainRelative).getChildAt(0);

                        View textView = ((ViewGroup) numberLayout).getChildAt(0);
                        ((TextView) textView).setText(String.valueOf(numberPlaylist[i]));
                    }
                }

                numberPlaylist[v.getId()] = -1;
                idPartition--;

                ((ViewGroup)v).getChildAt(0).setVisibility(View.INVISIBLE);
                ((ViewGroup)v).getChildAt(1).setBackgroundColor(getResources().getColor(R.color.cyan));
            }
            else
            {
                playlist.add(v.getId());

                View numberLayout = ((ViewGroup)v).getChildAt(0);
                View buttonLayout = ((ViewGroup)v).getChildAt(1);

                View textView = ((ViewGroup) numberLayout).getChildAt(0);
                ((TextView) textView).setText(String.valueOf(idPartition));

                numberPlaylist[v.getId()] = idPartition;
                idPartition++;

                numberLayout.setVisibility(View.VISIBLE);
                buttonLayout.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }
    };

    void createPlaylist()
    {
        PartitionActivity app = (PartitionActivity) getApplicationContext();
        ArrayList<Playlist> playlistList = app.getPlaylistList();
        ArrayList<Partition> partitionList = app.getPartitionList();

        int playlistNumber = getIntent().getIntExtra("playlistNumber",0);
        Playlist playlistToCreate = playlistList.get(playlistNumber);

        for (int i = 0 ; i < playlist.size() ; i++)
        {
            playlistToCreate.addPartition(partitionList.get(playlist.get(i)));
        }

        playlistList.set(playlistNumber,playlistToCreate);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(playlistList);

        editor.putString("playlistList", json);
        editor.apply();

        Toast.makeText(this, "Playlist " + playlistToCreate.getName() + " crée !", Toast.LENGTH_SHORT).show();
        finish();
    }
}
