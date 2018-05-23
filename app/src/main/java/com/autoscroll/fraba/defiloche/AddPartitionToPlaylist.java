package com.autoscroll.fraba.defiloche;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AddPartitionToPlaylist extends AppCompatActivity
{
    private static final int ARTIST = 0;
    private static final int TITLE = 1;
    private static final int INSIDEPLAYLIST = 2;

    boolean setup = false;
    boolean destroyPlaylist = true;
    float backgroundSize = 0;

    int displayChoice = TITLE;
    int partitionSize;
    int partitionsSelected = 0;
    int playlistNumber = -1;
    String playlistName;

    // Default values
    int marginTop = 0;
    int marginSide = 0;
    int buttonHeight = 200;
    float textSize = 24;

    AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F); // Fading animation on button when clicked
    AlphaAnimation buttonClickRelease = new AlphaAnimation(0.8F, 1F); // Unfading animation on button when clicked

    RelativeLayout textTitle;
    RelativeLayout textArtist;
    FrameLayout validateLayout;
    FrameLayout backLayout;

    LinearLayout layoutSource;
    LinearLayout layoutToAdd;

    ArrayList<Partition> partitionList;
    ArrayList<Integer> playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_partition_to_playlist);

        backLayout = findViewById(R.id.backLayout);
        validateLayout = findViewById(R.id.validateLayout);

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


        textTitle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (displayChoice == ARTIST)
                {
                    textTitle.setBackgroundColor(getResources().getColor(R.color.darkgreen));
                    textArtist.setBackgroundColor(getResources().getColor(R.color.green));

                    displayChoice = TITLE;

                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    PartitionActivity app = (PartitionActivity) getApplicationContext();

                    editor.putInt("artisteTitreParam",TITLE);
                    editor.apply();
                    app.setArtisteTitreParam(TITLE);

                    sortBy(TITLE);
                    refreshLayout();
                }
            }
        });

        textArtist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (displayChoice == TITLE)
                {
                    textTitle.setBackgroundColor(getResources().getColor(R.color.green));
                    textArtist.setBackgroundColor(getResources().getColor(R.color.darkgreen));

                    displayChoice = ARTIST;

                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    PartitionActivity app = (PartitionActivity) getApplicationContext();

                    editor.putInt("artisteTitreParam",ARTIST);
                    editor.apply();
                    app.setArtisteTitreParam(ARTIST);

                    sortBy(ARTIST);
                    refreshLayout();
                }
            }
        });

        refreshLayout();
    }

    public void refreshLayout()
    {
        // Layout modification according to orientation of the device
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size); // size.x = device width - size.y = device height

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

        layoutSource = findViewById(R.id.layoutSource);
        layoutToAdd = findViewById(R.id.layoutToAdd);

        layoutSource.removeAllViews();

        if (destroyPlaylist)
        {
            layoutToAdd.removeAllViews();
            playlist = new ArrayList<>();
        }

        PartitionActivity app = (PartitionActivity) getApplicationContext();
        partitionList = app.getPartitionList();
        partitionSize = partitionList.size();
        displayChoice = app.getArtisteTitreParam();

        if (displayChoice == TITLE)
        {
            textTitle.setBackgroundColor(getResources().getColor(R.color.darkgreen));
            textArtist.setBackgroundColor(getResources().getColor(R.color.green));

            sortBy(TITLE);
        }
        else
        {
            textTitle.setBackgroundColor(getResources().getColor(R.color.green));
            textArtist.setBackgroundColor(getResources().getColor(R.color.darkgreen));

            sortBy(ARTIST);
        }

        for (int i = 0 ; i < partitionSize ; i++)
        {
            // Create a new RelativeLayout
            RelativeLayout newButton = new RelativeLayout(this);
            newButton.setId(i);

            // Defining the RelativeLayout layout parameters
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

            int marginBottom = 0;

            if (i == partitionSize - 1)
            {
                marginBottom = marginTop;
            }

            buttonParams.setMargins(marginSide, marginTop, marginSide, marginBottom);
            buttonParams.height = buttonHeight;

            // Creating a new TextView
            TextView songName = new TextView(this);
            String toDisplay;

            if (partitionList.get(i).getArtist().length() > 0 && partitionList.get(i).getTitle().length() > 0)
            {
                if (displayChoice == ARTIST)
                {
                    toDisplay = partitionList.get(i).getArtist() + " - " + partitionList.get(i).getTitle();
                }
                else
                {
                    toDisplay = partitionList.get(i).getTitle() + " - " + partitionList.get(i).getArtist();
                }
            }
            else
            {
                toDisplay = partitionList.get(i).getFile().getName();
            }

            songName.setText(toDisplay);
            songName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            songName.setTextColor(Color.WHITE);
            songName.setLines(1);
            songName.setHorizontallyScrolling(true);
            songName.setMarqueeRepeatLimit(-1);
            songName.setSelected(true);

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
            newButton.setOnClickListener(sourceClick);

            // Add the RelativeLayout to the main LinearLayout
            layoutSource.addView(newButton, i);
        }

        if (getIntent().getIntExtra("displayMode", 0) == INSIDEPLAYLIST && destroyPlaylist)
        {
            playlistNumber = getIntent().getIntExtra("playlistNumber", 0);
            partitionsSelected = 0;

            app = (PartitionActivity) getApplicationContext();
            ArrayList<Playlist> playlistList = app.getPlaylistList();
            ArrayList<Partition> partitionList = app.getPartitionList();

            Playlist currentPlaylist = playlistList.get(playlistNumber);
            playlistName = currentPlaylist.getName();

            for (int i = 0 ; i < currentPlaylist.getPartitionList().size() ; i++)
            {
                addToPlaylist(currentPlaylist.getPartitionList().get(i), i);
                partitionsSelected++;

                for (int j = 0 ; j < partitionList.size() ; j++)
                {
                    String title = currentPlaylist.getPartitionList().get(i).getTitle();
                    String artist = currentPlaylist.getPartitionList().get(i).getArtist();
                    String fileName = currentPlaylist.getPartitionList().get(i).getFile().getName();
                    int speed = currentPlaylist.getPartitionList().get(i).getSpeed();

                    if (partitionList.get(j).getTitle().equals(title)
                            && partitionList.get(j).getArtist().equals(artist)
                            && partitionList.get(j).getFile().getName().equals(fileName)
                            && partitionList.get(j).getSpeed() == speed)
                    {
                        playlist.add(j); // Found the correct song
                        break;
                    }
                }
            }
        }

        destroyPlaylist = false;
    }

    void addToPlaylist(Partition partitionToAdd, int numberPartition)
    {
        // Create a new RelativeLayout
        LinearLayout numberButton = new LinearLayout(this);
        RelativeLayout newButton = new RelativeLayout(this);
        RelativeLayout newNumber = new RelativeLayout(this);
        numberButton.setId(numberPartition);

        // Defining the RelativeLayout layout parameters.
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams numberParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0);
        LinearLayout.LayoutParams buttonNumberParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

        buttonNumberParams.setMargins(marginSide, marginTop, marginSide, 0);
        numberParams.setMargins(0,0,marginSide,0);
        buttonNumberParams.height = buttonHeight;
        numberParams.height = buttonHeight;
        buttonParams.height = buttonHeight;

        // Creating a new TextView
        TextView songName = new TextView(this);
        String toDisplay;

        if (partitionToAdd.getArtist().length() > 0 && partitionToAdd.getTitle().length() > 0)
        {
            toDisplay = partitionToAdd.getArtist() + " - " + partitionToAdd.getTitle();
        }
        else
        {
            toDisplay = partitionToAdd.getFile().getName();
        }

        songName.setText(toDisplay);
        songName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        songName.setTextColor(Color.WHITE);

        TextView number = new TextView(this);
        number.setText(String.valueOf(numberPartition + 1));
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
        newButton.setBackgroundColor(getResources().getColor(R.color.green));

        newNumber.addView(number,0);
        newNumber.setGravity(Gravity.CENTER);
        newNumber.setLayoutParams(numberParams);
        newNumber.setBackgroundColor(getResources().getColor(R.color.green));

        numberButton.setLayoutParams(buttonNumberParams);
        numberButton.addView(newNumber,0);
        numberButton.addView(newButton,1);
        numberButton.setClickable(true);
        numberButton.setFocusable(true);
        numberButton.setFocusableInTouchMode(false);
        numberButton.setOnClickListener(toAddClick);

        // Add the RelativeLayout to the main LinearLayout
        layoutToAdd.addView(numberButton,numberPartition);
    }

    void createPlaylist()
    {
        PartitionActivity app = (PartitionActivity) getApplicationContext();
        ArrayList<Playlist> playlistList = app.getPlaylistList();
        ArrayList<Partition> partitionList = app.getPartitionList();

        if (playlistList == null)
        {
            playlistList = new ArrayList<>();
        }

        if (playlistNumber == -1)
        {
            playlistName = getIntent().getStringExtra("playlistName");
        }
        else
        {
            playlistList.remove(playlistNumber);
        }

        Playlist playlistToCreate = new Playlist(playlistName);

        for (Integer i : playlist)
        {
            playlistToCreate.addPartition(partitionList.get(i));
        }

        if (playlistNumber == -1)
        {
            playlistList.add(playlistToCreate);
            Toast.makeText(this, "Playlist " + playlistName + " crée avec succès !", Toast.LENGTH_SHORT).show();
        }
        else
        {
            playlistList.add(playlistNumber, playlistToCreate);
            Toast.makeText(this, "Playlist " + playlistName + " modifiée avec succès !", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(playlistList);

        editor.putString("playlistList", json);
        editor.apply();
        app.savePlaylistList(playlistList);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public void sortBy(int sortingChoice)
    {
        ArrayList<Partition> ArtistTitleList = new ArrayList<>();
        ArrayList<Partition> PDFList = new ArrayList<>();

        for (Partition partition : partitionList)
        {
            if (partition.getArtist().length() > 0 && partition.getTitle().length() > 0)
            {
                ArtistTitleList.add(partition);
            }
            else
            {
                PDFList.add(partition);
            }
        }

        Collections.sort(PDFList, new Comparator<Partition>()
        {
            public int compare(Partition one, Partition other)
            {
                return one.getFile().getName().compareToIgnoreCase(other.getFile().getName());
            }
        });

        if (sortingChoice == ARTIST)
        {
            Collections.sort(ArtistTitleList, new Comparator<Partition>()
            {
                public int compare(Partition one, Partition other)
                {
                    return one.getArtist().compareToIgnoreCase(other.getArtist());
                }
            });
        }
        else
        {
            Collections.sort(ArtistTitleList, new Comparator<Partition>()
            {
                public int compare(Partition one, Partition other)
                {
                    return one.getTitle().compareToIgnoreCase(other.getTitle());
                }
            });
        }

        partitionList.clear();

        for (Partition partition : ArtistTitleList)
        {
            partitionList.add(partition);
        }

        for (Partition partition : PDFList)
        {
            partitionList.add(partition);
        }
    }

    View.OnClickListener sourceClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            view.startAnimation(buttonClick);
            view.startAnimation(buttonClickRelease);

            addToPlaylist(partitionList.get(view.getId()), partitionsSelected);

            partitionsSelected++;
            playlist.add(view.getId());

            ((ImageView) validateLayout.getChildAt(0)).setImageResource(R.drawable.ic_done_white_48dp);
            validateLayout.setClickable(true);
        }
    };

    View.OnClickListener toAddClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() != partitionsSelected - 1)
            {
                for (int i = view.getId() ; i < partitionsSelected ; i++)
                {
                    if (i == view.getId())
                    {
                        i++; // Solve a bug
                    }

                    ((TextView)(((RelativeLayout)((LinearLayout) layoutToAdd.getChildAt(i)).getChildAt(0)).getChildAt(0))).setText(String.valueOf(i));
                    layoutToAdd.getChildAt(i).setId(i - 1);
                }
            }

            playlist.remove(view.getId());
            layoutToAdd.removeViewAt(view.getId());

            partitionsSelected--;

            if (partitionsSelected > 0)
            {
                ((ImageView) validateLayout.getChildAt(0)).setImageResource(R.drawable.ic_done_white_48dp);
                validateLayout.setClickable(true);
            }
            else
            {
                ((ImageView) validateLayout.getChildAt(0)).setImageResource(R.drawable.ic_done_grey_48dp);
                validateLayout.setClickable(false);
            }
        }
    };
}
