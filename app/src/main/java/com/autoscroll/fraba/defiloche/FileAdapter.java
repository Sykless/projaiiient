package com.autoscroll.fraba.defiloche;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FileAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemname;
    private final ArrayList<Integer> imgid;
    private final ArrayList<Integer> textColor;

    public FileAdapter(Activity context, ArrayList<String> itemname, ArrayList<Integer> imgid, ArrayList<Integer> textColor) {
        super(context, R.layout.file_adapter_layout, itemname);
        // Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.textColor=textColor;
    }

    public View getView(int position,View view,ViewGroup parent)
    {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.file_adapter_layout, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        //TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        //System.out.println("(filtreAdapter) position = " + position);
        txtTitle.setText(itemname.get(position));
        txtTitle.setTextColor(textColor.get(position));
        imageView.setImageResource(imgid.get(position));
        if(imgid.get(position) == R.mipmap.file_arrow)
            imageView.setColorFilter(Color.parseColor("#00B0F0"));
        //extratxt.setText("Description "+ itemname[position]);
        return rowView;

    };
}