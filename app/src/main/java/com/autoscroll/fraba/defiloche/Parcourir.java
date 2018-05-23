package com.autoscroll.fraba.defiloche;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

public class Parcourir extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    //needed in requestForPermission()
    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public final int EXTERNAL_REQUEST = 138;

    private static final int RESULT_OK = 1;

    //TODO rendre le code propre (en block)
    int lengthOfDCIMParentDir;
    int nodeCounter;
    boolean resultOfAlertBox;
    boolean firstTimeOpened;
    boolean prevClickComesListView = true;
    boolean firstDirectory = true;
    boolean DirNameHasChanged = false;
    boolean comesFromBackArrow = false;
    boolean fromExternalMemory = false;
    boolean lockIt = false;



    ListView listViewFiles;

    ArrayList<Integer> fileColor = new ArrayList<Integer>();
    ArrayList<Integer> fileIcon = new ArrayList<Integer>();
    ArrayList<String> fileName;
    ArrayAdapter<String> adapter;
    ArrayList<String> ArrayListRoot = new ArrayList<>();
    ArrayList<String> ArrayListFiles = new ArrayList<>();
    ArrayList<String> FilesIndex = new ArrayList<>();

    final ArrayList<File> extRootPaths = new ArrayList<>();

    final File DCIMDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    final File DCIMParentDir = DCIMDir.getParentFile();
    final File userDir = new File(DCIMParentDir.getAbsolutePath() + "/Lecteur de partition");
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
        onCreateInit();
    }

    public void onCreateInit()
    {
        fromExternalMemory = false;
        lockIt = false;

        //fill the lists of files from INTERNAL_STORAGE
        fichiers = DCIMParentDir.listFiles();
        lengthOfDCIMParentDir = fichiers.length;

        //--------------------Add the first directories in the "parcourir root"-------------------//
        ArrayListRoot.clear();
        FilesIndex.clear();
        ArrayListFiles.clear();

        ArrayListRoot.add("break"); //nedeed to come back in the previous directory

        if(extRootPaths.size() > 1)
        {
            FilesIndex.add("Mémoire externe");
            ArrayListFiles.add("Mémoire externe");
            ArrayListRoot.add("Mémoire externe");
        }

        FilesIndex.add("Mémoire interne");
        ArrayListFiles.add("Mémoire interne");
        ArrayListRoot.add("Mémoire interne");

        ArrayListRoot.add("break"); //nedeed to come back in the previous directory

        //Refresh the ListView
        fileIcon.add(R.mipmap.file_arrow);
        fileIcon.add(R.mipmap.file_arrow);
        fileColor.add(getResources().getColor(R.color.cyan));//define the color of the two main directories
        fileColor.add(getResources().getColor(R.color.cyan));
        fileName = ArrayListFiles;
        adapter = new FileAdapter(this, fileName, fileIcon, fileColor);
        listViewFiles.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        nodeCounter++;

        String dirName;
        ArrayList<String> NewArrayListFiles;
        ArrayList<String> temp;
        File [] newFichiers;
        dirName = ArrayListFiles.get(position);
        NewArrayListFiles = fillListWithFiles(fichiers);
        boolean nothingToChange = false;

        //add the SD external card directory
        if (firstDirectory)
        {
            if(extRootPaths.size() > 1) NewArrayListFiles.add("Mémoire externe");
            NewArrayListFiles.add("Mémoire interne");
        }

        //Select the targetedFile
        if(DirNameHasChanged)
        {
            targetedFile = new File(NewArrayListFiles.get(position));
            DirNameHasChanged = false;
        }

        else if(comesFromBackArrow)
        {
            //find the correct path
            String path;
            String result = "Le fichier n'est pas dans ArrayListRoot";
            for(int i = 0 ; i < ArrayListRoot.size() ; i++)
            {
                path = ArrayListRoot.get(i);
                String[] splitedPath = path.split("/", 0);
                String fileName = splitedPath[splitedPath.length - 1];
                if(ArrayListFiles.get(position).equals(fileName)) result = path;
            }
            targetedFile = new File(result);
            comesFromBackArrow = false;
        }
        else
        {
            targetedFile = new File(NewArrayListFiles.get(recognizeIndex(dirName, ArrayListFiles.size() )));
        }

        //------------------------------ firstDirectory in root ----------------------------------//
        if(dirName.equals("Mémoire interne"))
        {
            ArrayList<File> Arraytemp = new ArrayList<>();
            for (int i = 0; i < fichiers.length; i++)
            {
                // directory : {"Cascada.mp3", "Hello.mp3"} for example ("Music" here)
                File[] directories = fichiers[i].listFiles();
                //Displaying only directories which are not empty
                if (fichiers[i].listFiles() != null && directories.length > 0)
                {
                    String chemin = directories[0].getParentFile().getPath(); //we only need to display the directory
                    File tempFile = new File(chemin);
                    Arraytemp.add(tempFile);
                }
                else
                {
                    Arraytemp.add(fichiers[i]);
                }
            }
            newFichiers = Arraytemp.toArray(new File[Arraytemp.size()]); // convert an ArrayList <File> into File []
            //clearHiddenDir(newFichiers = Arraytemp.toArray(new File[Arraytemp.size()])); // convert an ArrayList <File> into File []
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
            //newFichiers = clearHiddenDir(directories.toArray(new File[directories.size()])); // convert an ArrayList <File> into File []
            newFichiers = directories.toArray(new File[directories.size()]); // convert an ArrayList <File> into File []
            firstDirectory = false;
        }
        //------------------------ rest of the directories in root --------------------------------//
        else if(targetedFile.isDirectory())
        {
            //newFichiers = clearHiddenDir(targetedFile.listFiles());
            newFichiers = targetedFile.listFiles();
        }
        else if (targetedFile.isFile())
        {
            String extension = targetedFile.getName().substring(targetedFile.getName().lastIndexOf(".") + 1);
            //check if the file is an audio one
            ArrayList<String> audioExention = new ArrayList<>(Arrays.asList("mp3", "m4a", "wav","aac","flac","mid","xmf","mxmf","rttl","rtx","ota","imy","mkv","wav","ogg"));
            boolean audioFile = false;
            for (String s : audioExention) if(extension.equals(s)) audioFile = true;

            //display the alert box if the fil is correct
            if(extension.equals("pdf"))
            {
                Intent prevActivityIntent = getIntent();
                //String test = prevActivityIntent.getStringExtra("TEST");
                int comes_from_create = prevActivityIntent.getIntExtra("COMES_FROM_CREATE",100);

                //displayAlertBox(targetedFile.getName());
                //communicate the result to ChangePartition activity
                Intent intent = new Intent();
                intent.putExtra("RESULT_STRING", targetedFile.getPath());

                //if we want to edit a partition
                if(comes_from_create == -1)
                {
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    //check if the file is in the "Lecteur de partition" directory
                    boolean fileInDirectory = false;
                    File[] userFiles = userDir.listFiles();
                    for (int i = 0; i < userFiles.length; i++)
                    {
                        if (targetedFile.getName().equals(userFiles[i].getName()))
                        {
                            fileInDirectory = true;
                        }
                    }

                    if(!fileInDirectory)
                    {
                        Toast.makeText(getApplicationContext(), "Ce fichier n'est pas dans le dossier [Lecteur de partition]", Toast.LENGTH_SHORT).show();
                        nothingToChange = true;
                        nodeCounter--;
                    }
                    else
                    {
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Le fichier sélectionné n'est pas un pdf", Toast.LENGTH_SHORT).show();
                nothingToChange = true;
                nodeCounter--;
            }
            //TODO rajouter l'option : fichier audio
            newFichiers = fichiers;
        }
        else
        {
            System.out.println("error, targetedFile is not a file");
            newFichiers = null;
            nodeCounter--;
        }
        ArrayListFiles.clear();
        FilesIndex.clear();

        //fill the ArrayListFiles which contain all the files & directories name
        temp = sortFilesByName(newFichiers, false);
        ArrayList<String> tempRoot = sortFilesByName(newFichiers, true);
        for(int i=0; i < temp.size(); i++)
        {
            // change the external SD cards Name into {carte SD n°1; carte SD n°2; ...}
            if(fromExternalMemory && !lockIt)
            {
                cutLastPartOfListRoot(); // cut the last part of arrayListRoot
                DirNameHasChanged = true;
                for(int j = 0; j < newFichiers.length; j++)
                {
                    ArrayListFiles.add("Carte SD n°" + (j+1));
                    ArrayListRoot.add(tempRoot.get(i));
                }
                lockIt = true;
            }
            else
            {
                ArrayListFiles.add(temp.get(i));
                if(!nothingToChange) ArrayListRoot.add(tempRoot.get(i)); //because we initialized ArrayListRoot on the OnCreate
            }
        }
        if(!nothingToChange)ArrayListRoot.add("break"); //because we initialized ArrayListRoot on the OnCreate

        if(newFichiers != null && newFichiers.length > 0)
        {
            fichiers = newFichiers;
        }

        prevClickComesListView = true;
        fileName = ArrayListFiles;
        adapter = new FileAdapter(this, fileName, fileIcon, fileColor);
        listViewFiles.setAdapter(adapter);
    }

    //recognizeIndex between the list of the files and the same list which is sorted
    public int recognizeIndex(String fileName, int dirLength)
    {
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

    public ArrayList<String> sortFilesByName(File[] files, boolean addFilePath)
    {
        ArrayList<String> result = new ArrayList<>();
        if (files != null)
        {
            ArrayList<String> filePart = new ArrayList<>();
            ArrayList<String> dirPart = new ArrayList<>();

            FilesIndex.clear();
            fileIcon.clear();
            fileName.clear();
            fileColor.clear();

            //getting the name of each file and seperate them whether it's a directory or a file
            for (int i = 0; i < files.length; i++)
            {
                String path = files[i].getPath();
                String[] splitedPath = path.split("/", 0);
                FilesIndex.add(splitedPath[splitedPath.length - 1]);

                if (files[i].isDirectory())
                {
                    if(!addFilePath) dirPart.add(splitedPath[splitedPath.length - 1]);
                    else dirPart.add(path);
                }
                else
                {
                    if(!addFilePath) filePart.add(splitedPath[splitedPath.length - 1]);
                    else filePart.add(path);
                }
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
            Collections.sort(filePart, new Comparator<String>()
            {
                public int compare(String arg0, String arg1)
                {

                    return arg0.compareToIgnoreCase(arg1); // To compare string values alphabetically
                }
            });

            //Add the directories and the files to the final result
            for (int i = 0; i < files.length; i++) {
                if (i < dirPart.size())
                {
                    result.add(dirPart.get(i));
                    fileIcon.add(R.mipmap.file_arrow);
                    fileColor.add(getResources().getColor(R.color.cyan));
                }
                else
                {
                    result.add(filePart.get(i - dirPart.size()));
                    fileIcon.add(R.mipmap.empty);
                    fileColor.add(Color.BLACK);
                }
            }
        }
        else System.out.println("files = null");
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
        comesFromBackArrow = true;

        if(nodeCounter == 0)
        {
            onCreateInit();
        }
        else if(nodeCounter == 1 && fromExternalMemory)
        {
            firstDirectory = false;

            ArrayList <File> sdcards = new ArrayList<File>();
            //The reason for the multiple ".getParentFile()" is to go up another folder, since the original path is .../**Android**/data/YOUR_APP_PACKAGE_NAME/files/
            for(final File file : externalSDFiles) sdcards.add(file.getParentFile().getParentFile().getParentFile().getParentFile());

            //target the external memory
            ArrayList <File> directories = new ArrayList<File>();
            for (int i = 1; i < sdcards.size(); i++)
            {
                directories.add(sdcards.get(i).getAbsoluteFile());
            }
            File [] newFichiers = directories.toArray(new File[directories.size()]); // convert an ArrayList <File> into File []

            //fill the lists with all the external memories
            ArrayListFiles.clear();
            FilesIndex.clear();
            ArrayList<String> temp = sortFilesByName(newFichiers, false);
            ArrayList<String> tempRoot = sortFilesByName(newFichiers, true);
            cutLastPartOfListRoot();//Cut the last part of ArrayListRoot
            DirNameHasChanged = true;
            for(int i=0; i < temp.size(); i++) {
                for (int j = 0; j < newFichiers.length; j++) {
                    ArrayListFiles.add("Carte SD n°" + (j + 1));
                }
            }
            fichiers = newFichiers;
            fileName = ArrayListFiles;
            adapter = new FileAdapter(this, fileName, fileIcon, fileColor);
            listViewFiles.setAdapter(adapter);
        }
        else
        {
            if (nodeCounter < 0)
            {
                nodeCounter = 0;
                FilesIndex.clear();
                onBackPressed();
            }
            else
            {
                comesFromBackArrow = true;
                cutLastPartOfListRoot();//Cut the last part of ArrayListRoot

                for(String s : ArrayListRoot)
                    System.out.println("ArrayListRoot = " + s);

                //fill the ArrayListFiles from the ArrayListRoot
                ArrayList<String> pathToFiles = new ArrayList<>();
                ArrayListFiles.clear();
                FilesIndex.clear();
                boolean fillArraylistFile = true;
                int breakCounter = 0;
                for (int i = ArrayListRoot.size() - 1; i >= 0; i--)
                {
                    if (ArrayListRoot.get(i).equals("break")) breakCounter++;
                    if (breakCounter >= 2) fillArraylistFile = false;
                    if (fillArraylistFile && !ArrayListRoot.get(i).equals("break"))
                    {
                        pathToFiles.add(ArrayListRoot.get(i));
                        String path = ArrayListRoot.get(i);
                        String[] splitedPath = path.split("/", 0);

                        ArrayListFiles.add(splitedPath[splitedPath.length - 1]);
                        FilesIndex.add(splitedPath[splitedPath.length - 1]);
                    }
                    fileName = ArrayListFiles;
                    adapter = new FileAdapter(this, fileName, fileIcon, fileColor);
                    listViewFiles.setAdapter(adapter);
                }

                //sort the files
                ArrayList<File> arrayFilesToSort = new ArrayList<File>();
                int i = 0;
                for (String s : pathToFiles) {
                    arrayFilesToSort.add(new File(s));
                    System.out.println("pathToFiles = " + s);
                    i++;
                }

                File[] filesToSort = arrayFilesToSort.toArray(new File[arrayFilesToSort.size()]); // convert an ArrayList <File> into File []
                ArrayListFiles = sortFilesByName(filesToSort, false);
                if (ArrayListFiles.get(0).equals("Mémoire externe") && nodeCounter == 0) {

                    //define the icon next to the two main directories
                    fileIcon.clear();
                    fileIcon.add(R.mipmap.file_arrow);
                    fileIcon.add(R.mipmap.file_arrow);

                    //define the color of the two main directories
                    fileColor.clear();
                    fileColor.add(getResources().getColor(R.color.cyan));
                    fileColor.add(getResources().getColor(R.color.cyan));
                }

                fichiers = fichiers[0].getParentFile().listFiles();
                //Refresh the ListView
                fileName = ArrayListFiles;
                adapter = new FileAdapter(this, fileName, fileIcon, fileColor);
                listViewFiles.setAdapter(adapter);
            }
        }
    }

    void cutLastPartOfListRoot()
    {
        int breakCounter = 0;
        int k = ArrayListRoot.size();
        while (breakCounter < 2) {
            if (ArrayListRoot.get(k - 1).equals("break")) breakCounter++;
            //delete the last element
            if (breakCounter < 2) ArrayListRoot.remove(k - 1);
            k--;
        }
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
                        String creationChoice = prevActivityIntent.getStringExtra("CREATTION_CHOICE");
                        String extension = targetedFile.getName().substring(targetedFile.getName().lastIndexOf(".") + 1);
                        if(extension.equals("pdf") && prevAcvtivityName.equals("CHANGE_PARTITION"))
                        {
                            //Log.e("Copying file","displayAlertBox(targetedFile.getName()) return " + resultOfAlertBox);
                            boolean fileExist = false;
                            File [] userFiles = userDir.listFiles();

                            //communicate the result to ChangePartition activity
                            Intent intent = new Intent();
                            intent.putExtra("RESULT_STRING", targetedFile.getPath());
                            if(creationChoice.equals("CREATION")) setResult(RESULT_OK, intent);
                            else setResult(RESULT_OK, intent);
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
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
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