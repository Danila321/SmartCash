package com.example.mysamsungapp.ui.statistics;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mysamsungapp.DBHelper;
import com.example.mysamsungapp.R;
import com.example.mysamsungapp.ui.home.OnChangeDate;
import com.example.mysamsungapp.ui.home.SetMonthDialog;
import com.example.mysamsungapp.ui.home.SetWeekDialog;
import com.example.mysamsungapp.ui.home.SetYearDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpensesIncomeChartFragment extends Fragment implements OnChangeDate {
    private static final String ARG_PARAM = "type";
    private int type;
    BarChart barChart;
    PieChart pieChart;
    BarData data;
    PieData dataPie;
    TextView period, chartType, reportTextSum, reportTextCategory, reportTextSumProfit, reportTextCategoryProfit;
    AlertDialog alertDialog;
    String expensiveCategory, profitableCategory;
    int profitableAmount, expensiveAmount;
    boolean chartTypeFlag = true, dialogChartFlag = true;
    ArrayList<BarEntry> barEntries = new ArrayList<>();
    ArrayList<PieEntry> pieEntries = new ArrayList<>();
    String[] categories, categoriesText;
    String sortDate = "";
    String todayDate, dateOfStartWeek, dateOfStartMonth, dateOfStartYear;
    String todayText, WeekText, MonthText, YearText;

    public ExpensesIncomeChartFragment() {

    }

    public static ExpensesIncomeChartFragment newInstance(int param1) {
        ExpensesIncomeChartFragment fragment = new ExpensesIncomeChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_chart_fragment, container, false);

        ChipGroup chipGroup = view.findViewById(R.id.statChipGroup);
        barChart = view.findViewById(R.id.chart1);
        pieChart = view.findViewById(R.id.chart2);
        Chip chipYear = view.findViewById(R.id.chipYear);
        period = view.findViewById(R.id.statTextViewDate);
        chartType = view.findViewById(R.id.changeChartType);
        TextView reportText = view.findViewById(R.id.reportText);
        TextView reportText2 = view.findViewById(R.id.textView5);
        reportTextSum = view.findViewById(R.id.reportTextSum);
        reportTextCategory = view.findViewById(R.id.reportTextCategory);
        TextView reportProfitText = view.findViewById(R.id.reportProfitText);
        TextView reportProfitText2 = view.findViewById(R.id.TextView6);
        reportTextSumProfit = view.findViewById(R.id.reportProfitTextSum);
        reportTextCategoryProfit = view.findViewById(R.id.reportProfitTextCategory);

        //Первоначальные настройки
        if (type == 1) {
            reportText.setText("Самая затратная категория: ");
            reportText2.setText("Вы потратили на неё: ");
            reportProfitText.setText("Самая дешевая категория: ");
            reportProfitText2.setText("Вы потратили на неё: ");
            categories = new String[]{"Продукты", "Спорт", "Транспорт", "Образование", "Семья", "Еда", "Прочее"};
            categoriesText = new String[]{"Продукты", "Спорт", "Трансп.", "Образов.", "Семья", "Еда", "Прочее"};
        } else {
            reportText.setText("Самая прибыльная категория: ");
            reportText2.setText("Вы заработали на ней: ");
            reportProfitText.setText("Самая неприбыльная категория: ");
            reportProfitText2.setText("Вы заработали на ней: ");
            categories = new String[]{"Зарплата", "Подарок", "% по вкладу", "Прочее"};
            categoriesText = categories;
        }
        chipYear.setChecked(true);
        period.setPaintFlags(period.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        chartType.setPaintFlags(period.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //Обновляем все данные
        makeDates();
        sortDate = "BETWEEN '" + dateOfStartYear + "' AND '" + todayDate + "'";
        period.setText(YearText);
        getDataFromDB(categories);
        updateChart();
        updateTexts();

        //Устанавливаем слушатель на дату для выбора
        period.setOnClickListener(v -> {
            List<Integer> checkedIds = chipGroup.getCheckedChipIds();
            if (checkedIds.size() > 0) {
                switch (chipGroup.indexOfChild(view.findViewById(checkedIds.get(0))) + 1) {
                    case 4:
                        showDatePickerDialog();
                        break;
                    case 3:
                        SetWeekDialog weekDialog = new SetWeekDialog();
                        weekDialog.show(getChildFragmentManager(), "weekDialog");
                        break;
                    case 2:
                        SetMonthDialog monthDialog = new SetMonthDialog();
                        monthDialog.show(getChildFragmentManager(), "monthDialog");
                        break;
                    case 1:
                        SetYearDialog yearDialog = new SetYearDialog();
                        yearDialog.show(getChildFragmentManager(), "yearDialog");
                        break;
                    default:
                        break;
                }
            }
        });

        chartType.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater1 = requireActivity().getLayoutInflater();
            @SuppressLint("InflateParams") View dialogView = inflater1.inflate(R.layout.statistics_chage_chart_dialog, null);
            builder.setView(dialogView);

            ConstraintLayout barChartItem = dialogView.findViewById(R.id.barChartDialog);
            ConstraintLayout pieChartItem = dialogView.findViewById(R.id.pieChartDialog);
            TextView barChartText = dialogView.findViewById(R.id.barChartText);
            TextView pieChartText = dialogView.findViewById(R.id.pieChartText);
            Button OkButton = dialogView.findViewById(R.id.dialog_ok_button);
            Button CancelButton = dialogView.findViewById(R.id.dialog_cancel_button);

            if (chartTypeFlag) {
                barChartItem.setBackgroundResource(R.drawable.statistics_chart_choose_clicked);
                barChartText.setTextColor(Color.WHITE);
                pieChartItem.setBackgroundResource(R.drawable.statistics_chart_choose);
                pieChartText.setTextColor(getResources().getColor(R.color.green_text, null));
            } else {
                barChartItem.setBackgroundResource(R.drawable.statistics_chart_choose);
                barChartText.setTextColor(getResources().getColor(R.color.green_text, null));
                pieChartItem.setBackgroundResource(R.drawable.statistics_chart_choose_clicked);
                pieChartText.setTextColor(Color.WHITE);
            }

            barChartItem.setOnClickListener(v1 -> {
                barChartItem.setBackgroundResource(R.drawable.statistics_chart_choose_clicked);
                barChartText.setTextColor(Color.WHITE);
                pieChartItem.setBackgroundResource(R.drawable.statistics_chart_choose);
                pieChartText.setTextColor(getResources().getColor(R.color.green_text, null));
                dialogChartFlag = true;
            });

            pieChartItem.setOnClickListener(v2 -> {
                barChartItem.setBackgroundResource(R.drawable.statistics_chart_choose);
                barChartText.setTextColor(getResources().getColor(R.color.green_text, null));
                pieChartItem.setBackgroundResource(R.drawable.statistics_chart_choose_clicked);
                pieChartText.setTextColor(Color.WHITE);
                dialogChartFlag = false;
            });

            CancelButton.setOnClickListener(v3 -> alertDialog.dismiss());

            OkButton.setOnClickListener(v4 -> {
                chartTypeFlag = dialogChartFlag;
                getDataFromDB(categories);
                updateChart();
                reportTextSum.setText(String.valueOf(profitableCategory));
                reportTextCategory.setText(String.valueOf(profitableAmount));
                reportTextSumProfit.setText(String.valueOf(expensiveCategory));
                reportTextCategoryProfit.setText(String.valueOf(expensiveAmount));
                alertDialog.dismiss();
            });

            alertDialog = builder.create();
            alertDialog.show();
        });

        //устанавливаем слушатель на chipGroup для фильтрации по дате
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.size() > 0) {
                switch (chipGroup.indexOfChild(view.findViewById(checkedIds.get(0))) + 1) {
                    case 4:
                        sortDate = "= '" + todayDate + "'";
                        period.setText(todayText);
                        break;
                    case 3:
                        sortDate = "BETWEEN '" + dateOfStartWeek + "' AND '" + todayDate + "'";
                        period.setText(WeekText);
                        break;
                    case 2:
                        sortDate = "BETWEEN '" + dateOfStartMonth + "' AND '" + todayDate + "'";
                        period.setText(MonthText);
                        break;
                    case 1:
                        sortDate = "BETWEEN '" + dateOfStartYear + "' AND '" + todayDate + "'";
                        period.setText(YearText);
                        break;
                }
                getDataFromDB(categories);
                updateChart();
                updateTexts();
            }
        });

        return view;
    }

    @SuppressLint("Range")
    void getDataFromDB(String[] array) {
        int[] chartColorsArray = new int[]{Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
                Color.rgb(191, 134, 134), Color.rgb(179, 48, 80), Color.GRAY, Color.GREEN};
        barEntries.clear();
        pieEntries.clear();
        expensiveAmount = Integer.MAX_VALUE;
        profitableAmount = Integer.MIN_VALUE;
        SQLiteDatabase db = new DBHelper(getActivity()).getReadableDatabase();
        for (int i = 0; i < array.length; i++) {
            int sum = 0;
            String sql = "SELECT amount FROM operations WHERE category = '" + array[i] + "'" + " AND date " + sortDate;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    sum += cursor.getInt(cursor.getColumnIndex("amount"));
                } while (cursor.moveToNext());
            }
            cursor.close();
            //Получаем самую затратную и дешевую категорию
            if (sum < expensiveAmount) {
                expensiveAmount = sum;
                expensiveCategory = array[i];
            }
            if (sum > profitableAmount) {
                profitableAmount = sum;
                profitableCategory = array[i];
            }
            //Добавляем все значения
            barEntries.add(new BarEntry(i, sum));
            if (sum == 0) {
                continue;
            }
            pieEntries.add(new PieEntry(sum, array[i]));
        }
        db.close();
        if (chartTypeFlag) {
            BarDataSet barDataSet = new BarDataSet(barEntries, "");
            barDataSet.setValueTextSize(13f);
            barDataSet.setColors(chartColorsArray);
            data = new BarData(barDataSet);
        } else {
            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setColors(chartColorsArray);
            dataPie = new PieData(pieDataSet);
        }
    }

    public void makeDates() {
        Date today = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfToday = new SimpleDateFormat("dd MMMM");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfMonth = new SimpleDateFormat("LLLL yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

        //Текущая неделя
        Calendar weekCalendar = Calendar.getInstance();
        weekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startOfWeek = weekCalendar.getTime();
        weekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Date endOfWeek = weekCalendar.getTime();

        //Текущий месяц
        Calendar monthCalendar = Calendar.getInstance();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = monthCalendar.getTime();

        //Текущий год
        Calendar yearCalendar = Calendar.getInstance();
        yearCalendar.set(Calendar.DAY_OF_YEAR, 1);
        Date startOfYear = yearCalendar.getTime();

        todayDate = sdfDB.format(today);
        dateOfStartWeek = sdfDB.format(startOfWeek);
        dateOfStartMonth = sdfDB.format(startOfMonth);
        dateOfStartYear = sdfDB.format(startOfYear);

        todayText = "Сегодня, " + sdfToday.format(today);
        WeekText = sdfToday.format(startOfWeek) + " - " + sdfToday.format(endOfWeek);
        MonthText = sdfMonth.format(today);
        YearText = sdfYear.format(today) + " год";
    }

    public void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        @SuppressLint("SimpleDateFormat") DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //Отображаем полученную дату
            String periodText;
            if (new Date().equals(selectedDate.getTime())) {
                SimpleDateFormat sdfToday = new SimpleDateFormat("dd MMMM");
                periodText = "Сегодня, " + sdfToday.format(selectedDate.getTime());
            } else {
                SimpleDateFormat format;
                if (mYear == year) {
                    format = new SimpleDateFormat("dd MMMM", Locale.getDefault());
                } else {
                    format = new SimpleDateFormat("dd MMMM yyyy г.", Locale.getDefault());
                }
                periodText = format.format(selectedDate.getTime());
            }
            //Обновляем все данные
            period.setText(periodText);
            sortDate = "= '" + new SimpleDateFormat("yyyy-MM-dd").format(selectedDate.getTime()) + "'";
            getDataFromDB(categories);
            updateChart();
            updateTexts();
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onChangeDate(String data, int type) {
        String periodText = null;
        Date date;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd");
        switch (type) {
            case 1:
                try {
                    date = new SimpleDateFormat("dd MMM - dd MMM, yyyy").parse(data);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if (date != null) {
                    calendar.setTime(date);
                }
                Date endOfWeek = calendar.getTime();
                calendar.add(Calendar.DATE, -6);
                Date startOfWeek = calendar.getTime();
                if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                    periodText = new SimpleDateFormat("dd MMMM").format(startOfWeek) + " - " + new SimpleDateFormat("dd MMMM").format(endOfWeek);
                } else {
                    periodText = data;
                }
                //Обновляем все данные
                period.setText(periodText);
                sortDate = "BETWEEN '" + sdfDB.format(startOfWeek) + "' AND '" + sdfDB.format(endOfWeek) + "'";
                break;
            case 2:
                try {
                    date = new SimpleDateFormat("M yyyy").parse(data);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if (date != null) {
                    calendar.setTime(date);
                }
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Date startOfMonth = calendar.getTime();
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date endOfMonth = calendar.getTime();
                //Обновляем все данные
                if (date != null) {
                    periodText = new SimpleDateFormat("LLLL yyyy").format(date);
                }
                period.setText(periodText);
                sortDate = "BETWEEN '" + sdfDB.format(startOfMonth) + "' AND '" + sdfDB.format(endOfMonth) + "'";
                break;
            case 3:
                Calendar yearCalendar = Calendar.getInstance();
                yearCalendar.set(Calendar.YEAR, Integer.parseInt(data));
                yearCalendar.set(Calendar.DAY_OF_YEAR, 1);
                Date startOfYear = yearCalendar.getTime();
                yearCalendar.set(Calendar.YEAR, Integer.parseInt(data));
                yearCalendar.set(Calendar.MONTH, 11);
                yearCalendar.set(Calendar.DAY_OF_MONTH, 31);
                Date endOfYear = yearCalendar.getTime();
                String dateOfStartYear = sdfDB.format(startOfYear);
                String dateOfEndYear = sdfDB.format(endOfYear);
                //Обновляем все данные
                periodText = new SimpleDateFormat("yyyy").format(startOfYear) + " год";
                period.setText(periodText);
                sortDate = "BETWEEN '" + dateOfStartYear + "' AND '" + dateOfEndYear + "'";
                break;
        }
        getDataFromDB(categories);
        updateChart();
        updateTexts();
    }

    void updateChart() {
        if (chartTypeFlag) {
            barChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.INVISIBLE);
            barChart.clear();
            barChart.setData(data);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(categoriesText));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1);
            xAxis.setGranularityEnabled(true);

            YAxis yAxisLeft = barChart.getAxisLeft();
            yAxisLeft.setDrawGridLines(true);
            YAxis yAxisRight = barChart.getAxisRight();
            yAxisRight.setEnabled(false);

            data.setBarWidth(0.7f);
            barChart.setDrawBarShadow(false);
            barChart.setDrawValueAboveBar(true);
            barChart.getDescription().setEnabled(false);
            barChart.setDrawGridBackground(false);
            barChart.setScaleEnabled(false);
            barChart.animateY(900);
            barChart.setMaxVisibleValueCount(50);
            barChart.setPinchZoom(false);

            barChart.invalidate();
        } else {
            barChart.setVisibility(View.INVISIBLE);
            pieChart.setVisibility(View.VISIBLE);
            pieChart.clear();
            pieChart.setData(dataPie);

            pieChart.setDrawEntryLabels(false);
            pieChart.getDescription().setEnabled(false);
            pieChart.animateY(900);

            pieChart.invalidate();
        }
    }

    void updateTexts() {
        reportTextSum.setText(String.valueOf(profitableCategory));
        reportTextCategory.setText(String.valueOf(profitableAmount));
        reportTextSumProfit.setText(String.valueOf(expensiveCategory));
        reportTextCategoryProfit.setText(String.valueOf(expensiveAmount));
    }
}