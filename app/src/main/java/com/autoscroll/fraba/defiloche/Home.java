package com.autoscroll.fraba.defiloche;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity
{
    private static final int CODE_PLAY = 1;

    int actionBarHeight = 0;

    AnimationDrawable rocketAnimation;
    AnimationDrawable button2Animation;
    AnimationDrawable button3Animation;

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F); // Animation on button when clicked
    private AlphaAnimation buttonClickRelease = new AlphaAnimation(0.8F,1F); // Animation when released

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        // Toolbar setup
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get the height of the toolbar
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        // Layout modification
        /*
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int marginTop = 0;
        int marginSide = 0;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            int width = size.x;
            int heigth = size.y - actionBarHeight;

            float ratio = (float) heigth / width;

            marginTop = (int) Math.round(0.81/ratio*width);
            marginSide = (int) Math.round(0.162/ratio*heigth);
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            int width = size.y;
            int heigth = size.x - actionBarHeight;

            float ratio = (float) heigth / width;

            marginTop = (int) Math.round(0.205/ratio*width);
            marginSide = (int) Math.round(0.164/ratio*heigth);

            marginTop = 400;
        }

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.setMargins(marginSide,marginTop,marginSide,marginTop);
        relativeLayout.setLayoutParams(params);

        */

        ImageView rocketImage = (ImageView) findViewById(R.id.test_button_image);
        rocketImage.setBackgroundResource(R.drawable.animation);
        rocketAnimation = (AnimationDrawable) rocketImage.getBackground();
        rocketAnimation.start();

        ImageView rocketImage2 = (ImageView) findViewById(R.id.test_button_image2);
        rocketImage2.setBackgroundResource(R.drawable.animation);
        button2Animation = (AnimationDrawable) rocketImage2.getBackground();
        button2Animation.start();

        ImageView rocketImage3 = (ImageView) findViewById(R.id.test_button_image3);
        rocketImage3.setBackgroundResource(R.drawable.animation);
        button3Animation = (AnimationDrawable) rocketImage3.getBackground();
        button3Animation.start();

        setRelativeLayoutButton(this,R.id.test_layout);
        setRelativeLayoutButton(this,R.id.test_layout2);
        setRelativeLayoutButton(this,R.id.test_layout3);

        RelativeLayout button1 = findViewById(R.id.test_layout);
        RelativeLayout button2 = findViewById(R.id.test_layout2);
        RelativeLayout button3 = findViewById(R.id.test_layout3);

        buttonClick.setDuration(300);
        buttonClickRelease.setDuration(300);
        buttonClickRelease.setStartOffset(200);

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

        button1.setOnClickListener(buttonEffect);
        button2.setOnClickListener(buttonEffect);
        button3.setOnClickListener(buttonEffect);
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
        RelativeLayout buttonTest = new RelativeLayout(context);
        RelativeLayout layout = findViewById(id);

        Log.e("Test ",String.valueOf(layout.getChildCount()));

        // copy layout parameters
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        buttonTest.setLayoutParams(params);

        // here I am using temporary instance of Button class
        // to get standard button background and to get button text color

        Button bt = new Button(context);
        bt.setBackgroundColor(getResources().getColor(R.color.cyan));
        buttonTest.setBackground(bt.getBackground());

        // copy all child from relative layout to this button
        while (layout.getChildCount() > 0)
        {
            Log.e("Test","In");
            View vchild = layout.getChildAt(0);
            ((ViewGroup)vchild.getParent()).removeView(vchild);
            buttonTest.addView(vchild);

            // if child is textView set its color to standard buttong text colors
            // using temporary instance of Button class
            if (vchild instanceof TextView  )
            {
                ((TextView)vchild).setTextColor(getResources().getColor(R.color.white));
            }

            // just to be sure that child views can't be clicked and focused
            vchild.setClickable(false);
            vchild.setFocusable(false);
            vchild.setFocusableInTouchMode(false);
        }

        // remove all view from layout (maybe it's not necessary)
        layout.removeAllViews();

        // set that this button is clickable, focusable, ...
        buttonTest.setClickable(true);
        buttonTest.setFocusable(true);
        buttonTest.setFocusableInTouchMode(false);

        // replace relative layout in parent with this one modified to looks like button
        ViewGroup vp = (ViewGroup)layout.getParent();
        int index = vp.indexOfChild(layout);
        vp.removeView(layout);
        vp.addView(buttonTest,index);

        buttonTest.setId(id);
    }




}
