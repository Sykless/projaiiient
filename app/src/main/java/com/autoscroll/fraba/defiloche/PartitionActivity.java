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
            System.out.println("ArrayList vide !");
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

            editor.putString("arrayList", json); // "arrayList" is only an ID, you can use "jambon" if you like as long as you use the same ID to get the data back
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
    }

    ArrayList<Partition> getPartitionList()
    {
        return arrayListPartition;
    }
    void addPartition(Partition partition)
    {
        arrayListPartition.add(partition);
    }
    void savePartitionList(ArrayList<Partition> partitionList)
    {
        arrayListPartition = partitionList;
    }
}
