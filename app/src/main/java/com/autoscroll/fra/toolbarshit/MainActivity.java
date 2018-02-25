package com.autoscroll.fra.toolbarshit;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    ImageView imageView;
    LinearLayout linearLayout;
    ScrollView sView;

    Point size;

    float actionBarHeight = 0;
    float endPage = 0;
    float endScreen = 0;
    int numberPdf = 0;

    ObjectAnimator movePartition;
    ObjectAnimator moveRestart;
    AnimatorSet animatorSet;
    boolean isRestarted = true;

    float imagePosition = 0;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Test");

        // Set the variable endScreen to the correct value according to the orientation of the device
        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            endScreen = (float) size.x;
        }
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            endScreen = (float) size.y;
        }

        linearLayout = (LinearLayout) findViewById(R.id.main_view);
        sView = (ScrollView) findViewById(R.id.scroll);

        File pdfFile = new File("sdcard/Download/test.pdf");
        ArrayList<Bitmap> pdfBitmaps = pdfToBitmap(pdfFile);

        for (Bitmap pdfBitmap : pdfBitmaps) // For each bitmap pdf
        {
            // Creation of a new imageView
            imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            imageView.setImageBitmap(pdfBitmap); // Put the bitmap in the imageView
            linearLayout.addView(imageView); // Put the imageView in the LinearLayout
        }

        // System.out.println("Taille bitmaps :" + pdfBitmaps.size() + " / Taille file : " + pdfFile.length());

        Log.e("Taille bitmaps",String.valueOf(pdfBitmaps.size()));

        sView.setOnTouchListener( new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // Get the height of the toolbar
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        movePartition = ObjectAnimator.ofFloat(linearLayout, "translationY", actionBarHeight, size.y - numberPdf*endPage - actionBarHeight);
        movePartition.setDuration(3000);

        moveRestart = ObjectAnimator.ofFloat(linearLayout, "translationY", actionBarHeight, endPage - endScreen);
        moveRestart.setDuration(200);

        movePartition.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imagePosition = (Float)animation.getAnimatedValue();
            }
        });

        animatorSet = new AnimatorSet();
        animatorSet.play(movePartition);
        animatorSet.start();

        movePartition.pause();

        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_play_pause)
        {
            isRestarted = false;

            if (movePartition.isPaused())
            {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_pause_black_24dp));
                movePartition.resume();
            }
            else
            {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_play_arrow_black_24dp));
                movePartition.pause();
            }

            return true;
        }

        if (id == R.id.action_restart)
        {

            int[] locationOnScreen = new int[2];
            linearLayout.getLocationOnScreen(locationOnScreen);

            if (isRestarted)
            {
                locationOnScreen[1] = 0;
            }

            isRestarted = true;

            moveRestart = ObjectAnimator.ofFloat(linearLayout, "translationY", (float) locationOnScreen[1],actionBarHeight);

            animatorSet.play(moveRestart);
            animatorSet.start();

            menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_play_arrow_black_24dp));

            animatorSet.play(movePartition);
            animatorSet.start();
            movePartition.pause();

            return true;
        }

        return super.onOptionsItemSelected(item);
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

                // close the page
                page.close();
            }

            // close the renderer
            renderer.close();
        } catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("YESfneilksglfbzjlkqngfljkdsmbgljmkqlen:v");
            System.err.println(ex.getMessage());
        }

        return bitmaps;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            endScreen = (float) size.x;
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            endScreen = (float) size.y;
        }

        Log.e("Test",String.valueOf(linearLayout.getChildCount()));
    }
}

