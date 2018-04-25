package com.autoscroll.fraba.defiloche;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Play extends AppCompatActivity {
    ImageView imageView;
    LinearLayout linearLayout;
    ScrollView scrollView;

    FrameLayout playPauseLayout;
    FrameLayout replayLayout;
    RelativeLayout textLayout;
    ImageView playPauseButton;
    ConstraintLayout toolbarLayout;
    FrameLayout backLayout;
    FrameLayout homeLayout;
    TextView textTitle;

    ImageView leftArrow;
    ImageView rightArrow;
    ImageView topArrow;
    ImageView bottomArrow;

    Partition partitionToPlay;
    PartitionActivity app;

    Point size;

    float endPage = 0;
    float endScreen = 0;

    int numberPdf = 0;
    int songNumber = 0;
    int actionBarHeight = 0;
    int statusBarHeight = 0;
    int bottomScroll =  0;

    ObjectAnimator movePartition;
    ObjectAnimator moveRestart;
    AnimatorSet animatorSet;

    boolean isRunning = false;
    boolean layoutSetup = false;
    boolean canScroll = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        playPauseButton = findViewById(R.id.playPauseButton);
        playPauseLayout = findViewById(R.id.playPauseLayout);
        replayLayout = findViewById(R.id.replayLayout);
        textLayout = findViewById(R.id.textLayout);
        toolbarLayout = findViewById(R.id.toolbarLayout);
        homeLayout = findViewById(R.id.homeLayout);
        backLayout = findViewById(R.id.backLayout);
        linearLayout = findViewById(R.id.main_view);
        scrollView = findViewById(R.id.scroll);
        textTitle = findViewById(R.id.textTitle);
        leftArrow = findViewById(R.id.leftArrow);
        rightArrow = findViewById(R.id.rightArrow);
        topArrow = findViewById(R.id.topArrow);
        bottomArrow = findViewById(R.id.bottomArrow);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get the height of the toolbar and statusBar
        actionBarHeight = getActionBarHeight();
        statusBarHeight = getStatusBarHeight();

        // Toolbar modification according to size of the device

        textLayout.getViewTreeObserver().addOnGlobalLayoutListener(textAdapter);
        playPauseLayout.setOnClickListener(playPauseListener);
        linearLayout.setOnClickListener(playPauseListener);
        replayLayout.setOnClickListener(replayListener);
        homeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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

        leftArrow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                songNumber--;
                refreshLayout(songNumber);

                return true;
            }});

        rightArrow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                songNumber++;
                refreshLayout(songNumber);

                return true;
            }});

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                songNumber--;
                scrollView.setScrollY(0);
                refreshLayout(songNumber);
            }});
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                songNumber++;
                scrollView.setScrollY(0);
                refreshLayout(songNumber);
            }});
        topArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                scrollView.setScrollY(scrollView.getScrollY() - Math.round(endScreen) + actionBarHeight + statusBarHeight);
            }});
        bottomArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                scrollView.setScrollY(scrollView.getScrollY() + Math.round(endScreen) - actionBarHeight - statusBarHeight);
            }});


        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        endScreen = (float) size.y;
        scrollView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return isRunning;
            }
        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged()
            {
                if (scrollView.getChildAt(0).getBottom() <= (scrollView.getHeight() + scrollView.getScrollY()))
                {
                    bottomArrow.setVisibility(View.GONE);
                }
                else
                {
                    bottomArrow.setVisibility(View.VISIBLE);
                }

                if (scrollView.getScrollY() == 0)
                {
                    topArrow.setVisibility(View.GONE);
                }
                else
                {
                    topArrow.setVisibility(View.VISIBLE);
                }
            }
        });

        songNumber = getIntent().getIntExtra("songNumber", 0);
        refreshLayout(songNumber);
    }

    public void goToHome()
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void refreshLayout(int songNumber)
    {
        linearLayout.removeAllViews();

        app = (PartitionActivity) getApplicationContext();
        partitionToPlay = app.getPartitionList().get(songNumber);

        ArrayList<Bitmap> pdfBitmaps = pdfToBitmap(partitionToPlay.getFile());

        for (Bitmap pdfBitmap : pdfBitmaps) // For each bitmap pdf
        {
            // Creation of a new imageView
            imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setImageBitmap(pdfBitmap); // Put the bitmap in the imageView

            linearLayout.addView(imageView); // Put the imageView in the LinearLayout
        }

        bottomScroll = Math.round(numberPdf*endPage - endScreen + actionBarHeight + statusBarHeight);

        if (songNumber > 0)
        {
            leftArrow.setVisibility(View.VISIBLE);
        }
        else
        {
            leftArrow.setVisibility(View.GONE);
        }

        if (songNumber < app.getPartitionList().size() - 1)
        {
            rightArrow.setVisibility(View.VISIBLE);
        }
        else
        {
            rightArrow.setVisibility(View.GONE);
        }

        if (bottomScroll < 0)
        {
            canScroll = false;
            topArrow.setVisibility(View.GONE);
            bottomArrow.setVisibility(View.GONE);
        }
        else
        {
            canScroll = true;
            topArrow.setVisibility(View.GONE);
            bottomArrow.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<Bitmap> pdfToBitmap(File pdfFile) // Convertit un FILE (pdf) en liste de Bitmap
    {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        try
        {
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));

            Bitmap bitmap;
            numberPdf = renderer.getPageCount();

            for (int i = 0; i < numberPdf; i++)
            {
                PdfRenderer.Page page = renderer.openPage(i);

                if (i == 0)
                {
                    endPage = size.x*page.getHeight()/page.getWidth();
                }

                bitmap = Bitmap.createBitmap(size.x, Math.round(endPage), Bitmap.Config.ARGB_8888);
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
            Log.e("Error ","Pdf load " + ex.getMessage());
        }

        return bitmaps;
    }

    View.OnClickListener playPauseListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (canScroll)
            {
                if (isRunning)
                {
                    animatorSet.cancel();
                }
                else
                {
                    isRunning = true;
                    playPauseButton.setImageResource(R.drawable.ic_pause_white_48dp);

                    movePartition = ObjectAnimator.ofInt(scrollView, "scrollY", scrollView.getScrollY(), bottomScroll);
                    movePartition.addListener(animatorPause);
                    int speed = 555555556;

                    if (partitionToPlay.getSpeed() > 0)
                    {
                        speed = partitionToPlay.getSpeed();
                    }

                    movePartition.setDuration(Math.round(speed*1000*(1 - (float)scrollView.getScrollY()/bottomScroll)));

                    animatorSet = new AnimatorSet();
                    animatorSet.play(movePartition);
                    animatorSet.start();
                }
            }
        }
    };

    public View.OnClickListener replayListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (scrollView.getScrollY() != 0 && !isRunning)
            {
                moveRestart = ObjectAnimator.ofInt(scrollView, "scrollY", scrollView.getScrollY(), 0);
                moveRestart.addListener(animatorPause);
                moveRestart.setDuration(200);

                animatorSet = new AnimatorSet();
                animatorSet.play(moveRestart);
                animatorSet.start();
            }
        }
    };

    public Animator.AnimatorListener animatorPause = new Animator.AnimatorListener()
    {
        public void onAnimationStart(Animator animation) {}
        public void onAnimationRepeat(Animator animation) {}
        public void onAnimationCancel(Animator animation) {}
        public void onAnimationEnd(Animator animation)
        {
            isRunning = false;
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);
        }
    };

    public ViewTreeObserver.OnGlobalLayoutListener textAdapter = new ViewTreeObserver.OnGlobalLayoutListener()
    {
        @Override
        public void onGlobalLayout()
        {
            if (!layoutSetup)
            {
                Rect rectView = new Rect();
                textLayout.getGlobalVisibleRect(rectView);
                int startTextLayout = rectView.left;

                homeLayout.getGlobalVisibleRect(rectView);
                int startHomeLayout = rectView.left;

                textTitle.setWidth(startHomeLayout - startTextLayout - 4);

                float textSize = size.x/80;

                if (textSize < 10)
                {
                    textSize = 10;
                }

                if (textSize > 20)
                {
                    textSize = 20;
                }

                textTitle.setTextSize(textSize);

                String artistTitle;

                if (partitionToPlay.getArtist().length() > 0 && partitionToPlay.getTitle().length() > 0)
                {
                    artistTitle = partitionToPlay.getArtist() + " - " + partitionToPlay.getTitle();
                }
                else
                {
                    artistTitle = partitionToPlay.getFile().getName();
                }

                textTitle.setText(artistTitle);

                layoutSetup = true;
            }
        }
    };

    public int getStatusBarHeight()
    {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0)
        {
            result = getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public int getActionBarHeight()
    {
        int result = 0;
        TypedValue tv = new TypedValue();

        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            result = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        return result;
    }
}

