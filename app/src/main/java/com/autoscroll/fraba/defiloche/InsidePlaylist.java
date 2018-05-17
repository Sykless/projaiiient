package com.autoscroll.fraba.defiloche;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class InsidePlaylist extends AppCompatActivity
{
    private static final int INSIDEPLAYLIST = 2;

    boolean emptyLayout = true;
    boolean longClick = false;
    boolean swapToolbar = false;

    boolean swapMode = false;
    boolean deleteMode = false;

    float actionBarHeight;
    float statusBarHeight;
    float screenSize;

    int marginSide = 0;
    int buttonHeight = 200;
    float textSize = 24;
    int marginTop = 0;

    ArrayList<Integer> toDelete;

    Playlist currentPlaylist;
    int playlistNumber;
    int idSong;
    int currentId;
    int previousId = -1;
    int originalId;
    int selectedSong = -1;

    LinearLayout mainLayout;
    LinearLayout linearLayout;
    Toolbar toolbar;

    FrameLayout deleteLayout;
    FrameLayout createLayout;
    FrameLayout swapLayout;

    ImageView deleteButton;
    ImageView createButton;

    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_playlist);

        // Toolbar icons setup
        FrameLayout homeLayout = findViewById(R.id.homeLayout);
        FrameLayout backLayout = findViewById(R.id.backLayout);

        deleteLayout = findViewById(R.id.deleteLayout);
        createLayout = findViewById(R.id.createLayout);
        swapLayout = findViewById(R.id.swapLayout);

        deleteButton = findViewById(R.id.deleteButton);
        createButton = findViewById(R.id.createButton);

        toolbar = findViewById(R.id.toolbar);
        scrollView = findViewById(R.id.scrollView);
        mainLayout = findViewById(R.id.mainLayout);

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

        actionBarHeight = getActionBarHeight();
        statusBarHeight = getStatusBarHeight();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenSize = (float) size.y - actionBarHeight - statusBarHeight;

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

        createLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                toolbar.setBackgroundColor(getResources().getColor(R.color.cyan));
                swapMode = false;

                goToCreate();
            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (deleteMode)
                {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.cyan));
                    swapLayout.setVisibility(View.VISIBLE);
                    deleteButton.setImageResource(R.drawable.ic_delete_white_48dp);
                    createButton.setImageResource(R.drawable.ic_add_circle_white_48dp);
                    createLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToCreate();
                        }
                    });

                    for (int i = 0 ; i < currentPlaylist.getPartitionList().size() ; i++)
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
                    swapLayout.setVisibility(View.GONE);
                    toDelete = new ArrayList<>();

                    swapMode = false;
                    deleteMode = true;
                }
            }
        });

        swapLayout.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (swapMode)
                {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.cyan));
                    createLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToCreate();
                        }
                    });

                    for (int i = 0 ; i < currentPlaylist.getPartitionList().size() ; i++)
                    {
                        linearLayout.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.cyan));
                    }

                    swapMode = false;
                }
                else
                {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.purple));
                    createLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToCreate();
                        }
                    });

                    swapMode = true;
                }
            }
        });


        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.removeAllViews();
        linearLayout.setOnDragListener(dragListener);

        PartitionActivity app = (PartitionActivity) getApplicationContext();
        ArrayList<Playlist> playlistList = app.getPlaylistList();

        playlistNumber = getIntent().getIntExtra("playlistNumber",0);
        currentPlaylist = playlistList.get(playlistNumber);

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
            newButton.setOnLongClickListener(buttonSwap);
            newButton.setOnTouchListener(buttonClick);

            // Add the RelativeLayout to the main LinearLayout
            linearLayout.addView(newButton, i);
        }

        emptyLayout = false;
    }

    void swapTitre(int newId, int oldId)
    {
        // Swap in currentPlaylist
        Partition oldPartition = currentPlaylist.getPartitionList().get(oldId);
        Partition newPartition = currentPlaylist.getPartitionList().get(newId);

        currentPlaylist.setPartition(oldId,newPartition);
        currentPlaylist.setPartition(newId,oldPartition);

        // Save currentPlaylist
        PartitionActivity app = (PartitionActivity) getApplicationContext();
        ArrayList<Playlist> playlistList = app.getPlaylistList();

        playlistList.set(playlistNumber, currentPlaylist);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(playlistList);

        editor.putString("playlistList", json);
        editor.apply();
        app.savePlaylistList(playlistList);
    }

    public void goToHome()
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void goToCreate()
    {
        emptyLayout = true;

        Intent intent = new Intent(this, AddPartitionToPlaylist.class);
        intent.putExtra("playlistNumber",playlistNumber);
        intent.putExtra("displayMode", INSIDEPLAYLIST);
        startActivity(intent);
    }

    View.OnDragListener dragListener = new View.OnDragListener()
    {
        @Override
        public boolean onDrag(View view, DragEvent event)
        {
            if (event.getAction() == DragEvent.ACTION_DROP)
            {
                View selectedView = linearLayout.getChildAt(currentId);
                selectedView.setVisibility(View.VISIBLE);

                toolbar.setBackgroundColor(getResources().getColor(R.color.cyan));
                refreshLayout();

                longClick = false;
            }

            if (event.getAction() ==  DragEvent.ACTION_DRAG_ENDED)
            {
                View v = (View) event.getLocalState();
                v.setVisibility(View.VISIBLE);

                if (!event.getResult())
                {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Échange impossible.", Toast.LENGTH_LONG).show();

                    swapTitre(idSong,originalId);

                    toolbar.setBackgroundColor(getResources().getColor(R.color.cyan));
                    refreshLayout();

                    return false;
                }
            }

            if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION)
            {
                Point touchPosition = getTouchPositionFromDragEvent(view, event);

                float positionY = touchPosition.y - actionBarHeight - statusBarHeight;

                while (positionY - scrollView.getScrollY() > screenSize - 100 && scrollView.canScrollVertically(1))
                {
                    scrollView.scrollBy(0,1);
                    positionY = touchPosition.y - actionBarHeight - statusBarHeight;
                }

                while (positionY - scrollView.getScrollY() < 100 && scrollView.canScrollVertically(-1))
                {
                    scrollView.scrollBy(0,-1);
                    positionY = touchPosition.y - actionBarHeight - statusBarHeight;
                }

                idSong = (int)(positionY / (marginTop + buttonHeight));

                if (idSong >= currentPlaylist.getPartitionList().size())
                {
                    idSong = currentPlaylist.getPartitionList().size() - 1;
                }

                int goToBottom = 1;

                if (idSong < currentId)
                {
                    goToBottom = -1;
                }

                if (idSong != currentId && longClick)
                {
                    do
                    {
                        System.out.println("CurrentId : " + currentId + " - idSong : " + idSong);

                        RelativeLayout oldPartition = (RelativeLayout)(linearLayout.getChildAt(currentId));
                        TextView oldTextView = (TextView)(oldPartition.getChildAt(0));
                        String oldTitre = (String) oldTextView.getText();

                        RelativeLayout newPartition = (RelativeLayout)(linearLayout.getChildAt(currentId + goToBottom));
                        TextView newTextView = (TextView)(newPartition.getChildAt(0));
                        String newTitre = (String) newTextView.getText();

                        newPartition.setVisibility(View.INVISIBLE);
                        oldPartition.setVisibility(View.VISIBLE);

                        newTextView.setText(oldTitre);
                        oldTextView.setText(newTitre);

                        newPartition.setId(currentId);
                        oldPartition.setId(currentId + goToBottom);

                        swapTitre(currentId + goToBottom, currentId);

                        currentId = currentId + goToBottom;
                    }
                    while (currentId != idSong);
                }
            }

            return true;
        }
    };

    View.OnTouchListener buttonClick = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (deleteMode)
            {
                if (event.getAction() == MotionEvent.ACTION_UP)
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
            }
            else
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    v.animate().alpha(0.8F).setDuration(100).start();
                }

                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    v.animate().alpha(1F).setDuration(100).start();

                    if (!longClick && swapMode)
                    {
                        if (selectedSong < 0) // No song selected
                        {
                            v.setBackgroundColor(getResources().getColor(R.color.purple));
                            selectedSong = v.getId();
                        }
                        else
                        {
                            swapTitre(selectedSong,v.getId());
                            selectedSong = -1;

                            refreshLayout();
                        }
                    }

                    longClick = false;
                }
            }

            return false;
        }
    };

    View.OnLongClickListener buttonSwap = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View v)
        {
            if (!deleteMode)
            {
                longClick = true;
                scrollView.setVerticalScrollBarEnabled(false);
                v.setAlpha(1F);
                v.setBackgroundColor(getResources().getColor(R.color.purple));
                toolbar.setBackgroundColor(getResources().getColor(R.color.purple));

                currentId = v.getId();
                originalId = currentId;

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
                v.setVisibility(View.INVISIBLE);

                v.setBackgroundColor(getResources().getColor(R.color.cyan));
                return true;
            }

            return true;
        }
    };

    View.OnClickListener validateDelete = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPrefs.edit();
            Gson gson = new Gson();

            PartitionActivity app = (PartitionActivity) getApplicationContext();
            ArrayList<Partition> newPlaylist = new ArrayList<>();

            for (int i = 0 ; i < currentPlaylist.getPartitionList().size() ; i++)
            {
                if (!toDelete.contains(i))
                {
                    newPlaylist.add(currentPlaylist.getPartitionList().get(i));
                }
            }

            ArrayList<Playlist> playlistList = app.getPlaylistList();

            currentPlaylist.setPlaylist(newPlaylist);
            playlistList.set(playlistNumber,currentPlaylist);
            String json = gson.toJson(playlistList);

            editor.putString("playlistList", json);
            editor.apply();
            app.savePlaylistList(playlistList);

            toolbar.setBackgroundColor(getResources().getColor(R.color.cyan));
            swapLayout.setVisibility(View.VISIBLE);
            deleteButton.setImageResource(R.drawable.ic_delete_white_48dp);
            createButton.setImageResource(R.drawable.ic_add_circle_white_48dp);
            createLayout.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    goToCreate();
                }
            });

            deleteMode = false;

            refreshLayout();

            /*
            TODO SadAndroid

            if (refreshLayout() == LISTLAYOUT)
            {
                linearLayout.removeAllViews();
                listLayout();
            }
            else
            {
                androidGuyLayout();
            }
            */
        }
    };

    public static Point getTouchPositionFromDragEvent(View item, DragEvent event)
    {
        Rect rItem = new Rect();
        item.getGlobalVisibleRect(rItem);
        return new Point(rItem.left + Math.round(event.getX()), rItem.top + Math.round(event.getY()));
    }

    public float getStatusBarHeight()
    {
        float result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0)
        {
            result = getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public float getActionBarHeight()
    {
        float result = 0;
        TypedValue tv = new TypedValue();

        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            result = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        return result;
    }
}

