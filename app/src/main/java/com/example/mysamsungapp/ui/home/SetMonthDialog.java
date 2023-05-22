package com.example.mysamsungapp.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mysamsungapp.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Calendar;

public class SetMonthDialog extends DialogFragment {
    int selectedYear, selectedMonthNumber;
    String selectedMonth;
    String date;
    private OnChangeDate onChangeDate;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChangeDate = (OnChangeDate) getParentFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.home_edit_month_dialog, null);
        builder.setView(dialogView);

        TextView finalDate = dialogView.findViewById(R.id.finalDateText);
        TextView year = dialogView.findViewById(R.id.yearText);
        ImageButton arrowLeft = dialogView.findViewById(R.id.arrowLeft);
        ImageButton arrowRight = dialogView.findViewById(R.id.arrowRight);
        ChipGroup chipGroup = dialogView.findViewById(R.id.chipGroupMonths);
        TextView cancel = dialogView.findViewById(R.id.cancelTextView);
        TextView choose = dialogView.findViewById(R.id.chooseTextView);

        //Получаем текущий месяц
        String[] monthsNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        selectedMonth = monthsNames[Calendar.getInstance().get(Calendar.MONTH)];
        selectedMonthNumber = Calendar.getInstance().get(Calendar.MONTH) + 1;
        //Получаем текущий год
        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        year.setText(String.valueOf(selectedYear));
        //Проверяем месяца
        checkMonths(chipGroup);
        //Формируем текст даты
        date = selectedMonth + " " + selectedYear;
        finalDate.setPaintFlags(finalDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        finalDate.setText(date);

        arrowRight.setVisibility(View.INVISIBLE);

        arrowLeft.setOnClickListener(v -> {
            selectedYear--;
            checkMonths(chipGroup);
            year.setText(String.valueOf(selectedYear));
            date = selectedMonth + " " + selectedYear;
            finalDate.setText(date);
            if (selectedYear > 1970) {
                arrowRight.setVisibility(View.VISIBLE);
            } else {
                arrowLeft.setVisibility(View.INVISIBLE);
            }
        });

        arrowRight.setOnClickListener(v -> {
            selectedYear++;
            checkMonths(chipGroup);
            year.setText(String.valueOf(selectedYear));
            date = selectedMonth + " " + selectedYear;
            finalDate.setText(date);
            if (selectedYear < Calendar.getInstance().get(Calendar.YEAR)) {
                arrowLeft.setVisibility(View.VISIBLE);
            } else {
                arrowRight.setVisibility(View.INVISIBLE);
            }
        });

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.size() > 0) {
                int selectedChipNumber = chipGroup.indexOfChild(dialogView.findViewById(checkedIds.get(0)));
                selectedMonthNumber = selectedChipNumber + 1;
                selectedMonth = monthsNames[selectedChipNumber];
                date = selectedMonth + " " + selectedYear;
                finalDate.setText(date);
            }
        });

        cancel.setOnClickListener(v -> dismiss());

        choose.setOnClickListener(v -> {
            onChangeDate.onChangeDate(selectedMonthNumber + " " + selectedYear, 2);
            dismiss();
        });

        return builder.create();
    }

    void checkMonths(ChipGroup chipGroup) {
        for (int i = 0; i < 12; i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (selectedYear == Calendar.getInstance().get(Calendar.YEAR)) {
                if (i == Calendar.getInstance().get(Calendar.MONTH)) {
                    chip.setChecked(true);
                }
                if (i > Calendar.getInstance().get(Calendar.MONTH)) {
                    chip.setTextColor(getResources().getColor(R.color.chip_gray, null));
                    chip.setCheckable(false);
                }
            } else {
                chip.setCheckable(true);
                chip.setTextColor(getResources().getColor(R.color.black, null));
            }
        }
    }
}