package com.example.mysamsungapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mysamsungapp.R;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<Integer> images;
    ArrayList<String> categories;
    boolean type;

    public SpinnerAdapter(Context context, ArrayList<Integer> images, ArrayList<String> categories, boolean type) {
        this.context = context;
        this.images = images;
        this.categories = categories;
        this.type = type;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (type){
            convertView = inflater.inflate(R.layout.custom_spinner_item, null);
        } else {
            convertView = inflater.inflate(R.layout.custom_spinner_item_small, null);
        }
        ImageView icon = convertView.findViewById(R.id.imageViewSpinner);
        TextView names = convertView.findViewById(R.id.textViewSpinner);
        icon.setImageResource(images.get(position));
        names.setText(categories.get(position));
        return convertView;
    }
}
