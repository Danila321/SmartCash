package com.example.mysamsungapp.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Date;

public class AddOperationDialog extends DialogFragment {
    private int type;
    private ArrayList<String> categoriesName;
    private ArrayList<Integer> categoriesImage;
    private OnAddOperation onAddOperation;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onAddOperation = (OnAddOperation) getParentFragment();
    }

    public static AddOperationDialog newInstance(int type, ArrayList<String> categoriesName, ArrayList<Integer> categoriesImage) {
        AddOperationDialog dialog = new AddOperationDialog();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putStringArrayList("categoriesName", categoriesName);
        args.putIntegerArrayList("categoriesImage", categoriesImage);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
            categoriesName = getArguments().getStringArrayList("categoriesName");
            categoriesImage = getArguments().getIntegerArrayList("categoriesImage");
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

        if (type == 0) {
            editTitle.setText("Добавление расхода");
        } else {
            editTitle.setText("Добавление дохода");
        }

        SpinnerAdapter adapter = new SpinnerAdapter(getContext(), categoriesImage, categoriesName, true);
        editCategory.setAdapter(adapter);

        editDate.setMaxDate(new Date().getTime());

        OkButton.setOnClickListener(v -> {
            String amount = editAmount.getText().toString();
            String category = categoriesName.get(editCategory.getSelectedItemPosition());
            String description = editDescription.getText().toString();

            int day = editDate.getDayOfMonth();
            int month = editDate.getMonth() + 1;
            int year = editDate.getYear();
            String date;
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
                //Получаем category_id для добавления операции
                SQLiteDatabase dbCategory = new DBHelper(getContext()).getReadableDatabase();
                String sql = "SELECT id FROM categories WHERE name = '" + category + "'";
                Cursor cursor = dbCategory.rawQuery(sql, null);
                int categoryId = 0;
                if (cursor.moveToFirst()){
                    categoryId = cursor.getInt(cursor.getColumnIndex("id"));
                }
                cursor.close();
                dbCategory.close();
                //Добавляем операцию в БД
                SQLiteDatabase dbAdd = new DBHelper(getContext()).getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("amount", amount);
                values.put("date", date);
                values.put("type", type);
                values.put("description", description);
                values.put("category_id", categoryId);
                dbAdd.insert("operations", null, values);
                dbAdd.close();
                onAddOperation.onAddOperation();
                dismiss();
            }
        });

        CancelButton.setOnClickListener(v -> dismiss());

        return builder.create();
    }
}