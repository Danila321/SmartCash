package com.example.mysamsungapp.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mysamsungapp.DBHelper;
import com.example.mysamsungapp.R;
import com.example.mysamsungapp.ui.SpinnerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class EditOperationDialog extends DialogFragment {
    ArrayList<String> expensesCategories = new ArrayList<>();
    ArrayList<String> incomeCategories = new ArrayList<>();
    ArrayList<Integer> expensesImages = new ArrayList<>();
    ArrayList<Integer> incomeImages = new ArrayList<>();
    private int id, amount;
    String category, date, description;

    public static EditOperationDialog newInstance(int id, int amount, String category, String date, String description) {
        EditOperationDialog dialog = new EditOperationDialog();
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putInt("amount", amount);
        args.putString("category", category);
        args.putString("date", date);
        args.putString("description", description);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("id");
            amount = getArguments().getInt("amount");
            category = getArguments().getString("category");
            date = getArguments().getString("date");
            description = getArguments().getString("description");
        }
    }

    @SuppressLint("Range")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.home_dialog_fragment, null);
        builder.setView(dialogView);

        TextView editTitle = dialogView.findViewById(R.id.dialog_title);
        EditText editAmount = dialogView.findViewById(R.id.dialog_amount);
        Spinner editCategory = dialogView.findViewById(R.id.dialog_category);
        DatePicker editDate = dialogView.findViewById(R.id.dialog_date);
        EditText editDescription = dialogView.findViewById(R.id.dialog_description);
        Button OkButton = dialogView.findViewById(R.id.dialog_ok_button);
        Button CancelButton = dialogView.findViewById(R.id.dialog_cancel_button);

        editTitle.setText("Изменение операции");
        editAmount.setText(String.valueOf(amount));

        //Получаем категории
        expensesCategories.clear();
        expensesImages.clear();
        incomeCategories.clear();
        incomeImages.clear();
        SQLiteDatabase db = new DBHelper(getActivity()).getReadableDatabase();
        String sql = "SELECT name, image, type FROM categories";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndex("type")) == 1) {
                    expensesCategories.add(cursor.getString(cursor.getColumnIndex("name")));
                    expensesImages.add(cursor.getInt(cursor.getColumnIndex("image")));
                } else {
                    incomeCategories.add(cursor.getString(cursor.getColumnIndex("name")));
                    incomeImages.add(cursor.getInt(cursor.getColumnIndex("image")));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        ArrayList<String> spinnerList;
        ArrayList<Integer> spinnerListImages;
        if (expensesCategories.contains(category)) {
            spinnerList = expensesCategories;
            spinnerListImages = expensesImages;
        } else {
            spinnerList = incomeCategories;
            spinnerListImages = incomeImages;
        }
        SpinnerAdapter adapter = new SpinnerAdapter(getContext(), spinnerListImages, spinnerList, true);
        editCategory.setAdapter(adapter);
        int position = 0;
        for (String string : spinnerList) {
            if (string.equals(category)) {
                break;
            }
            position++;
        }
        editCategory.setSelection(position, true);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dateDB;
        try {
            dateDB = sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar calendar = Calendar.getInstance();
        if (dateDB != null) {
            calendar.setTime(dateDB);
        }
        editDate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        editDate.setMaxDate(new Date().getTime());

        editDescription.setText(description);

        OkButton.setText("Изменить");
        OkButton.setOnClickListener(v -> {
            String amount = editAmount.getText().toString();
            String category = spinnerList.get(editCategory.getSelectedItemPosition());
            String description = editDescription.getText().toString();

            int day = editDate.getDayOfMonth();
            int month = editDate.getMonth() + 1;
            int year = editDate.getYear();
            String date = "";
            if (month < 10 && day < 10) {
                date = year + "-0" + month + "-0" + day;
            } else if (month < 10) {
                date = year + "-0" + month + "-" + day;
            } else if (day < 10) {
                date = year + "-" + month + "-0" + day;
            } else {
                date = year + "-" + month + "-" + day;
            }

            if (amount.length() == 0 || description.length() == 0) {
                if (amount.length() == 0) {
                    editAmount.setError("Введите сумму");
                }
                if (description.length() == 0) {
                    editDescription.setError("Введите описание");
                }
            } else {
                ((OperationsInfoActivity) requireActivity()).onChangeItem(id, Integer.parseInt(amount), category, date, description);
                dismiss();
            }
        });


        CancelButton.setOnClickListener(v -> dismiss());

        return builder.create();
    }
}
