package com.example.mysamsungapp.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteOperationDialog extends DialogFragment {
    private int id;

    public static DeleteOperationDialog newInstance(int id) {
        DeleteOperationDialog dialog = new DeleteOperationDialog();
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Удаление операции")
                .setMessage("Вы действительно хотите удалить данную операцию?")
                .setNegativeButton("Нет", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Да", (dialog, which) -> ((OperationsInfoActivity) requireActivity()).onDeleteItem(id));
        return builder.create();
    }
}
