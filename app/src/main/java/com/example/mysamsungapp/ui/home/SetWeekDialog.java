package com.example.mysamsungapp.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.mysamsungapp.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class SetWeekDialog extends DialogFragment {
    TextView selectedTextView = null;
    int selectedYear, selectedMonthNumber;
    String selectedMonth, selectedWeek;
    String date;
    ChipGroup chipGroup;
    private OnChangeDate onChangeDate;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChangeDate = (OnChangeDate) getParentFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.home_edit_week_dialog, null);
        builder.setView(dialogView);

        TextView finalDate = dialogView.findViewById(R.id.finalDateText);
        TextView year = dialogView.findViewById(R.id.yearText);
        ImageButton arrowLeft = dialogView.findViewById(R.id.arrowLeft);
        ImageButton arrowRight = dialogView.findViewById(R.id.arrowRight);
        HorizontalScrollView scrollView = dialogView.findViewById(R.id.horizontalScrollView);
        chipGroup = dialogView.findViewById(R.id.chipGroupMonths);
        LinearLayout linearLayout = dialogView.findViewById(R.id.LinearLayoutWeeks);
        TextView cancel = dialogView.findViewById(R.id.cancelTextView);
        TextView choose = dialogView.findViewById(R.id.chooseTextView);

        //Получаем текущий месяц
        String[] monthsNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        selectedMonth = monthsNames[Calendar.getInstance().get(Calendar.MONTH)];
        selectedMonthNumber = Calendar.getInstance().get(Calendar.MONTH) + 1;
        //Получаем текущий год
        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        year.setText(String.valueOf(selectedYear));
        //Проверяем месяца и выводим недели
        checkMonths();
        getWeeks(linearLayout, finalDate);
        //Формируем текст даты
        date = selectedWeek + ", " + selectedYear;
        finalDate.setPaintFlags(finalDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        finalDate.setText(date);

        //Проматываем scrollView до сегодняшнего месяца
        int scrollTo = (chipGroup.indexOfChild(dialogView.findViewById(chipGroup.getCheckedChipId())) + 1) * 143;
        scrollView.post(() -> scrollView.smoothScrollTo(scrollTo, 0));

        arrowRight.setVisibility(View.INVISIBLE);

        arrowLeft.setOnClickListener(v -> {
            selectedYear--;
            checkMonths();
            year.setText(String.valueOf(selectedYear));
            getWeeks(linearLayout, finalDate);
            date = selectedWeek + ", " + selectedYear;
            finalDate.setText(date);
            if (selectedYear > 1970) {
                arrowRight.setVisibility(View.VISIBLE);
            } else {
                arrowLeft.setVisibility(View.INVISIBLE);
            }
        });

        arrowRight.setOnClickListener(v -> {
            selectedYear++;
            checkMonths();
            year.setText(String.valueOf(selectedYear));
            getWeeks(linearLayout, finalDate);
            date = selectedWeek + ", " + selectedYear;
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
                getWeeks(linearLayout, finalDate);
                date = selectedWeek + ", " + selectedYear;
                finalDate.setText(date);
            }
        });

        cancel.setOnClickListener(v -> dismiss());

        choose.setOnClickListener(v -> {
            onChangeDate.onChangeDate(date, 1);
            dismiss();
        });

        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getWeeks(LinearLayout layout, TextView finalDate) {
        LocalDate WeekDate = LocalDate.of(selectedYear, selectedMonthNumber, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter TextFormatter = DateTimeFormatter.ofPattern("dd MMM");
        layout.removeAllViews();
        boolean flag = true;
        while (WeekDate.getMonthValue() == selectedMonthNumber) {
            LocalDate startOfWeek = WeekDate.with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = WeekDate.with(DayOfWeek.SUNDAY);
            //Создаем TextView с датами недели
            TextView textView = new TextView(getContext());
            String week = startOfWeek.format(formatter) + "-" + endOfWeek.format(formatter);
            textView.setText(week);
            textView.setTag(startOfWeek.format(TextFormatter) + " - " + endOfWeek.format(TextFormatter));
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(15);
            textView.setPadding(20, 5, 20, 5);
            //Устанавливаем отступы между TextView
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 22, 0);
            textView.setLayoutParams(params);
            //Устанавливаем слушатель для выбора недели
            Date startOfWeekDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
            if (new Date().before(startOfWeekDate)) {
                textView.setTextColor(Color.LTGRAY);
            } else {
                textView.setOnClickListener(v -> {
                    if (selectedTextView != null) {
                        selectedTextView.setBackgroundResource(0);
                    }
                    selectedTextView = (TextView) v;
                    selectedTextView.setBackgroundResource(R.drawable.custom_week_text);
                    selectedWeek = textView.getTag().toString();
                    date = selectedWeek + ", " + selectedYear;
                    finalDate.setText(date);
                });
                //Выбираем первую неделю по умолчанию
                if (flag) {
                    selectedTextView = textView;
                    selectedTextView.setBackgroundResource(R.drawable.custom_week_text);
                    selectedWeek = textView.getTag().toString();
                    flag = false;
                }
            }
            //Отображаем полученный TextView
            layout.addView(textView);
            WeekDate = endOfWeek.plusDays(1);
        }
    }

    void checkMonths() {
        for (int i = 0; i < 12; i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chip.setAlpha(1f);
            if (selectedYear == Calendar.getInstance().get(Calendar.YEAR)) {
                if (i == Calendar.getInstance().get(Calendar.MONTH)) {
                    chip.setChecked(true);
                }
                if (i > Calendar.getInstance().get(Calendar.MONTH)) {
                    chip.setAlpha(0.45f);
                    chip.setCheckable(false);
                }
            } else {
                chip.setCheckable(true);
            }
        }
    }
}