package com.example.mysamsungapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mysamsungapp.R;

import java.util.ArrayList;

public class ItemCategoryAdapter extends ArrayAdapter<ItemCategory> {
    public ItemCategoryAdapter(Context context, ArrayList<ItemCategory> arr) {
        super(context, R.layout.home_category_item, arr);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_category_item, null);
        }

        ImageView image = convertView.findViewById(R.id.categoryImage);
        TextView category = convertView.findViewById(R.id.categoryName);
        TextView amount = convertView.findViewById(R.id.categoryAmount);

        ItemCategory item = getItem(position);

        image.setImageResource(item.image);
        category.setText(item.category);
        amount.setText(item.amount + " руб.");

        return convertView;
    }
}