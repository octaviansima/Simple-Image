package com.octavian.simpleimage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GridAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> name;
    private ArrayList<String> link;

    public GridAdapter(@NonNull Context context, ArrayList<String> name, ArrayList<String> link) {
        super(context, R.layout.grid_item, name);

        this.context = context;
        this.name = name;
        this.link = link;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = inflater.inflate(R.layout.grid_item, parent, false);

        }
        TextView tv = view.findViewById(R.id.grid_text);
        ImageButton im = view.findViewById(R.id.grid_image);
        im.setTag(R.id.individual_image, link.get(position));
        tv.setText(name.get(position));
        Glide.with(context).load(link.get(position)).into(im);
        return view;
    }
}
