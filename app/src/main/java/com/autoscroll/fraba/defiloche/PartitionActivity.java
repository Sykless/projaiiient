package com.autoscroll.fraba.defiloche;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.lang.reflect.Type;

public class PartitionActivity extends Application
{
    ArrayList<Partition> arrayListPartition = new ArrayList<Partition>();

    @Override
    public void onCreate()
    {
        super.onCreate();
        System.out.println("Open app");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("arrayList", null);
        Type type = new TypeToken<ArrayList<Partition>>() {}.getType();
        arrayListPartition = gson.fromJson(json, type);

        if (arrayListPartition != null)
        {
            System.out.println(arrayListPartition.get(0).getArtist() + " - " + arrayListPartition.get(0).getTitle());
        }
        else
        {
            System.out.println("ArrayList vide !"); // TODO Maybe add a link to Create if there isn't any partition
        }

        // Here's the code to write the ArrayList
        // TODO Add the ArrayList writing in Create menu

        /*
        ArrayList<Partition> list = new ArrayList<Partition>();

        list.add(new Partition("The Resilient","Betraying the Martyrs","test.pdf"));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(list);

        editor.putString("arrayList", json); -- "arrayList" is only an ID, you can use "jambon" if you like as long as you use the same ID to get the data back
        editor.apply();
        */
    }

    ArrayList<Partition> getPartitionList()
    {
        return arrayListPartition;
    }
}
