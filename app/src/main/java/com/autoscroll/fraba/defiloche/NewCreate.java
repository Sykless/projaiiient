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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class NewCreate extends AppCompatActivity
{
    AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F); // Fading animation on button when clicked
    AlphaAnimation buttonClickRelease = new AlphaAnimation(0.8F, 1F); // Unfading animation on button when clicked

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_create);

        // Layout modification according to orientation of the device
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size); // size.x = device width - size.y = device height

        // Default values
        float textSize = 24;
        int marginTop = 0;
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

        ImageView androidGuy = findViewById(R.id.androidGuy);
        ConstraintLayout.LayoutParams paramsLayout = (ConstraintLayout.LayoutParams) androidGuy.getLayoutParams();
        paramsLayout.setMargins(0,marginTop,0,0);
        paramsLayout.height = imageSize;
        androidGuy.setLayoutParams(paramsLayout);

        TextView textCreate = findViewById(R.id.textCreate);
        TextView Ohoh = findViewById(R.id.Ohoh);
        TextView trouvay = findViewById(R.id.trouvay);

        textCreate.setTextSize(textSize);
        Ohoh.setTextSize(textSize);
        trouvay.setTextSize(textSize);

        buttonClick.setDuration(100);
        buttonClickRelease.setDuration(100);
        buttonClickRelease.setStartOffset(100);

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

        FrameLayout createLayout = findViewById(R.id.createLayout);
        createLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                v.startAnimation(buttonClickRelease);

                goToCreate();
            }
        });
    }

    public void goToCreate()
    {
        /*
            Intent intent = new Intent(this, Play.class);
            startActivity(intent);
        */

        PartitionActivity app = (PartitionActivity) getApplicationContext();
        ArrayList<Partition> list = app.getPartitionList();

        if (list == null)
        {
            list = new ArrayList<>();
        }

        list.add(new Partition("Artist","Title",0,"test.pdf"));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(list);

        editor.putString("arrayList", json); // "arrayList" is only an ID, you can use "jambon" if you like as long as you use the same ID to get the data back
        editor.apply();
        app.savePartitionList(list);
    }
}
