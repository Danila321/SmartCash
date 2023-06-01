package com.example.mysamsungapp.ui.categories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.mysamsungapp.R;

import java.util.ArrayList;

public class ItemCategoriesAdapter extends ArrayAdapter<ItemCategories> {
    FragmentManager fm;
    int type;
    int[] imagesExpenses;
    int[] imagesIncomes;

    public ItemCategoriesAdapter(@NonNull Context context, ArrayList<ItemCategories> items, FragmentManager fm, int type, int[] imagesExpenses, int[] imagesIncomes) {
        super(context, R.layout.categories_custom_item, items);
        this.fm = fm;
        this.type = type;
        Log.i("TYPE", String.valueOf(type));
        this.imagesExpenses = imagesExpenses;
        this.imagesIncomes = imagesIncomes;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.categories_custom_item, null);
        }

        TextView textViewCategory1 = convertView.findViewById(R.id.textViewCategory1);
        TextView textViewCategory2 = convertView.findViewById(R.id.textViewCategory2);
        ImageButton edit1 = convertView.findViewById(R.id.categoryEdit1);
        ImageButton edit2 = convertView.findViewById(R.id.categoryEdit2);
        ImageButton delete1 = convertView.findViewById(R.id.categoryDelete1);
        ImageButton delete2 = convertView.findViewById(R.id.categoryDelete2);
        ImageView imageViewCategory1 = convertView.findViewById(R.id.imageViewCategory1);
        ImageView imageViewCategory2 = convertView.findViewById(R.id.imageViewCategory2);
        ConstraintLayout item2 = convertView.findViewById(R.id.constraintLayout4);

        ItemCategories item = getItem(position);

        edit1.setOnClickListener(v -> {
            EditCategoryDialog dialog = EditCategoryDialog.newInstance(item.id, item.name, item.image, type, imagesExpenses, imagesIncomes);
            dialog.show(fm, "editItem");
        });

        edit2.setOnClickListener(v -> {
            EditCategoryDialog dialog = EditCategoryDialog.newInstance(item.id2, item.name2, item.image2, type, imagesExpenses, imagesIncomes);
            dialog.show(fm, "editItem");
        });

        delete1.setOnClickListener(v -> {
            DeleteCategoryDialog dialog = DeleteCategoryDialog.newInstance(item.id);
            dialog.show(fm, "deleteItem");
        });

        delete2.setOnClickListener(v -> {
            DeleteCategoryDialog dialog = DeleteCategoryDialog.newInstance(item.id2);
            dialog.show(fm, "deleteItem");
        });

        textViewCategory1.setText(item.name);
        imageViewCategory1.setImageResource(item.image);
        if (!item.name2.equals("")) {
            textViewCategory2.setText(item.name2);
            imageViewCategory2.setImageResource(item.image2);
            item2.setVisibility(View.VISIBLE);
        } else {
            item2.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
