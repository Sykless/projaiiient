package com.autoscroll.fraba.defiloche;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class CreatePlaylist extends AppCompatActivity
{
    AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F); // Fading animation on button when clicked
    AlphaAnimation buttonClickRelease = new AlphaAnimation(0.8F, 1F); // Unfading animation on button when clicked

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);

        // Toolbar icons setup
        FrameLayout homeLayout = findViewById(R.id.homeLayout);
        FrameLayout backLayout = findViewById(R.id.backLayout);

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

        // Layout modification according to orientation of the device
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size); // size.x = device width - size.y = device height

        editText = findViewById(R.id.editText);

        // Default values
        int marginTop = 0;
        int marginSide = 0;
        int marginBottom = 0;
        int buttonHeight = 200;
        float textSize = 24;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) // Portait orientation
        {
            marginTop = size.y / 6;
            marginSide = size.x / 4;
            marginBottom = size.y / 2;
            textSize = size.x / 30;
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) // Landscape orientation
        {
            marginTop = size.y / 10;
            marginSide = size.x / 4;
            marginBottom = size.x / 5;
            textSize = size.x / 42;
        }

        RelativeLayout createButton = findViewById(R.id.buttonCreate);
        EditText editText = findViewById(R.id.editText);
        TextView textCreate = findViewById(R.id.textCreate);

        ConstraintLayout.LayoutParams paramsLayout = (ConstraintLayout.LayoutParams) createButton.getLayoutParams();
        paramsLayout.setMargins(marginSide,0,marginSide,marginBottom);
        createButton.setLayoutParams(paramsLayout);

        RelativeLayout.LayoutParams paramsEditText = (RelativeLayout.LayoutParams) editText.getLayoutParams();
        paramsEditText.setMargins((int) Math.round(marginSide * 0.5),marginTop,(int) Math.round(marginSide * 0.5),0);
        editText.setLayoutParams(paramsEditText);

        editText.setTextSize((float) (textSize * 0.75));
        textCreate.setTextSize(textSize);

        buttonClick.setDuration(100);
        buttonClickRelease.setDuration(100);
        buttonClickRelease.setStartOffset(100);

        createButton.setClickable(true);
        createButton.setFocusable(true);
        createButton.setFocusableInTouchMode(false);
        createButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                v.startAnimation(buttonClickRelease);

                goToAdd();
            }
        });
    }

    public void goToHome()
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void goToAdd()
    {
        String playlistName = editText.getText().toString();

        if (playlistName.length() > 0)
        {
            PartitionActivity app = (PartitionActivity) getApplicationContext();
            ArrayList<Playlist> list = app.getPlaylistList();

            if (list == null)
            {
                list = new ArrayList<>();
            }

            list.add(new Playlist(playlistName));

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            Gson gson = new Gson();

            String json = gson.toJson(list);

            editor.putString("playlistList", json);
            editor.apply();

            app.savePlaylistList(list);

            Intent intent = new Intent(this, AddPartitionToPlaylist.class);
            intent.putExtra("playlistNumber",list.size() - 1);
            startActivity(intent);
        }
        else
        {
            System.out.println("Nope !");
        }
    }
}
