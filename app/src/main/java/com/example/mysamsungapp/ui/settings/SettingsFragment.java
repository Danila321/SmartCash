package com.example.mysamsungapp.ui.settings;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mysamsungapp.DBHelper;
import com.example.mysamsungapp.R;
import com.example.mysamsungapp.databinding.FragmentSettingsBinding;
import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView delete = root.findViewById(R.id.deleteAllText);
        ImageButton infoDelete = root.findViewById(R.id.InfoImageView);

        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Удаление данных")
                    .setMessage("Вы уверены, что хотите удалить все данные?")
                    .setNegativeButton("Нет", (dialog, which) -> dialog.cancel())
                    .setPositiveButton("Да", (dialog, which) -> this.onDelete());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        infoDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Удаление данных");
            builder.setMessage("Все ваши данные в приложении, включая все операции и категории будут безвозвратно удалены с вашего устройства");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onDelete() {
        SQLiteDatabase db = new DBHelper(getContext()).getWritableDatabase();
        db.delete("operations", "1", null);
        db.delete("categories", "1", null);
        Snackbar.make(requireView(), "Все данные успешно удалены", Snackbar.LENGTH_SHORT).show();
        db.close();
    }
}