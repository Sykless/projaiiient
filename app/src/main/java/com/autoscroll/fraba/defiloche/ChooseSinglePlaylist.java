package com.autoscroll.fraba.defiloche;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ChooseSinglePlaylist extends AppCompatActivity
{
    AnimationDrawable animationSingle;
    AnimationDrawable animationAlbum;

    AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F); // Fading animation on button when clicked
    AlphaAnimation buttonClickRelease = new AlphaAnimation(0.8F,1F); // Unfading animation on button when clicked

    PartitionActivity app;
    ArrayList<Partition> partitionList;
    ArrayList<Playlist> playlistList;

    private static final int PLAY = 1;
    private static final int CREATE = 2;
    private static final int SHARE = 3;
    private static final int PARTITION = 10;
    private static final int PLAYLIST = 11;

    int selectionHome;
    int selectionPartitionPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_single_playlist);

        // Layout items declaration
        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        ImageView imageSingle = findViewById(R.id.imageSingle);
        ImageView imageAlbum = findViewById(R.id.imageAlbum);

        TextView textSingle = findViewById(R.id.textSingle);
        TextView textAlbum = findViewById(R.id.textAlbum);

        // Toolbar setup
        FrameLayout homeLayout = findViewById(R.id.homeLayout);
        FrameLayout backLayout = findViewById(R.id.backLayout);

        backLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        homeLayout.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                goToHome();
            }
        });

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
        setRelativeLayoutButton(this,R.id.buttonSingle);
        setRelativeLayoutButton(this,R.id.buttonAlbum);

        RelativeLayout buttonSingle = findViewById(R.id.buttonSingle);
        RelativeLayout buttonAlbum = findViewById(R.id.buttonAlbum);

        // Add an animation to ImageViews
        imageSingle.setBackgroundResource(R.drawable.animation);
        animationSingle = (AnimationDrawable) imageSingle.getBackground();
        animationSingle.start();

        imageAlbum.setBackgroundResource(R.drawable.animation);
        animationAlbum = (AnimationDrawable) imageAlbum.getBackground();
        animationAlbum.start();

        // Change the size of the text according to orientation
        textSingle.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        textAlbum.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);

        // Change the size of the animation according to orientation
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageSingle.getLayoutParams();
        params.width = animSize;
        params.height = animSize;
        imageSingle.setLayoutParams(params);
        imageAlbum.setLayoutParams(params);

        // Setup of fading effect on button when clicked
        buttonClick.setDuration(100);
        buttonClickRelease.setDuration(100);
        buttonClickRelease.setStartOffset(100);

        selectionHome = getIntent().getIntExtra("selectionHome",PLAY);

        View.OnClickListener buttonEffect = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                v.startAnimation(buttonClickRelease);

                selectionPartitionPlaylist = PARTITION;
                goToSelect();
            }
        };

        View.OnClickListener buttonEffectPlaylist = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                v.startAnimation(buttonClickRelease);

                selectionPartitionPlaylist = PLAYLIST;
                goToSelect();
            }
        };

        buttonSingle.setOnClickListener(buttonEffect);
        buttonAlbum.setOnClickListener(buttonEffectPlaylist);
    }

    public void goToHome()
    {
        Intent intent = new Intent(this,Home.class);
        startActivity(intent);
    }

    public void goToSelect()
    {
        Intent intent = new Intent(this,SelectSongPlaylist.class);
        intent.putExtra("selectionHome",selectionHome); // PLAY or CREATE
        intent.putExtra("selectionPartitionPlaylist", selectionPartitionPlaylist); // PARTITION or PLAYLIST
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    public void setRelativeLayoutButton(Context context, int id) // TODO Move this shit
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
}
