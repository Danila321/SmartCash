package com.example.mysamsungapp.ui.categories;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;

import com.example.mysamsungapp.DBHelper;
import com.example.mysamsungapp.R;
import com.example.mysamsungapp.ui.home.AddOperationDialog;
import com.example.mysamsungapp.ui.home.OnAddOperation;

import java.util.ArrayList;

public class AddCategoryDialog extends DialogFragment implements View.OnClickListener{
    private ImageButton selectedButton;
    private int type;
    private OnActionCategory onActionCategory;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onActionCategory = (OnActionCategory) getParentFragment();
    }

    public static AddCategoryDialog newInstance(int type) {
        AddCategoryDialog dialog = new AddCategoryDialog();
        Bundle args = new Bundle();
        args.putInt("type", type);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
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
        EditText editName = dialogView.findViewById(R.id.dialog_description);
        Button OkButton = dialogView.findViewById(R.id.dialog_ok_button);
        Button CancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        ConstraintLayout imagesContainer = dialogView.findViewById(R.id.imagesCategoryContainer);

        int[] images;
        int[] imagesExpenses = new int[]{R.drawable.products, R.drawable.food, R.drawable.education, R.drawable.family, R.drawable.sport, R.drawable.airplane, R.drawable.metro, R.drawable.gas_station};
        int[] imagesIncomes = new int[]{R.drawable.gift, R.drawable.salary, R.drawable.percent};
        if (type == 1) {
            expensesRadioButton.setChecked(true);
            images = imagesExpenses;
        } else {
            incomeRadioButton.setChecked(true);
            images = imagesIncomes;
        }

        for (int i = 0; i < imagesContainer.getChildCount(); i++) {
            ImageButton button = (ImageButton) imagesContainer.getChildAt(i);
            if (i < images.length){
                button.setOnClickListener(this);
                button.setImageResource(images[i]);
                button.setTag(i);
            } else {
                button.setVisibility(View.GONE);
            }
        }



        OkButton.setOnClickListener(v2 -> {
            if (editName.length() == 0) {
                editName.setError("Введите название");
            } else {
                int checkedButton = radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())) + 1;
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

    @Override
    public void onClick(View v) {
        if (selectedButton != null) {
            selectedButton.setBackgroundResource(R.color.transparent);
        }
        selectedButton = (ImageButton) v;
        selectedButton.setBackgroundResource(R.color.holo_blue_dark);
    }
}
