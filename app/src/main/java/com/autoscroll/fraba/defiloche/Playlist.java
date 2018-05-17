package com.autoscroll.fraba.defiloche;

import java.util.ArrayList;

public class Playlist
{
    private ArrayList<Partition> arrayListPlaylist = new ArrayList<>();
    private String namePlaylist = "newPlaylist";

    Playlist()
    {
    }

    Playlist(String name)
    {
        namePlaylist = name;
    }

    Playlist(String name, ArrayList<Partition> arrayList)
    {
        setName(name);
        setPlaylist(arrayList);
    }

    public void setName(String name)
    {
        namePlaylist = name;
    }
    public void setPlaylist(ArrayList<Partition> arrayList)
    {
        arrayListPlaylist = arrayList;
    }
    public void setPartition(int idSong, Partition partition)
    {
        arrayListPlaylist.set(idSong,partition);
    }
    public void addPartition(Partition partition)
    {
        arrayListPlaylist.add(partition);
    }
    public String getName()
    {
        return namePlaylist;
    }
    public ArrayList<Partition> getPartitionList()
    {
        return arrayListPlaylist;
    }
}
