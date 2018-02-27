package com.autoscroll.fraba.defiloche;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Home extends AppCompatActivity
{
    private static final int CODE_PLAY = 1;

    AnimationDrawable animationPlay;
    AnimationDrawable animationCreate;
    AnimationDrawable animationShare;

    AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F); // Fading animation on button when clicked
    AlphaAnimation buttonClickRelease = new AlphaAnimation(0.8F,1F); // Unfading animation on button when clicked

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        // Layout items declaration
        Toolbar toolbar = findViewById(R.id.toolbar);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        ImageView imagePlay = findViewById(R.id.test_button_image);
        ImageView imageCreate = findViewById(R.id.test_button_image2);
        ImageView imageShare = findViewById(R.id.test_button_image3);

        TextView textPlay = findViewById(R.id.test_button_text);
        TextView textCreate = findViewById(R.id.test_button_text2);
        TextView textShare = findViewById(R.id.test_button_text3);

        // Toolbar setup
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
        setRelativeLayoutButton(this,R.id.test_layout);
        setRelativeLayoutButton(this,R.id.test_layout2);
        setRelativeLayoutButton(this,R.id.test_layout3);

        RelativeLayout buttonPlay = findViewById(R.id.test_layout);
        RelativeLayout buttonCreate = findViewById(R.id.test_layout2);
        RelativeLayout buttonShare = findViewById(R.id.test_layout3);

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

                System.out.println("Test");

                // goToPlay(v);
            }
        };

        buttonPlay.setOnClickListener(buttonEffect);
        buttonCreate.setOnClickListener(buttonEffect);
        buttonShare.setOnClickListener(buttonEffect);
    }

    public void goToPlay(View view)
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivityForResult(intent,CODE_PLAY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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
}
