package com.example.mysamsungapp.ui.categories;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.example.mysamsungapp.DBHelper;
import com.example.mysamsungapp.R;

import java.util.ArrayList;

public class AddCategoryDialog extends DialogFragment implements View.OnClickListener {
    ConstraintLayout imagesContainer;
    private ImageButton selectedButton;
    private OnActionCategory onActionCategory;
    private int type;
    int[] imagesExpenses;
    int[] imagesIncomes;
    int[] images;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onActionCategory = (OnActionCategory) getParentFragment();
    }

    public static AddCategoryDialog newInstance(int type, int[] imagesExpenses, int[] imagesIncomes) {
        AddCategoryDialog dialog = new AddCategoryDialog();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putIntArray("imagesExpenses", imagesExpenses);
        args.putIntArray("imagesIncomes", imagesIncomes);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
            imagesExpenses = getArguments().getIntArray("imagesExpenses");
            imagesIncomes = getArguments().getIntArray("imagesIncomes");
        }
    }

    @SuppressLint("Range")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater1 = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater1.inflate(R.layout.categories_add_category, null);
        builder.setView(dialogView);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
        RadioButton expensesRadioButton = dialogView.findViewById(R.id.radioButton2);
        RadioButton incomeRadioButton = dialogView.findViewById(R.id.radioButton);
        TextView typeTextView = dialogView.findViewById(R.id.TypeTextView);
        TextView typeTextViewData = dialogView.findViewById(R.id.TypeTextViewData);
        EditText editName = dialogView.findViewById(R.id.dialog_description);
        Button OkButton = dialogView.findViewById(R.id.dialog_ok_button);
        Button CancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        imagesContainer = dialogView.findViewById(R.id.imagesCategoryContainer);

        typeTextView.setVisibility(View.INVISIBLE);
        typeTextViewData.setVisibility(View.INVISIBLE);

        if (type == 1) {
            expensesRadioButton.setChecked(true);
            images = imagesExpenses;
        } else {
            incomeRadioButton.setChecked(true);
            images = imagesIncomes;
        }
        updateImages();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton2) {
                images = imagesExpenses;
            } else {
                images = imagesIncomes;
            }
            updateImages();
        });

        OkButton.setOnClickListener(v2 -> {
            int checkedButton = radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())) + 1;
            SQLiteDatabase dbCheck = new DBHelper(getContext()).getReadableDatabase();
            String sql = "SELECT name FROM categories WHERE type = '" + checkedButton + "'";
            Cursor cursor = dbCheck.rawQuery(sql, null);
            ArrayList<String> names = new ArrayList<>();
            if (cursor.moveToNext()) {
                do {
                    names.add(cursor.getString(cursor.getColumnIndex("name")));
                } while (cursor.moveToNext());
            }
            cursor.close();
            dbCheck.close();
            if (editName.length() == 0 || names.contains(editName.getText().toString())) {
                if (editName.length() == 0) {
                    editName.setError("Введите название");
                }
                if (names.contains(editName.getText().toString())) {
                    editName.setError("Такая категория уже есть");
                }
            } else {
                SQLiteDatabase db = new DBHelper(getContext()).getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("name", editName.getText().toString());
                values.put("type", checkedButton);
                values.put("image", images[Integer.parseInt(selectedButton.getTag().toString())]);
                db.insert("categories", null, values);
                db.close();
                dismiss();
                onActionCategory.onAdd();
            }
        });

        CancelButton.setOnClickListener(v1 -> dismiss());

        return builder.create();
    }

    void updateImages() {
        boolean flag = true;
        for (int i = 0; i < imagesContainer.getChildCount(); i++) {
            ImageButton button = (ImageButton) imagesContainer.getChildAt(i);
            if (i < images.length) {
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(this);
                button.setImageResource(images[i]);
                button.setTag(i);
                if (flag) {
                    selectedButton = button;
                    selectedButton.setBackgroundResource(R.drawable.custom_category_item_selected);
                    flag = false;
                } else {
                    button.setBackgroundResource(R.color.transparent);
                }
            } else {
                button.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        selectedButton.setBackgroundResource(R.color.transparent);
        selectedButton = (ImageButton) v;
        selectedButton.setBackgroundResource(R.drawable.custom_category_item_selected);
    }
}
