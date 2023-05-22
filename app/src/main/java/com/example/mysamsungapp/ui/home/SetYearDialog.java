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

import java.util.Calendar;

public class SetYearDialog extends DialogFragment {
    int selectedYear;
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
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.home_edit_year_dialog, null);
        builder.setView(dialogView);

        TextView finalDate = dialogView.findViewById(R.id.finalDateText);
        TextView year = dialogView.findViewById(R.id.yearText);
        ImageButton arrowLeft = dialogView.findViewById(R.id.arrowLeft);
        ImageButton arrowRight = dialogView.findViewById(R.id.arrowRight);
        TextView cancel = dialogView.findViewById(R.id.cancelTextView);
        TextView choose = dialogView.findViewById(R.id.chooseTextView);

        //Получаем текущий год
        int todayYear = Calendar.getInstance().get(Calendar.YEAR);
        selectedYear = todayYear;
        year.setText(String.valueOf(todayYear));
        //Формируем текст даты
        date = todayYear + " год";
        finalDate.setPaintFlags(finalDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        finalDate.setText(date);

        arrowRight.setVisibility(View.INVISIBLE);

        arrowLeft.setOnClickListener(v -> {
            selectedYear--;
            year.setText(String.valueOf(selectedYear));
            date = selectedYear + " год";
            finalDate.setText(date);
            if (selectedYear > 1970) {
                arrowRight.setVisibility(View.VISIBLE);
            } else {
                arrowLeft.setVisibility(View.INVISIBLE);
            }
        });

        arrowRight.setOnClickListener(v -> {
            selectedYear++;
            year.setText(String.valueOf(selectedYear));
            date = selectedYear + " год";
            finalDate.setText(date);
            if (selectedYear < todayYear) {
                arrowLeft.setVisibility(View.VISIBLE);
            } else {
                arrowRight.setVisibility(View.INVISIBLE);
            }
        });

        cancel.setOnClickListener(v -> dismiss());

        choose.setOnClickListener(v -> {
            onChangeDate.onChangeDate(String.valueOf(selectedYear), 3);
            dismiss();
        });

        return builder.create();
    }
}