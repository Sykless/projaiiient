package com.autoscroll.fraba.defiloche;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Telephony;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.lang.reflect.Type;

public class PartitionActivity extends Application
{
    ArrayList<Partition> arrayListPartition = new ArrayList<>();
    ArrayList<Playlist> arrayListPlaylist = new ArrayList<>();

    boolean artisteTitreParam = true;

    @Override
    public void onCreate()
    {
        super.onCreate();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        Gson gson = new Gson();

        String json = sharedPrefs.getString("partitionList", null);
        Type type = new TypeToken<ArrayList<Partition>>() {}.getType();
        arrayListPartition = gson.fromJson(json, type);

        json = sharedPrefs.getString("playlistList", null);
        type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        arrayListPlaylist = gson.fromJson(json, type);

        if (arrayListPartition != null && arrayListPartition.size() > 0)
        {
            System.out.println("In partitionList :");

            for (Partition partition : arrayListPartition)
            {
                System.out.println(partition.getFile().getName());
            }
        }

        if (arrayListPartition == null)
        {
            System.out.println("partitionList null");
        }
        else if (arrayListPartition.size() == 0)
        {
            System.out.println("partitionList empty");
        }

        System.out.println("-----------------------------------------");

        if (arrayListPlaylist != null && arrayListPlaylist.size() > 0)
        {
            System.out.println("In playlistList :");

            for (Playlist playlist : arrayListPlaylist)
            {
                System.out.println(playlist.getName());
            }
        }

        if (arrayListPlaylist == null)
        {
            System.out.println("playlistList null");
        }
        else if (arrayListPlaylist.size() == 0)
        {
            System.out.println("playlistList empty");
        }
    }

    ArrayList<Partition> getPartitionList()
    {
        return arrayListPartition;
    }
    ArrayList<Playlist> getPlaylistList()
    {
        return arrayListPlaylist;
    }

    void addPartition(Partition partition)
    {
        arrayListPartition.add(partition);
    }
    void addPlaylist(Playlist playlist)
    {
        arrayListPlaylist.add(playlist);
    }

    void savePartitionList(ArrayList<Partition> partitionList)
    {
        arrayListPartition = partitionList;
    }

    void savePlaylistList(ArrayList<Playlist> playlistList)
    {
        arrayListPlaylist = playlistList;
    }
    boolean getArtisteTitreParam(){return artisteTitreParam;}
    void setArtisteTitreParam(boolean newValue){artisteTitreParam = newValue;}
}

// TODO Add the ArrayList writing in Create menu
// How to write in the ArrayList

        /*
            PartitionActivity app = (PartitionActivity) getApplicationContext();
            ArrayList<Partition> list = app.getPartitionList();

            if (list == null)
            {
                list = new ArrayList<>();
            }

            list.add(new Partition("Artist","Title",speed,"test.pdf"));

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            Gson gson = new Gson();

            String json = gson.toJson(list);

            editor.putString("partitionList", json); // "arrayList" is only an ID, you can use "jambon" if you like as long as you use the same ID to get the data back
            editor.apply();
        */


// Code to detect a key on the keyboard
        /*
        takeKeyEvents(true);

        public boolean onKeyUp(int keyCode, KeyEvent event)
        {
            if (keyCode == KeyEvent.KEYCODE_ENTER)
            {
                System.out.println(""+keyCode);
            }

            return true;
        }
        */