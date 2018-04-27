package com.autoscroll.fraba.defiloche;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class Parcourir extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    //needed in requestForPermission()
    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public final int EXTERNAL_REQUEST = 138;

    private static final int RESULT_OK = 1;

    //TODO rendre le code propre (en block)
    int lengthOfDCIMParentDir;
    int nodeCounter;
    boolean backArrow = false;
    boolean resultOfAlertBox;
    boolean firstTimeOpened;
    boolean prevClickComesListView = true;
    boolean currentFileIsEmpty;
    boolean firstDirectory = true;
    boolean DirNameHasChanged = false;
    boolean comesFromBackArrow = false;


    ListView listViewFiles;

    ArrayAdapter<String> adapter;

    ArrayList<String> PrevArrayListFiles = new ArrayList<>();
    ArrayList<String> ArrayListRoot = new ArrayList<>();
    ArrayList<String> ArrayListFiles = new ArrayList<>();
    ArrayList<String> FilesIndex = new ArrayList<>();

    final ArrayList<File> extRootPaths = new ArrayList<>();
    final ArrayList<File> ArrayListRootFile = new ArrayList<>();

    final File DCIMDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    final File DCIMParentDir = DCIMDir.getParentFile();
    final File userDir = new File(DCIMParentDir.getAbsolutePath() + "/Défileur de partitions");
    File targetedFile;
    File[] externalSDFiles;
    File[] fichiers;
    // fichiers : {"Music", "Download", "DCIM", "Android"} for example

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parcourir_layout);
        requestForPermission();

        // Toolbar icons setup
        FrameLayout homeLayout = findViewById(R.id.homeLayout);
        final FrameLayout backLayout = findViewById(R.id.backLayout);
        homeLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToHome();
            }
        });
        backLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                backArrowFunc();
                //finish();
            }
        });

        //initialisation of the parameters
        nodeCounter = 0;
        firstTimeOpened = false;
        currentFileIsEmpty = false;
        listViewFiles = findViewById(R.id.IDFiles);
        listViewFiles.setOnItemClickListener(this);

        //---------Creating the directory where will be stored the partitions (pdf files)---------//
        if(userDir.mkdirs()) Log.e("OnCreate","Directory created");
        else Log.e("OnCreate","Directory is not created");
        //StackOverFlow | https://stackoverflow.com/questions/13507789/folder-added-in-android-not-visible-via-usb
        MediaScannerConnection.scanFile(this, new String[] {userDir.toString()}, null, null);

        // list all the files from external memory
        externalSDFiles = ContextCompat.getExternalFilesDirs(this,null);//TODO changer le nom du dossier qui stocke la mémoire SD dans l'arrayList

        //------ list all the memories available (SD cards + intern memory) in extRootPaths-------//
        //The reason for the multiple ".getParentFile()" is to go up another folder, since the original path is .../**Android**/data/YOUR_APP_PACKAGE_NAME/files/
        for(final File file : externalSDFiles) extRootPaths.add(file.getParentFile().getParentFile().getParentFile().getParentFile());


        //fill the lists of files from INTERNAL_STORAGE
        fichiers = DCIMParentDir.listFiles();
        lengthOfDCIMParentDir = fichiers.length;

        //--------------------Add the first directories in the "parcourir root"-------------------//
        ArrayListRoot.add("break"); //nedeed to come back in the previous directory
        //ArrayListRootFile.add(null);

        FilesIndex.add("Mémoire externe");
        ArrayListFiles.add("Mémoire externe");
        ArrayListRoot.add("Mémoire externe");
        //ArrayListRootFile.add();

        FilesIndex.add("Mémoire interne");
        ArrayListFiles.add("Mémoire interne");
        ArrayListRoot.add("Mémoire interne");

        ArrayListRoot.add("break"); //nedeed to come back in the previous directory

        //Refresh the ListView
        adapter = new ArrayAdapter<String>(Parcourir.this, android.R.layout.simple_expandable_list_item_1, ArrayListFiles);
        listViewFiles.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        String dirName;
        ArrayList<String> NewArrayListFiles;
        ArrayList<String> temp;
        File [] newFichiers;

        boolean fromExternalMemory = false;
        dirName = ArrayListFiles.get(position);
        NewArrayListFiles = fillListWithFiles(fichiers);

        //add the SD external card directory
        if (firstDirectory)
        {
            NewArrayListFiles.add("Mémoire externe");
            NewArrayListFiles.add("Mémoire interne");
        }

        //Select the targetedFile
        if(DirNameHasChanged)
        {
            targetedFile = new File(NewArrayListFiles.get(position));
            DirNameHasChanged = false;
        }
        /*
        else if(comesFromBackArrow)
        {
            targetedFile = new File(NewArrayListFiles.get(position));
            comesFromBackArrow = false;
        }
        */
        else
        {
            targetedFile = new File(NewArrayListFiles.get(recognizeIndex(dirName, ArrayListFiles.size())));
        }

        //------------------------------ firstDirectory in root ----------------------------------//
        if(dirName.equals("Mémoire interne"))
        {
            ArrayList<File> Arraytemp = new ArrayList<>();
            for (int i = 0; i < fichiers.length; i++)
            {
                // directory : {"Cascada.mp3", "Hello.mp3"} for example ("Music" here)
                File[] directory = fichiers[i].listFiles();
                //Displaying only directories which are not empty
                if (fichiers[i].listFiles() != null && directory.length > 0)
                {
                    String chemin = directory[0].getParentFile().getPath(); //we only need to display the directory
                    //System.out.println("in if chemin = " + chemin);
                    File tempFile = new File(chemin);
                    Arraytemp.add(tempFile);
                }
                else
                {
                    //System.out.println("in else fichiers[i].getName() = " + fichiers[i].getName());
                    Arraytemp.add(fichiers[i]);
                }
            }
            clearHiddenDir(newFichiers = Arraytemp.toArray(new File[Arraytemp.size()])); // convert an ArrayList <File> into File []
            firstDirectory = false;
        }
        else if (dirName.equals("Mémoire externe"))
        {
            fromExternalMemory = true;
            ArrayList <File> directories = new ArrayList<File>();
            for (int i = 1; i < extRootPaths.size(); i++)
            {
                directories.add(extRootPaths.get(i).getAbsoluteFile());
            }
            //newFichiers = extRootPaths.get(1).listFiles();
            newFichiers = clearHiddenDir(directories.toArray(new File[directories.size()])); // convert an ArrayList <File> into File []
            firstDirectory = false;
        }
        //------------------------ rest of the directories in root --------------------------------//
        else if(targetedFile.isDirectory())
        {
            newFichiers = clearHiddenDir(targetedFile.listFiles());
        }
        else
        {
            newFichiers = fichiers;
            displayAlertBox(targetedFile.getName());
        }
        ArrayListFiles.clear();
        FilesIndex.clear();

        //fill the ArrayListFiles which contain all the files & directories name
        temp = sortFilesByName(newFichiers, false);
        for(int i=0; i < temp.size(); i++)
        {
            // change the external SD cards Name into {carte SD n°1; carte SD n°2; ...}
            if(fromExternalMemory)
            {
                DirNameHasChanged = true;
                for(int j = 0; j < newFichiers.length; j++)
                {
                    ArrayListFiles.add("Carte SD n°" + (j+1));
                    ArrayListRoot.add(temp.get(i));
                }
            }
            else
            {
                ArrayListFiles.add(temp.get(i));
                ArrayListRoot.add(temp.get(i));
            }
        }
        ArrayListRoot.add("break");

        if(newFichiers != null && newFichiers.length > 0)
        {
            fichiers = newFichiers;
            currentFileIsEmpty = false;
        }
        else currentFileIsEmpty = true;

        prevClickComesListView = true;
        nodeCounter++;
        adapter.notifyDataSetChanged();//refresh the adapter
        //listViewFiles.invalidateViews(); //refresh the ListView
    }

    //recognizeIndex between the list of the files and the same list which is sorted
    public int recognizeIndex(String fileName, int dirLength)
    {
        for(String s : FilesIndex)
            System.out.println("FilesIndex = " + s);
        for(int i = 0 ; i < dirLength ; i++) Log.e("recognizeIndex","FilesIndex n°" + i + " : " + FilesIndex.get(i));
        for(int i = 0 ; i < dirLength ; i++)
        {
            if(fileName.equals(FilesIndex.get(i))) return i;
        }
        return -1;
    }

    public File[] clearHiddenDir(File[] inputFiles)
    {
        //init an ArrayList <File>
        ArrayList <File> fileArrayList = new ArrayList<File>();
        for (File file : inputFiles)
            if(!file.isHidden()) fileArrayList.add(file);
        return fileArrayList.toArray(new File[fileArrayList.size()]);
    }

    //Convert a list of files into an ArrayList
    public ArrayList<String> fillListWithFiles (File [] listDir)
    {
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0; i < listDir.length; i++)
        {
            result.add(listDir[i].getPath());
        }
        return result;
    }

    public ArrayList<String> sortFilesByName(File[] files, boolean callFromBackButton)
    {
        ArrayList<String> result = new ArrayList<>();
        if (files != null) {
            ArrayList<String> filePart = new ArrayList<>();
            ArrayList<String> dirPart = new ArrayList<>();

            FilesIndex.clear();

            //getting the name of each file and seperate them whether it's a directory or a file
            for (int i = 0; i < files.length; i++) {
                String path = files[i].getPath();
                String[] splitedPath = path.split("/", 0);
                //if(callFromBackButton == false)FilesIndex.add(splitedPath[splitedPath.length - 1]);
                FilesIndex.add(splitedPath[splitedPath.length - 1]);

                if (files[i].isDirectory()) dirPart.add(splitedPath[splitedPath.length - 1]);
                else filePart.add(splitedPath[splitedPath.length - 1]);
            }

            //sorting the directories/files by name. We implemented the Comparator interface and override the compare method
            //StackOverFlow | https://stackoverflow.com/questions/9109890/android-java-how-to-sort-a-list-of-objects-by-a-certain-value-within-the-object
            //sorting the directories
            Collections.sort(dirPart, new Comparator<String>() {
                public int compare(String arg0, String arg1) {
                    return arg0.compareToIgnoreCase(arg1); // To compare string values alphabetically
                }
            });
            //sorting the files
            Collections.sort(filePart, new Comparator<String>() {
                public int compare(String arg0, String arg1) {
                    return arg0.compareToIgnoreCase(arg1); // To compare string values alphabetically
                }
            });

            //Add the direcories and the files to the final result
            for (int i = 0; i < files.length; i++) {
                if (i < dirPart.size()) result.add(dirPart.get(i));
                else result.add(filePart.get(i - dirPart.size()));
            }
        }
        return result;
    }

    public void goToHome()
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void backArrowFunc()
    {
        nodeCounter--;
        if (nodeCounter < 0)
        {
            nodeCounter = 0;
            FilesIndex.clear();
            onBackPressed();
        }
        else
        {
            for(File file : fichiers)
                System.out.println("chemin fichier = " + file.getPath());

            comesFromBackArrow = true;
            //Cut the last part of ArrayListRoot
            int breakCounter = 0;
            int j = ArrayListRoot.size();
            while (breakCounter < 2) {
                if (ArrayListRoot.get(j - 1).equals("break")) breakCounter++;
                //delete the last element
                if (breakCounter < 2) ArrayListRoot.remove(j - 1);
                j--;
            }

            /*
            for(String s : ArrayListRoot)
                System.out.println("ArrayListRoot = " + s);
                */

            ArrayListFiles.clear();
            //fill the ArrayListFiles from the ArrayListRoot
            boolean fillArraylistFile = true;
            breakCounter = 0;
            for (int i = ArrayListRoot.size() - 1; i >= 0; i--)
            {
                if (ArrayListRoot.get(i).equals("break")) breakCounter++;
                if (breakCounter >= 2) fillArraylistFile = false;
                if (fillArraylistFile && !ArrayListRoot.get(i).equals("break")) ArrayListFiles.add(ArrayListRoot.get(i));
            }

            /*
            System.out.println("");
            for(String s : ArrayListFiles)
                System.out.println("  ArrayListFiles = " + s);
                */

            //sorting the directories
            Collections.sort(ArrayListFiles, new Comparator<String>() {
                public int compare(String arg0, String arg1) {
                    return arg0.compareToIgnoreCase(arg1); // To compare string values alphabetically
                }
            });

            /*
            //refresh fichiers
            ArrayList <File> prevFichiers = new ArrayList<File>();
            for (int i = 0; i < fichiers.length ; i++)
            {

            }
            */
            fichiers = clearHiddenDir(fichiers[0].getParentFile().listFiles());

            for(File file : fichiers)
                System.out.println("  chemin fichier = " + file.getPath());

            for(String s : FilesIndex)
                System.out.println("     FilesIndex = " + s);

            adapter.notifyDataSetChanged();//refresh the adapter
            /*
            System.out.println(" ");
            for(String s : ArrayListRoot)
                System.out.println("ArrayListRoot = " + s);
            */
            //for (String s : ArrayListRoot) Log.e("NavigationUp", "ArrayListRoot " + s);

            /*
            //Refresh ArrayListFile
            breakCounter = 0;
            ArrayListFiles.clear();
            int i = 0;
            int length = ArrayListRoot.size() - 1;
            //Log.e("NavigationUp", "NodeCounter = " + nodeCounter);
            //Log.e("NavigationUp", "length = " +length+" "+ ArrayListRoot.get(length - i) );
            while (breakCounter < 2)
            {
                //Log.e("NavigationUp", "avant get");
                if (ArrayListRoot.get(length - i).equals("break"))
                {
                    //Log.e("NavigationUp", "après get " + i);
                    breakCounter++;
                }
                else if (breakCounter < 2)
                {
                    ArrayListFiles.add(ArrayListRoot.get(length - i));
                    //Log.e("NavigationUp", "ArrayListRoot " + i + " " + ArrayListRoot.get(length - i));
                }
                i++;

            }*/
        }
    }

    //TODO finir de coder cette partie
    @Override
    public boolean onSupportNavigateUp()
    {
        for(String s : ArrayListRoot)
            System.out.println("ArrayListRoot = " + s);

        if (backArrow)
            {
                nodeCounter--;
            if (nodeCounter < 0)
            {
                nodeCounter = 0;
                FilesIndex.clear();
                onBackPressed();
            }
            else
                {
                //fill the ArrayListFiles from the ArrayListRoot
                boolean fillArraylistFile = true;
                int breakCounter = 0;
                for (int i = ArrayListRoot.size() - 2; i >= 0; i--) {
                    if (ArrayListRoot.get(i).equals("break")) breakCounter++;
                    if (breakCounter == 2) fillArraylistFile = false;
                    if (fillArraylistFile) ArrayListFiles.add(ArrayListRoot.get(i));
                }

                //Cut the las part of ArrayListRoot
                breakCounter = 0;
                boolean removeListRoot = true;
                ArrayList<String> temp = new ArrayList<>(ArrayListRoot.size());
                //int tempLenght = ArrayListRoot.size() - 1;
                int j = 0;
                while (breakCounter < 2) {
                    if (ArrayListRoot.get(ArrayListRoot.size() - 1).equals("break")) breakCounter++;
                    //delete the last element
                    if (breakCounter < 2) ArrayListRoot.remove(ArrayListRoot.size() - 1);
                }
                //for (String s : ArrayListRoot) Log.e("NavigationUp", "ArrayListRoot " + s);

                //Refresh ArrayListFile
                breakCounter = 0;
                ArrayListFiles.clear();
                int i = 0;
                int length;
                Log.e("NavigationUp", "NodeCounter = " + nodeCounter);
                length = ArrayListRoot.size() - 1;
                //Log.e("NavigationUp", "length = " +length+" "+ ArrayListRoot.get(length - i) );
                while (breakCounter < 2) {
                    //Log.e("NavigationUp", "avant get");
                    if (ArrayListRoot.get(length - i).equals("break")) {
                        Log.e("NavigationUp", "après get " + i);
                        breakCounter++;
                    } else if (breakCounter < 2) {
                        ArrayListFiles.add(ArrayListRoot.get(length - i));
                        Log.e("NavigationUp", "ArrayListRoot " + i + " " + ArrayListRoot.get(length - i));
                    }
                    i++;
                }
            }
        }
        else
        {
            for(String s : ArrayListRoot)
                System.out.println("ArrayListRoot = " + s);
        }
        return true;
    }

    boolean displayAlertBox(String fileName) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Voulez vous ajouter " + '"' + fileName + '"' + " à l'application ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Oui",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent prevActivityIntent = getIntent();
                        String prevAcvtivityName = prevActivityIntent.getStringExtra("PREVIOUS_ACTIVITY");
                        String extension = targetedFile.getName().substring(targetedFile.getName().lastIndexOf(".") + 1);
                        if(extension.equals("pdf") && prevAcvtivityName.equals("CHANGE_PARTITION"))
                        {
                            Log.e("Copying file","displayAlertBox(targetedFile.getName()) return " + resultOfAlertBox);
                            boolean fileExist = false;
                            File [] userFiles = userDir.listFiles();

                            //communicate the result to ChangePartition activity
                            Intent intent = new Intent();
                            intent.putExtra("RESULT_STRING", targetedFile.getPath());
                            setResult(RESULT_OK, intent);
                            finish();

                        }
                        else if (prevAcvtivityName.equals("CHANGE_SONG_PARAM"))
                        {
                            //check if the file is an audio one
                            ArrayList<String> audioExention = new ArrayList<>(Arrays.asList("mp3", "m4a", "wav","aac","flac","mid","xmf","mxmf","rttl","rtx","ota","imy","mkv","wav","ogg"));
                            boolean audioFile = false;
                            for (String s : audioExention) if(extension.equals(s)) audioFile = true;
                            if(audioFile)
                            {
                                //communicate the result to ChangePartition activity
                                Intent intent = new Intent();
                                intent.putExtra("RESULT_STRING", targetedFile.getPath());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Ce n'est pas un fichier audio", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Ce n'est pas un fichier pdf", Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Non",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Log.e("dialog"," id = " + id);
                        //resultOfAlertBox = false;
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
        Log.e("dialog"," resultOfAlertBox = " + resultOfAlertBox);
        return resultOfAlertBox;
    }

    //This code comes from StackOverflow to allow permission on the /sdcard directory
    public boolean requestForPermission()
    {
        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!canAccessExternalSd())
            {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        return isPermissionOn;
    }

    public boolean canAccessExternalSd()
    {
        return (hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm)
    {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));
    }

    public class ExternalStorage {

        public static final String SD_CARD = "sdCard";
        public static final String EXTERNAL_SD_CARD = "externalSdCard";


        //@return True if the external storage is available. False otherwise.

        public boolean isAvailable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                return true;
            }
            return false;
        }

        public String getSdCardPath() {
            return Environment.getExternalStorageDirectory().getPath() + "/";
        }



        //@return True if the external storage is writable. False otherwise.
        public boolean isWritable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            return false;

        }
    }
}

