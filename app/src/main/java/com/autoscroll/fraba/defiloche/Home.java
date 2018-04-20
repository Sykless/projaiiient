package com.autoscroll.fraba.defiloche;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Home extends AppCompatActivity
{
    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public final int EXTERNAL_REQUEST = 138;

    AnimationDrawable animationPlay;
    AnimationDrawable animationCreate;
    AnimationDrawable animationShare;

    AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F); // Fading animation on button when clicked
    AlphaAnimation buttonClickRelease = new AlphaAnimation(0.8F,1F); // Unfading animation on button when clicked

    PartitionActivity app;
    ArrayList<Partition> partitionList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        requestForPermission();

        // Layout items declaration
        Toolbar toolbar = findViewById(R.id.toolbar);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        ImageView imagePlay = findViewById(R.id.imagePlay);
        ImageView imageCreate = findViewById(R.id.imageCreate);
        ImageView imageShare = findViewById(R.id.imageShare);

        TextView textPlay = findViewById(R.id.textPlay);
        TextView textCreate = findViewById(R.id.textCreate);
        TextView textShare = findViewById(R.id.textShare);

        // Toolbar setup
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Partition ArrayList setup
        app = (PartitionActivity) getApplicationContext();
        partitionList = app.getPartitionList();

        // Layout modification according to orientation of the device
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size); // size.x = device width - size.y = device height

        // Default values
        int marginTop = 300;
        int marginSide = 200;
        float textSize = 24;
        int animSize = 72;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) // Portait orientation
        {
            marginTop = size.x/3;
            marginSide = size.x/8;
            textSize = size.x/15;
            animSize = size.x/10;
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) // Landscape orientation
        {
            marginTop = size.x/16;
            marginSide = size.y/5;
            textSize = size.x/25;
            animSize = size.x/15;
        }

        // Main Layout margins setup
        LinearLayout.LayoutParams paramsLayout = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLayout.setMargins(marginSide,marginTop,marginSide,marginTop);
        linearLayout.setLayoutParams(paramsLayout);

        // Transform every RelativeLayout with ImageView/TextView inside into a clickable Button
        setRelativeLayoutButton(this,R.id.buttonPlay);
        setRelativeLayoutButton(this,R.id.buttonCreate);
        setRelativeLayoutButton(this,R.id.buttonShare);

        RelativeLayout buttonPlay = findViewById(R.id.buttonPlay);
        RelativeLayout buttonCreate = findViewById(R.id.buttonCreate);
        RelativeLayout buttonShare = findViewById(R.id.buttonShare);

        // Add an animation to ImageViews
        imagePlay.setBackgroundResource(R.drawable.animation);
        animationPlay = (AnimationDrawable) imagePlay.getBackground();
        animationPlay.start();

        imageCreate.setBackgroundResource(R.drawable.animation);
        animationCreate = (AnimationDrawable) imageCreate.getBackground();
        animationCreate.start();

        imageShare.setBackgroundResource(R.drawable.animation);
        animationShare = (AnimationDrawable) imageShare.getBackground();
        animationShare.start();

        // Change the size of the text according to orientation
        textPlay.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        textCreate.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        textShare.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);

        // Change the size of the animation according to orientation
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imagePlay.getLayoutParams();
        params.width = animSize;
        params.height = animSize;
        imagePlay.setLayoutParams(params);
        imageCreate.setLayoutParams(params);
        imageShare.setLayoutParams(params);

        // Setup of fading effect on button when clicked
        buttonClick.setDuration(100);
        buttonClickRelease.setDuration(100);
        buttonClickRelease.setStartOffset(100);

        View.OnClickListener buttonEffect = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                v.startAnimation(buttonClickRelease);

                goToPlay(v);
            }
        };

        View.OnClickListener buttonEffectCreate = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                v.startAnimation(buttonClickRelease);

                goToCreate(v);
            }
        };

        View.OnClickListener buttonEffectShare = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                v.startAnimation(buttonClickRelease);

                goToShare(v);
            }
        };

        buttonPlay.setOnClickListener(buttonEffect);
        buttonCreate.setOnClickListener(buttonEffectCreate);
        buttonShare.setOnClickListener(buttonEffectShare);

        ActivityCompat.requestPermissions(Home.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    // Toast.makeText(Home.this, "Autorisation accordée", Toast.LENGTH_SHORT).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // Toast.makeText(Home.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void goToPlay(View view)
    {
        Intent intent;
        partitionList = app.getPartitionList();

        if (partitionList == null || partitionList.size() == 0)
        {
            intent = new Intent(this,NewCreate.class);
        }
        else
        {
            intent = new Intent(this,SelectSong.class);
        }

        startActivity(intent);
    }

    public void goToCreate(View view)
    {
        Intent intent;
        partitionList = app.getPartitionList();
        intent = new Intent(this,ChooseSinglePlaylist.class);
        /*
        if (partitionList == null || partitionList.size() == 0)
        {
            intent = new Intent(this,NewCreate.class);
        }
        else
        {
            intent = new Intent(this,SelectSong.class);
        }
        */

        intent = new Intent(this,CreatePlaylist.class);

        intent.putExtra("menuValue", 2);
        startActivity(intent);
    }

    public void goToShare(View view)
    {
        Intent intent = new Intent(this,NewCreate.class);
        intent.putExtra("menuValue", 3);
        startActivity(intent);
    }

    public void setRelativeLayoutButton(Context context, int id)
    {
        RelativeLayout buttonLayout = new RelativeLayout(context);
        RelativeLayout layout = findViewById(id);

        // Copy ancient layout parameters on my button
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        buttonLayout.setLayoutParams(params);

        // Copy parameters of a standart button on my button layout
        Button bt = new Button(context);
        bt.setBackgroundColor(getResources().getColor(R.color.cyan)); // Blueish background
        buttonLayout.setBackground(bt.getBackground());

        // Copy all children from relative layout to this button
        while (layout.getChildCount() > 0)
        {
            // I transfer the child from the ancient layout to my button
            View child = layout.getChildAt(0);
            layout.removeView(child);
            buttonLayout.addView(child);

            // If child is a TextView, I set its color to white
            if (child instanceof TextView  )
            {
                ((TextView)child).setTextColor(getResources().getColor(R.color.white));
            }

            // Just to be sure that child views can't be clicked and focused
            child.setClickable(false);
            child.setFocusable(false);
            child.setFocusableInTouchMode(false);
        }

        // Set that this button is clickable, focusable, etc
        buttonLayout.setClickable(true);
        buttonLayout.setFocusable(true);
        buttonLayout.setFocusableInTouchMode(false);

        // Replace relative layout in parent with this one modified to looks like button
        ViewGroup vp = (ViewGroup)layout.getParent();
        int index = vp.indexOfChild(layout);
        vp.removeView(layout);
        vp.addView(buttonLayout,index);

        // Put the ancient layout id in my button
        buttonLayout.setId(id);
    }

    public boolean requestForPermission()
    {
        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23)
        {
            if (!canAccessExternalSd())
            {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        return isPermissionOn;
    }

    public boolean canAccessExternalSd()
    {
        return (hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm)
    {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));

    }
}
