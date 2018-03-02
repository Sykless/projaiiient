package com.autoscroll.fraba.defiloche;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    LinearLayout linearLayout;
    ScrollView sView;

    FrameLayout playPauseLayout;
    FrameLayout replayLayout;
    RelativeLayout textLayout;
    ImageView playPauseButton;
    ConstraintLayout toolbarLayout;
    FrameLayout homeLayout;
    TextView artistText;
    TextView titleText;
    TextView separatorText;

    Point size;

    float actionBarHeight = 0;
    float endPage = 0;
    float endScreen = 0;
    float textSize = 20;

    int numberPdf = 0;
    int compteurSize = 0;

    ObjectAnimator movePartition;
    ObjectAnimator moveRestart;
    AnimatorSet animatorSet;

    boolean isRestarted = true;
    boolean layoutSetup = false;
    boolean oneLine = true;
    boolean twoLinesPacked = false;

    float imagePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPauseButton = findViewById(R.id.playPauseButton);
        playPauseLayout = findViewById(R.id.playPauseLayout);
        replayLayout = findViewById(R.id.replayLayout);
        textLayout = findViewById(R.id.textLayout);
        toolbarLayout = findViewById(R.id.toolbarLayout);
        artistText = findViewById(R.id.textArtist);
        titleText = findViewById(R.id.textTitle);
        separatorText = findViewById(R.id.textSeparator);
        homeLayout = findViewById(R.id.homeLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get the height of the toolbar
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        // Toolbar modification according to size of the device
        // Main Layout margins setup
        artistText.setTextSize(20);
        titleText.setTextSize(20);
        separatorText.setTextSize(20);

        homeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });

        playPauseLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isRestarted = false;

                if (movePartition.isPaused())
                {
                    playPauseButton.setImageResource(R.drawable.ic_pause_white_48dp);
                    movePartition.resume();
                }
                else
                {
                    playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    movePartition.pause();
                }
            }
        });


        replayLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int[] locationOnScreen = new int[2];
                linearLayout.getLocationOnScreen(locationOnScreen);

                if (isRestarted)
                {
                    locationOnScreen[1] = 0;
                }

                isRestarted = true;

                moveRestart = ObjectAnimator.ofFloat(linearLayout, "translationY", (float) locationOnScreen[1], actionBarHeight);

                animatorSet.play(moveRestart);
                animatorSet.start();

                playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);

                animatorSet.play(movePartition);
                animatorSet.start();
                movePartition.pause();
            }
        });

        textLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout ()
            {
                if (!layoutSetup)
                {
                    Rect rectView = new Rect();
                    textLayout.getGlobalVisibleRect(rectView);
                    int endTextLayout = rectView.right;

                    homeLayout.getGlobalVisibleRect(rectView);
                    int startHomeLayout = rectView.left;

                    if (startHomeLayout - 5 < endTextLayout)
                    {
                        if (oneLine)
                        {
                            if (compteurSize < 5)
                            {
                                compteurSize++;
                                textSize = textSize - 1;

                                // Main Layout margins setup
                                artistText.setTextSize(textSize);
                                titleText.setTextSize(textSize);
                                separatorText.setTextSize(textSize);

                                Log.e("Test oui", "" + endTextLayout + " - " + startHomeLayout);
                            }
                            else
                            {
                                oneLine = false;
                                textSize = 20;
                                compteurSize = 0;
                                artistText.setTextSize(textSize);
                                titleText.setTextSize(textSize);
                                separatorText.setVisibility(View.GONE);

                                RelativeLayout.LayoutParams paramsTitle = (RelativeLayout.LayoutParams) titleText.getLayoutParams();
                                paramsTitle.addRule(RelativeLayout.BELOW,R.id.textArtist);
                                paramsTitle.addRule(RelativeLayout.END_OF);
                                titleText.setLayoutParams(paramsTitle);
                            }
                        }
                        else
                        {
                            if (compteurSize < 5)
                            {
                                /*
                                if (textLayout.getHeight() + 30 > actionBarHeight)
                                {
                                    artistText.setTextSize(textSize - 1);
                                    titleText.setTextSize(textSize - 1);

                                    layoutSetup = true;
                                }
                                else
                                {
                                */
                                    compteurSize++;
                                    textSize = textSize - 1;

                                    // Main Layout margins setup
                                    artistText.setTextSize(textSize);
                                    titleText.setTextSize(textSize);
                                // }

                                Log.e("Test oui", "" + endTextLayout + " - " + startHomeLayout);
                            }
                            else
                            {
                                // Shrink text

                            }
                        }
                    }
                    else
                    {
                        layoutSetup = true;
                    }
                }
            }
        });


        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            endScreen = (float) size.x;
        }
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            endScreen = (float) size.y;
        }

        linearLayout = (LinearLayout) findViewById(R.id.main_view);
        sView = (ScrollView) findViewById(R.id.scroll);

        File pdfFile = new File("sdcard/Download/test-1.pdf"); // TODO add variable path

        ArrayList<Bitmap> pdfBitmaps = pdfToBitmap(pdfFile);

        for (Bitmap pdfBitmap : pdfBitmaps) // For each bitmap pdf
        {
            // Creation of a new imageView
            imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            imageView.setImageBitmap(pdfBitmap); // Put the bitmap in the imageView
            linearLayout.addView(imageView); // Put the imageView in the LinearLayout
        }

        sView.setOnTouchListener(new ScrollDetect());

        // Animation setup
        movePartition = ObjectAnimator.ofFloat(linearLayout, "translationY", actionBarHeight, size.y - numberPdf * endPage - actionBarHeight);
        movePartition.setDuration(3000);

        moveRestart = ObjectAnimator.ofFloat(linearLayout, "translationY", actionBarHeight, endPage - endScreen);
        moveRestart.setDuration(200);

        movePartition.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imagePosition = (Float) animation.getAnimatedValue();
            }
        });

        animatorSet = new AnimatorSet();
        animatorSet.play(movePartition);
        animatorSet.start();

        movePartition.pause();
    }

    public void goToHome()
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void layoutAdaptation()
    {

    }

    public class ScrollDetect implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            return true;
        } // Disable scrolling
    }

    private ArrayList<Bitmap> pdfToBitmap(File pdfFile) // Convertit un FILE (pdf) en liste de Bitmap
    {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        try
        {
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));

            Bitmap bitmap;
            final int pageCount = renderer.getPageCount();

            for (int i = 0; i < pageCount; i++)
            {
                PdfRenderer.Page page = renderer.openPage(i);

                int newY = Math.round(size.x*(float)(page.getHeight() - actionBarHeight)/page.getWidth());

                if (i == 0)
                {
                    endPage = newY;
                    numberPdf = pageCount;
                }

                bitmap = Bitmap.createBitmap(size.x, newY, Bitmap.Config.ARGB_8888);

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                bitmaps.add(bitmap);

                page.close();
            }

            renderer.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
            Log.e("Error","Pdf load");
        }

        return bitmaps;
    }
}

