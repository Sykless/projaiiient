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
        createFile(fileName);
    }

    Partition(String artist, String title, int speed, File file)
    {
        setArtist(artist);
        setTitle(title);
        setSpeed(speed);
        setFile(file);
    }

    String getTitle()
    {
        return mTitle;
    }
    String getArtist()
    {
        return mArtist;
    }
    int getSpeed()
    {
        return mSpeed;
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
    void setFile(File file)
    {
        pdfFile = file;
    }
    void createFile(String fileName)
    {
        pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getParentFile().getAbsolutePath() + "/Lecteur de partition/" + fileName);
    }

    public String toString()
    {
        return "Artist : " + mArtist + " - Title : " + mTitle + " - Speed : " + mSpeed + " - File : " + pdfFile.getName();
    }
}
