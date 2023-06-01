package com.example.mysamsungapp.ui.categories;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteCategoryDialog extends DialogFragment {
    private int id;
    private OnActionCategory onActionCategory;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onActionCategory = (OnActionCategory) getParentFragment();
    }

    public static DeleteCategoryDialog newInstance(int id) {
        DeleteCategoryDialog dialog = new DeleteCategoryDialog();
        Bundle args = new Bundle();
        args.putInt("id", id);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Удаление категории")
                .setMessage("Удаление категории приведет к удалению всех операций относящихся к ней! Вы действительно хотите удалить категорию?")
                .setNegativeButton("Нет", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Да", (dialog, which) -> onActionCategory.onDelete(id));
        return builder.create();
    }
}
