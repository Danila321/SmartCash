package com.example.mysamsungapp.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class AddOperationDialog extends DialogFragment {
    private int type;
    private String[] spinnerList;
    private OnAddOperation onAddOperation;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onAddOperation = (OnAddOperation) getParentFragment();
    }

    public static AddOperationDialog newInstance(int type, String[] spinnerList) {
        AddOperationDialog dialog = new AddOperationDialog();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putStringArray("spinnerList", spinnerList);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
            spinnerList = getArguments().getStringArray("spinnerList");
        }
    }

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCategory.setAdapter(adapter);

        OkButton.setOnClickListener(v -> {
            String amount = editAmount.getText().toString();
            String category = editCategory.getSelectedItem().toString();
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
                SQLiteDatabase db = new DBHelper(getContext()).getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("amount", amount);
                values.put("category", category);
                values.put("date", date);
                values.put("type", type);
                values.put("description", description);
                db.insert("operations", null, values);
                db.close();
                onAddOperation.onAddOperation();
                dismiss();
            }
        });

        CancelButton.setOnClickListener(v -> dismiss());

        return builder.create();
    }
}