// TODO extra code from NavigationUP

//for (String s : ArrayListFiles) Log.e("NavigationUp", "ArrayListFiles " + s);
//adapter.notifyDataSetChanged();//refresh the adapter
                    /*
                    for (int i = ArrayListRoot.size() - 1 ; i > 0 ; i--)
                    {
                        Log.e("Navigation Up", "ArrayListRoot.size() = "+ ArrayListRoot.size() + " i =  " + i + " j =  " + j + "  (i - j) = " + (i-j));
                        //if (!removeListRoot) tempLenght --;
                        //if (removeListRoot)
                        //{
                        //}
                        if (ArrayListRoot.get(i - j).equals("break"))
                        {
                            Log.e("Navigation Up", "break ++");
                            breakCounter ++;
                            //removeListRoot = false;
                        }
                        ArrayListRoot.remove(i);
                        j++;
                        if (breakCounter == 2) break;
                    }
                    for (String s: ArrayListRoot)
                    {
                        Log.e("NavigationUp", "ArrayListRoot " + s);
                    }
                    */
                    /*
                    prevClickComesListView = false;
                    firstTimeOpened = false;

                    File [] parent;
                    if (currentFileIsEmpty) parent = fichiers[0].getParentFile().listFiles();
                    else parent = fichiers[0].getParentFile().getParentFile().listFiles();
                    //if(fichiers.length > 0) parent = fichiers[0].getParentFile().getParentFile().listFiles();
                    ArrayList<String> actualTemp;
                    ArrayList<String> prevTemp;
                    actualTemp = sortFilesByName(parent, true);
                    prevTemp = sortFilesByName(fichiers, true);
                    //PrevArrayListFiles
                    FilesIndex.clear();
                    ArrayListFiles.clear();
                    PrevArrayListFiles.clear();
                    //if(actualTemp.size() > 0 ) Log.i("Navigation", "Je remplis la liste !");
                    for (int i = 0; i < actualTemp.size(); i++)
                    {
                        ArrayListFiles.add(actualTemp.get(i));
                        FilesIndex.add(actualTemp.get(i));
                        //Log.i("Navigation", "ArrayListFiles " + i + " " + ArrayListFiles.get(i));
                    }

                    for (int i = 0; i < prevTemp.size(); i++)
                    {
                        PrevArrayListFiles.add(prevTemp.get(i));
                        //FilesIndex.add(prevTemp.get(i));
                        //Log.e("Navigation", "PrevArrayListFiles " + i + " " + PrevArrayListFiles.get(i));
                    }
                    //for (int i = 0; i < parent.length; i++) Log.i("Navigation", "parent " + i + " " + parent[i].getName());
                    fichiers = parent;
                    adapter.notifyDataSetChanged();//refresh the adapter
                    //listViewFiles.invalidateViews();
                    */