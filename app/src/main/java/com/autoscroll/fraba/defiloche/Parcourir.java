package com.autoscroll.fraba.defiloche;

import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Parcourir extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    ListView listViewFiles;
    ArrayAdapter<String> adapter;
    ArrayList<String> ArrayListFiles = new ArrayList<>();
    ArrayList<String> FilesIndex = new ArrayList<>();
    private static final int CODE_MY_ROOT = 1;
    //final File userFile = Environment.getExternalStorageDirectory();
    final File DCIMDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    final File DCIMParentDir = DCIMDir.getParentFile();

    // fichiers : {"Music", "Download", "DCIM", "Android"} for example
    File[] fichiers;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parcourir_layout);
        fichiers = DCIMParentDir.listFiles();
        listViewFiles = (ListView) findViewById(R.id.IDFiles);
        listViewFiles.setOnItemClickListener(this);

        File userDir = new File(DCIMParentDir.getAbsolutePath() + "/DepuisAndroid");
        if(userDir.mkdirs()) Log.e("MyRoot","Directory created");
        else Log.e("MyRoot","Directory is not created");
        //StackOverFlow | https://stackoverflow.com/questions/13507789/folder-added-in-android-not-visible-via-usb
        MediaScannerConnection.scanFile(this, new String[] {userDir.toString()}, null, null);

        for (int i = 0; i < fichiers.length; i++)
        {
            // directory : {"Cascada.mp3", "Hello.mp3"} for example ("Music" here)
            File[] directory = fichiers[i].listFiles();
            if (fichiers[i].listFiles() != null && directory.length > 0) //Displaying only directories which are not empty
            {
                String chemin = directory[0].getPath(); //we only need to display the directory
                //splting the path /sdcard/Music/Cascada.mp3 => { "", "sdcard", "Music", "Cascada.mp3 }
                String [] dirName = chemin.split("/", 0);
                FilesIndex.add(dirName[dirName.length - 2]); //Keeping the order to recognize the index of files in the "onItemClick" listener
                ArrayListFiles.add(dirName[dirName.length - 2]); // Displaying "Music" here
            }
            else
            {
                FilesIndex.add(fichiers[i].getName());
                ArrayListFiles.add(fichiers[i].getName());
            }
        }

        //sorting the directories/files by name. We implemented the Comparator interface and override the compare method
        //StackOverFlow | https://stackoverflow.com/questions/9109890/android-java-how-to-sort-a-list-of-objects-by-a-certain-value-within-the-object
        Collections.sort(ArrayListFiles, new Comparator<String>()
        {
            public int compare(String arg0, String arg1)
            {
                return arg0.compareToIgnoreCase(arg1); // To compare string values alphabetically
            }
        });

        adapter = new ArrayAdapter<String>(Parcourir.this, android.R.layout.simple_expandable_list_item_1, ArrayListFiles);
        listViewFiles.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        String dirName = ArrayListFiles.get(position);
        ArrayList<String> NewArrayListFiles;
        ArrayList<String> temp;
        File [] newFichiers;

        NewArrayListFiles = cleanEmptyDirectories(fichiers);
        //for(int i = 0; i < NewArrayListFiles.size(); i++) Log.e("OnItemClick","NewArrayListFiles " + i + " " + NewArrayListFiles.get(i));
        newFichiers = new File(NewArrayListFiles.get(recognizeIndex(dirName, ArrayListFiles.size()))).listFiles();//take all the files from the directory we taped
        //for(int i = 0; i < newFichiers.length; i++) Log.e("onItemClick", "fichier n°1 = " + newFichiers[i].getName());

        ArrayListFiles.clear();
        FilesIndex.clear();
        temp = sortFilesByName(newFichiers);
        for(int i=0; i < temp.size(); i++) ArrayListFiles.add(temp.get(i));
        //for(int i=0; i < ArrayListFiles.size(); i++) Log.e("sortByName", "ArrayListFiles " + i + " " + ArrayListFiles.get(i));
        fichiers = newFichiers;
        adapter.notifyDataSetChanged();//refresh the adapter
        //listViewFiles.invalidateViews(); //refresh the ListView
    }

    public int recognizeIndex(String fileName, int dirLength)
    {
        for(int i = 0 ; i < dirLength ; i++)
        {
            //Log.e("recognizeIndex", "FilesIndex.get(" + i + ")" + " = " + FilesIndex.get(i) + "  " + fileName);
            if(fileName.equals(FilesIndex.get(i))) return i;
        }
        return -1;
    }

    public ArrayList<String> cleanEmptyDirectories (File [] listDir)
    {
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0; i < listDir.length; i++)
        {
            result.add(listDir[i].getPath());
            /*if(listDir[i].list() != null && listDir[i].list().length > 0)//if the the directory isn't empty
            {
                result.add(listDir[i].getPath());
            }*/
        }
        return result;
    }

    public ArrayList<String> sortFilesByName(File[] files)
    {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> filePart = new ArrayList<>();
        ArrayList<String> dirPart = new ArrayList<>();
        //getting the name of each file and seperate them wether it's a directory or a file
        //Log.e("sortFilesByName", "files.length = " + files.length);
        int counter = 0;
        for(int i = 0; i < files.length; i++)
        {
            String path = files[i].getPath();
            String[] splitedPath = path.split("/", 0);
            FilesIndex.add(splitedPath[splitedPath.length - 1]);

            //if(files[i].isDirectory() && files[i].list() != null && files[i].list().length > 0) //if the the directory isn't empty
            //{
            if (files[i].isDirectory()) dirPart.add(splitedPath[splitedPath.length - 1]);

            else filePart.add(splitedPath[splitedPath.length - 1]);
            //}
            //else if(!files[i].isDirectory()) filePart.add(splitedPath[splitedPath.length - 1]);
            //else counter++;
        }
        //sorting the directories
        Collections.sort(dirPart, new Comparator<String>()
        {
            public int compare(String arg0, String arg1)
            {
                return arg0.compareToIgnoreCase(arg1); // To compare string values alphabetically
            }
        });
        //sorting the files
        Collections.sort(filePart, new Comparator<String>()
        {
            public int compare(String arg0, String arg1)
            {
                return arg0.compareToIgnoreCase(arg1); // To compare string values alphabetically
            }
        });

        for(int i=0; i < files.length; i++)
        {
            //Log.e("sortFilesByName", "files.length - counter = " + (files.length - counter)  + "  dirPart.size() = " + dirPart.size() + " filePart.size() " + filePart.size());
            if(i < dirPart.size()) result.add(dirPart.get(i));
            else result.add(filePart.get(i - dirPart.size()));
        }

        //for(int i=0; i < result.size(); i++) Log.e("sortByName", "result " + i + " " + result.get(i));
        return result;
    }
}
