package com.autoscroll.fraba.defiloche;

import android.os.Environment;
import java.io.File;

public class Partition
{
    String mTitle;
    String mArtist;
    File pdfFile;
    int mSpeed;

    Partition()
    {
    }

    Partition(String artist, String title, int speed, String fileName)
    {
        setArtist(artist);
        setTitle(title);
        setSpeed(speed);
        setFile(fileName);
    }

    String getTitle()
    {
        return mTitle;
    }

    String getArtist()
    {
        return mArtist;
    }

    File getFile()
    {
        return pdfFile;
    }

    void setTitle(String title)
    {
        mTitle = title;
    }
    void setArtist(String artist)
    {
        mArtist = artist;
    }
    void setSpeed(int speed)
    {
        mSpeed = speed;
    }
    void setFile(String fileName)
    {
        pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getParentFile().getAbsolutePath() + "/DepuisAndroid/" + fileName);
    }
        }
