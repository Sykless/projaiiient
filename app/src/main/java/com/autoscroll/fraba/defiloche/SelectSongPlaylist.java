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
import android.support.v7.widget.Toolbar;
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

import com.google.gson.Gson;

import java.util.ArrayList;

public class SelectSongPlaylist extends AppCompatActivity
{
    AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F); // Fading animation on button when clicked
    AlphaAnimation buttonClickRelease = new AlphaAnimation(0.8F, 1F); // Unfading animation on button when clicked

    private static final int ARTIST = 0;
    private static final int TITLE = 1;
    private static final int PARTITION = 2;
    private static final int PLAYLIST = 3;
    private static final int ANDROIDGUY = 4;
    private static final int LISTLAYOUT = 5;

    int displayChoice = TITLE;
    int arraySize = 0;

    boolean setup = false;
    boolean noPartition = false;
    boolean layoutEmpty = true;
    boolean androidGuyEmpty = true;
    boolean deleteMode = false;

    float backgroundSize = 0;
    float textSize = 24;
    int marginTop = 0;
    int menuValue;

    PartitionActivity app;
    ArrayList<Playlist> playlistList;
    ArrayList<Partition> partitionList;
    ArrayList<Integer> toDelete;

    Point size;

    Toolbar toolbar;

    RelativeLayout textTitle;
    RelativeLayout textArtist;
    ConstraintLayout mainLayout;
    LinearLayout linearLayout;

    FrameLayout homeLayout;
    FrameLayout backLayout;
    FrameLayout deleteLayout;
    FrameLayout createLayout;

    ImageView deleteButton;
    ImageView createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_create);

        // Layout modification according to orientation of the device
        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size); // size.x = device width - size.y = device height

        // Toolbar icons setup
        homeLayout = findViewById(R.id.homeLayout);
        backLayout = findViewById(R.id.backLayout);
        deleteLayout = findViewById(R.id.deleteLayout);
        createLayout = findViewById(R.id.createLayout);

        deleteButton = findViewById(R.id.deleteButton);
        createButton = findViewById(R.id.createButton);

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
        createLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToCreate();
            }
        });

        textTitle = findViewById(R.id.textTitleLayout);
        textArtist = findViewById(R.id.textArtistLayout);
        mainLayout = findViewById(R.id.mainLayout);

        linearLayout = findViewById(R.id.linearLayout);
        toolbar = findViewById(R.id.toolbar);

        buttonClick.setDuration(100);
        buttonClickRelease.setDuration(100);
        buttonClickRelease.setStartOffset(100);

        app = (PartitionActivity) getApplicationContext();
        playlistList = app.getPlaylistList();
        partitionList = app.getPartitionList();
        menuValue = getIntent().getIntExtra("menuValue", PARTITION);

        if (partitionList != null)
        {
            System.out.println("Nombre de partitions au début : " + partitionList.size());
        }
        else
        {
            System.out.println("null !");
        }

        noPartition = partitionList == null || partitionList.size() == 0;

        if (noPartition)
        {
            menuValue = PARTITION;
        }

        if (menuValue == PLAYLIST)
        {
            noPartition = playlistList == null || playlistList.size() == 0;
        }

        if (noPartition)
        {
            androidGuyLayout();
        }
        else
        {
            listLayout();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (layoutEmpty && refreshLayout() == LISTLAYOUT)
        {
            linearLayout.removeAllViews();
            listLayout();
        }

        if (androidGuyEmpty && refreshLayout() == ANDROIDGUY)
        {
            androidGuyLayout();
        }
    }

    public int refreshLayout()
    {
        app = (PartitionActivity) getApplicationContext();
        partitionList = app.getPartitionList();
        playlistList = app.getPlaylistList();

        boolean displayLayout = false;

        if (menuValue == PARTITION)
        {
            displayLayout = partitionList != null && partitionList.size() > 0;
        }

        if (menuValue == PLAYLIST)
        {
            displayLayout = playlistList != null && playlistList.size() > 0;
        }

        if (displayLayout)
        {
            return LISTLAYOUT;
        }
        else
        {
            return ANDROIDGUY;
        }
    }

    public void listLayout()
    {
        textTitle.setVisibility(View.VISIBLE);
        textArtist.setVisibility(View.VISIBLE);
        deleteLayout.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);

        mainLayout.setVisibility(View.GONE);

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
            public void onClick(View v)
            {
                if (deleteMode)
                {
                    if (displayChoice == ARTIST)
                    {
                        textTitle.setBackgroundColor(getResources().getColor(R.color.darkred));
                        textArtist.setBackgroundColor(getResources().getColor(R.color.red));

                        displayChoice = TITLE;
                    }
                }
                else
                {
                    if (displayChoice == ARTIST)
                    {
                        textTitle.setBackgroundColor(getResources().getColor(R.color.darkcyan));
                        textArtist.setBackgroundColor(getResources().getColor(R.color.cyan));

                        displayChoice = TITLE;
                    }
                }

            }
        });

        textArtist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (deleteMode)
                {
                    if (displayChoice == TITLE)
                    {
                        textTitle.setBackgroundColor(getResources().getColor(R.color.red));
                        textArtist.setBackgroundColor(getResources().getColor(R.color.darkred));

                        displayChoice = ARTIST;
                    }
                }
                else
                {
                    if (displayChoice == TITLE)
                    {
                        textTitle.setBackgroundColor(getResources().getColor(R.color.cyan));
                        textArtist.setBackgroundColor(getResources().getColor(R.color.darkcyan));

                        displayChoice = ARTIST;
                    }
                }
            }
        });

        // Default values
        int marginSide = 0;
        int buttonHeight = 200;

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

        toolbar.setBackgroundColor(getResources().getColor(R.color.cyan));
        deleteButton.setImageResource(R.drawable.ic_delete_white_48dp);
        createButton.setImageResource(R.drawable.ic_add_circle_white_48dp);
        createLayout.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                goToCreate();
            }
        });

        if (displayChoice == TITLE)
        {
            textTitle.setBackgroundColor(getResources().getColor(R.color.darkcyan));
            textArtist.setBackgroundColor(getResources().getColor(R.color.cyan));
        }
        else
        {
            textTitle.setBackgroundColor(getResources().getColor(R.color.cyan));
            textArtist.setBackgroundColor(getResources().getColor(R.color.darkcyan));
        }

        deleteMode = false;
        deleteLayout.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (deleteMode)
                {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.cyan));
                    deleteButton.setImageResource(R.drawable.ic_delete_white_48dp);
                    createButton.setImageResource(R.drawable.ic_add_circle_white_48dp);
                    createLayout.setOnClickListener(new View.OnClickListener()
                    {
                        public void onClick(View v)
                        {
                            goToCreate();
                        }
                    });

                    if (displayChoice == TITLE)
                    {
                        textTitle.setBackgroundColor(getResources().getColor(R.color.darkcyan));
                        textArtist.setBackgroundColor(getResources().getColor(R.color.cyan));
                    }
                    else
                    {
                        textTitle.setBackgroundColor(getResources().getColor(R.color.cyan));
                        textArtist.setBackgroundColor(getResources().getColor(R.color.darkcyan));
                    }

                    for (int i = 0 ; i < arraySize ; i++)
                    {
                        linearLayout.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.cyan));
                    }

                    deleteMode = false;
                }
                else
                {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.red));
                    deleteButton.setImageResource(R.drawable.ic_clear_white_48dp);
                    createButton.setImageResource(R.drawable.ic_done_grey_48dp);
                    createLayout.setOnClickListener(validateDelete);
                    createLayout.setClickable(false);
                    toDelete = new ArrayList<>();

                    if (displayChoice == TITLE)
                    {
                        textTitle.setBackgroundColor(getResources().getColor(R.color.darkred));
                        textArtist.setBackgroundColor(getResources().getColor(R.color.red));
                    }
                    else
                    {
                        textTitle.setBackgroundColor(getResources().getColor(R.color.red));
                        textArtist.setBackgroundColor(getResources().getColor(R.color.darkred));
                    }

                    deleteMode = true;
                }
            }
        });

        View.OnClickListener buttonEffect = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                v.startAnimation(buttonClickRelease);

                if (deleteMode)
                {
                    if (toDelete.contains(v.getId()))
                    {
                        v.setBackgroundColor(getResources().getColor(R.color.cyan));
                        toDelete.remove(Integer.valueOf(v.getId()));

                        if (toDelete.size() == 0)
                        {
                            createButton.setImageResource(R.drawable.ic_done_grey_48dp);
                            createLayout.setClickable(false);
                        }
                    }
                    else
                    {
                        v.setBackgroundColor(getResources().getColor(R.color.red));
                        toDelete.add(v.getId());

                        createButton.setImageResource(R.drawable.ic_done_white_48dp);
                        createLayout.setClickable(true);
                    }
                }
                else
                {
                    if (menuValue == PARTITION)
                    {
                        goToPlay(v.getId());
                    }

                    if (menuValue == PLAYLIST)
                    {
                        goToSelect(v.getId());
                    }
                }
            }
        };

        arraySize = 0;

        if (menuValue == PARTITION)
        {
            arraySize = partitionList.size();
        }

        if (menuValue == PLAYLIST)
        {
            arraySize = playlistList.size();
        }

        for (int i = 0 ; i < arraySize ; i++)
        {
            // Create a new RelativeLayout
            RelativeLayout newButton = new RelativeLayout(this);
            newButton.setId(i);

            // Defining the RelativeLayout layout parameters
            RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);

            int marginBottom = 0;

            if (i == arraySize - 1)
            {
                marginBottom = marginTop;
            }

            buttonParams.setMargins(marginSide, marginTop, marginSide, marginBottom);
            buttonParams.height = buttonHeight;

            // Creating a new TextView
            TextView songName = new TextView(this);
            String toDisplay = "None";

            if (menuValue == PARTITION)
            {
                toDisplay = partitionList.get(i).getArtist() + " - " + partitionList.get(i).getTitle();
            }

            if (menuValue == PLAYLIST)
            {
                toDisplay = playlistList.get(i).getName();
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
            newButton.setOnClickListener(buttonEffect);

            // Add the RelativeLayout to the main LinearLayout
            linearLayout.addView(newButton, i);
        }

        layoutEmpty = false;
        androidGuyEmpty = true;
    }

    public void androidGuyLayout()
    {
        textArtist.setVisibility(View.GONE);
        textTitle.setVisibility(View.GONE);
        deleteLayout.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);

        mainLayout.setVisibility(View.VISIBLE);

        toolbar.setBackgroundColor(getResources().getColor(R.color.cyan));
        createButton.setImageResource(R.drawable.ic_add_circle_white_48dp);
        createLayout.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                goToCreate();
            }
        });

        // Default values
        int imageSize = 0;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) // Portait orientation
        {
            textSize = size.x/36;
            marginTop = size.y/5;
            imageSize = size.y/6;
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) // Landscape orientation
        {
            textSize = size.x/60;
            marginTop = size.y/10;
            imageSize = size.y/6;
        }

        // Change the size of the sadAndroidGuy image
        ImageView androidGuy = findViewById(R.id.androidGuy);
        ConstraintLayout.LayoutParams paramsLayout = (ConstraintLayout.LayoutParams) androidGuy.getLayoutParams();
        paramsLayout.setMargins(0,marginTop,0,0);
        paramsLayout.height = imageSize;
        androidGuy.setLayoutParams(paramsLayout);

        // Change the size of the text
        TextView textCreate = findViewById(R.id.textCreate);
        TextView Ohoh = findViewById(R.id.Ohoh);
        TextView trouvay = findViewById(R.id.trouvay);

        if (menuValue == PLAYLIST) // If I create a playlist, I change the text
        {
            trouvay.setText("n'avez aucune playlist...");
            textCreate.setText("Créer votre première playlist");
        }

        textCreate.setTextSize(textSize);
        Ohoh.setTextSize(textSize);
        trouvay.setTextSize(textSize);

        // Make the relativeLayout button clickable with an animation
        RelativeLayout buttonCreate = findViewById(R.id.buttonCreate);
        buttonCreate.setClickable(true);
        buttonCreate.setFocusable(true);
        buttonCreate.setFocusableInTouchMode(false);
        buttonCreate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                v.startAnimation(buttonClickRelease);

                goToCreate();
            }
        });

        layoutEmpty = true;
        androidGuyEmpty = false;
    }

    public void goToCreate()
    {
        layoutEmpty = true;
        androidGuyEmpty = true;

        if (menuValue == PARTITION)
        {
            Intent intent = new Intent(this, ChangePartition.class);
            startActivity(intent);
        }

        if (menuValue == PLAYLIST)
        {
            Intent intent = new Intent(this, CreatePlaylist.class);
            startActivity(intent);
        }
    }

    public void goToHome()
    {
        layoutEmpty = true;
        androidGuyEmpty = true;

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void goToPlay(int songNumber)
    {
        layoutEmpty = true;
        androidGuyEmpty = true;

        Intent intent = new Intent(this, Play.class);
        intent.putExtra("songNumber", songNumber);
        startActivity(intent);
    }

    public void goToSelect(int id)
    {
        layoutEmpty = true;
        androidGuyEmpty = true;

        Intent intent = new Intent(this, InsidePlaylist.class);
        intent.putExtra("playlistNumber", id);
        startActivity(intent);
    }

    View.OnClickListener validateDelete = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPrefs.edit();
            Gson gson = new Gson();

            PartitionActivity app = (PartitionActivity) getApplicationContext();

            if (menuValue == PARTITION)
            {
                ArrayList<Partition> newPartitionList = new ArrayList<>();

                for (int i = 0 ; i < partitionList.size() ; i++)
                {
                    if (!toDelete.contains(i))
                    {
                        newPartitionList.add(partitionList.get(i));
                    }
                }

                String json = gson.toJson(newPartitionList);

                editor.putString("partitionList", json);
                editor.apply();
                app.savePartitionList(newPartitionList);
            }

            if (menuValue == PLAYLIST)
            {
                ArrayList<Playlist> newPlaylistList = new ArrayList<>();

                System.out.println("Premier indice : " + toDelete.get(0));

                for (int i = 0 ; i < playlistList.size() ; i++)
                {
                    System.out.println(i);

                    if (!toDelete.contains(i))
                    {
                        System.out.println("In ! " + i);
                        newPlaylistList.add(playlistList.get(i));
                    }
                }

                System.out.println("new size : " + newPlaylistList.size());

                String json = gson.toJson(newPlaylistList);

                editor.putString("playlistList", json);
                editor.apply();
                app.savePlaylistList(newPlaylistList);
            }

            if (refreshLayout() == LISTLAYOUT)
            {
                linearLayout.removeAllViews();
                listLayout();
            }
            else
            {
                androidGuyLayout();
            }
        }
    };
}