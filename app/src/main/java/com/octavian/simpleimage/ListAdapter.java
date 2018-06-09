package com.octavian.simpleimage;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ListAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> name;
    private ArrayList<String> link;

    public ListAdapter(@NonNull Context context, ArrayList<String> name, ArrayList<String> link) {
        super(context, R.layout.list_item, name);

        this.context = context;
        this.name = name;
        this.link = link;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = inflater.inflate(R.layout.list_item, parent, false);

        }
        TextView tv = view.findViewById(R.id.list_text);
        ImageButton im = view.findViewById(R.id.list_image);
        im.setTag(link.get(position));
        tv.setText(name.get(position));
        Picasso.get().load(link.get(position)).into(im);
        return view;
    }
}
