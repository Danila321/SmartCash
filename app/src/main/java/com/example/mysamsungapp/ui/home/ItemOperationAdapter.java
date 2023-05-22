package com.example.mysamsungapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.mysamsungapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ItemOperationAdapter extends ArrayAdapter<ItemOperation> {
    public ItemOperationAdapter(@NonNull Context context, ArrayList<ItemOperation> items) {
        super(context, R.layout.home_operations_item, items);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_operations_item, null);
        }

        TextView date = convertView.findViewById(R.id.operationDate);
        ImageView image = convertView.findViewById(R.id.operationImage);
        TextView description = convertView.findViewById(R.id.operationDescription);
        TextView amount = convertView.findViewById(R.id.operationAmount);
        ImageButton edit = convertView.findViewById(R.id.operationEdit);
        ImageButton delete = convertView.findViewById(R.id.operationDelete);

        ItemOperation item = getItem(position);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date inputDate;
        try {
            inputDate = inputFormat.parse(item.date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy г.", new Locale("ru"));
        String outputDate = outputFormat.format(Objects.requireNonNull(inputDate));
        date.setText(outputDate);

        image.setImageResource(item.image);
        description.setText(item.description);
        String amountText = item.amount + " руб.";
        amount.setText(amountText);

        edit.setOnClickListener(v -> {
            EditOperationDialog dialog = EditOperationDialog.newInstance(item.id, item.amount, item.category, item.date, item.description);
            FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
            dialog.show(fragmentManager, "editItem");
        });

        delete.setOnClickListener(v -> {
            DeleteOperationDialog dialog = DeleteOperationDialog.newInstance(item.id);
            FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
            dialog.show(fragmentManager, "deleteItem");
        });

        return convertView;
    }
}

