package com.example.mysamsungapp.ui.statistics;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mysamsungapp.DBHelper;
import com.example.mysamsungapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class GeneralChartFragment extends Fragment {
    private BarChart barChart;
    String expensiveDate, profitableDate;
    int profitableAmount, expensiveAmount;
    BarData data;
    ArrayList<String> years = new ArrayList<>();

    public GeneralChartFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_general_chart_fragment, container, false);

        ChipGroup chipGroup = view.findViewById(R.id.chipGroupGeneral);
        Chip chipYear = view.findViewById(R.id.chipYearGeneral);
        barChart = view.findViewById(R.id.chartYear);
        TextView reportText = view.findViewById(R.id.reportText);
        TextView reportTextDate = view.findViewById(R.id.reportTextDate);
        TextView reportTextAmount = view.findViewById(R.id.reportTextAmount);
        TextView reportTextProfit = view.findViewById(R.id.reportProfitText);
        TextView reportTextDateProfit = view.findViewById(R.id.reportProfitTextDate);
        TextView reportTextAmountProfit = view.findViewById(R.id.reportProfitTextAmount);

        //Устанавливаем первоначальные настройки
        chipYear.setChecked(true);
        getDataFromDB(1);
        updateBarChart();
        reportText.setText("Самый убыточный год: ");
        reportTextDate.setText(expensiveDate);
        String amountStringYear = expensiveAmount + " руб.";
        reportTextAmount.setText(amountStringYear);
        reportTextProfit.setText("Самый прибыльный год: ");
        reportTextDateProfit.setText(profitableDate);
        amountStringYear = profitableAmount + " руб.";
        reportTextAmountProfit.setText(amountStringYear);

        //Устанавливаем слушатель на chipGroup для выбора типа barChart
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.size() > 0) {
                int checkedChip = chipGroup.indexOfChild(view.findViewById(checkedIds.get(0))) + 1;
                getDataFromDB(checkedChip);
                updateBarChart();
                String amountString;
                switch (checkedChip) {
                    case 1:
                        reportText.setText("Самый убыточный год: ");
                        reportTextDate.setText(expensiveDate);
                        amountString = expensiveAmount + " руб.";
                        reportTextAmount.setText(amountString);
                        reportTextProfit.setText("Самый прибыльный год: ");
                        reportTextDateProfit.setText(profitableDate);
                        amountString = profitableAmount + " руб.";
                        reportTextAmountProfit.setText(amountString);
                        break;
                    case 2:
                        reportText.setText("Самый убыточный месяц: ");
                        reportTextDate.setText(expensiveDate);
                        amountString = expensiveAmount + " руб.";
                        reportTextAmount.setText(amountString);
                        reportTextProfit.setText("Самый прибыльный месяц: ");
                        reportTextDateProfit.setText(profitableDate);
                        amountString = profitableAmount + " руб.";
                        reportTextAmountProfit.setText(amountString);
                        break;
                    case 3:
                        reportText.setText("Самая убыточный неделя: ");
                        reportTextDate.setText(expensiveDate);
                        amountString = expensiveAmount + " руб.";
                        reportTextAmount.setText(amountString);
                        reportTextProfit.setText("Самая прибыльная неделя: ");
                        reportTextDateProfit.setText(profitableDate);
                        amountString = profitableAmount + " руб.";
                        reportTextAmountProfit.setText(amountString);
                }
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("Range")
    public void getDataFromDB(int checkedChip) {
        ArrayList<BarEntry> expensesData = new ArrayList<>();
        ArrayList<BarEntry> incomeData = new ArrayList<>();
        ArrayList<BarEntry> profitData = new ArrayList<>();
        ArrayList<BarEntry> lossData = new ArrayList<>();
        SQLiteDatabase db = new DBHelper(getActivity()).getReadableDatabase();
        String sql = "SELECT MIN(date) as date FROM operations";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
            Date minYearDate;
            try {
                minYearDate = sdfDate.parse(cursor.getString(cursor.getColumnIndex("date")));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            int startYear = Integer.parseInt(sdfYear.format(Objects.requireNonNull(minYearDate)));
            int endYear = Integer.parseInt(sdfYear.format(Calendar.getInstance().getTime()));

            years.clear();
            expensiveAmount = 0;
            profitableAmount = 0;
            switch (checkedChip) {
                case 1:
                    if (endYear - startYear < 3) {
                        startYear = endYear - 3;
                    }
                    for (int year = startYear; year <= endYear; year++) {
                        String yearPeriod = "BETWEEN '" + year + "-01-01" + "' AND '" + year + "-12-31" + "'";
                        sql = "SELECT amount, type FROM operations WHERE date " + yearPeriod;
                        cursor = db.rawQuery(sql, null);
                        int expenses = 0;
                        int incomes = 0;
                        int profit = 0;
                        int loss = 0;
                        if (cursor.moveToFirst()) {
                            do {
                                if (cursor.getInt(cursor.getColumnIndexOrThrow("type")) == 1) {
                                    incomes += cursor.getInt(cursor.getColumnIndexOrThrow("amount"));
                                } else {
                                    expenses += cursor.getInt(cursor.getColumnIndexOrThrow("amount"));
                                }
                            } while (cursor.moveToNext());
                        }

                        //Получаем прибыль и убыток
                        if (incomes > expenses) {
                            profit = incomes - expenses;
                        } else {
                            loss = expenses - incomes;
                        }

                        //Получаем самый убыточный и прибыльный год
                        if (expensiveAmount < loss) {
                            expensiveAmount = loss;
                            expensiveDate = year + " год";
                        }
                        if (profitableAmount < profit) {
                            profitableAmount = profit;
                            profitableDate = year + " год";
                        }

                        expensesData.add(new BarEntry(year, expenses));
                        incomeData.add(new BarEntry(year, incomes));
                        profitData.add(new BarEntry(year, profit));
                        lossData.add(new BarEntry(year, loss));
                        years.add(String.valueOf(year));
                    }
                    break;
                case 2:
                    int MonthCount = 0;
                    for (int year = startYear; year <= endYear; year++) {
                        for (int i = 1; i <= 12; i++) {
                            MonthCount++;
                            String month;
                            if (i < 10) {
                                month = "0" + i;
                            } else {
                                month = String.valueOf(i);
                            }
                            YearMonth yearMonth = YearMonth.of(year, i);
                            LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String lastDayOfMonthString = lastDayOfMonth.format(formatter);

                            String monthPeriod = "BETWEEN '" + year + "-" + month + "-01" + "' AND '" + lastDayOfMonthString + "'";
                            sql = "SELECT amount, type FROM operations WHERE date " + monthPeriod;
                            cursor = db.rawQuery(sql, null);
                            int expenses = 0;
                            int incomes = 0;
                            int profit = 0;
                            int loss = 0;
                            if (cursor.moveToFirst()) {
                                do {
                                    if (cursor.getInt(cursor.getColumnIndexOrThrow("type")) == 1) {
                                        incomes += cursor.getInt(cursor.getColumnIndexOrThrow("amount"));
                                    } else {
                                        expenses += cursor.getInt(cursor.getColumnIndexOrThrow("amount"));
                                    }
                                } while (cursor.moveToNext());
                            }

                            //Получаем прибыль и убыток
                            if (incomes > expenses) {
                                profit = incomes - expenses;
                            } else {
                                loss = expenses - incomes;
                            }

                            //Получаем самый убыточный и прибыльный месяц
                            if (expensiveAmount < loss) {
                                expensiveAmount = loss;
                                expensiveDate = Month.of(i).getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")) + ", " + year;
                            }
                            if (profitableAmount < profit) {
                                profitableAmount = profit;
                                profitableDate = Month.of(i).getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")) + ", " + year;
                            }

                            expensesData.add(new BarEntry(MonthCount, expenses));
                            incomeData.add(new BarEntry(MonthCount, incomes));
                            profitData.add(new BarEntry(MonthCount, profit));
                            lossData.add(new BarEntry(MonthCount, loss));
                            String monthName = Month.of(i).getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
                            years.add(monthName.substring(0, 1).toUpperCase() + monthName.substring(1) + " " + year);

                            if (lastDayOfMonth.isAfter(LocalDate.now())) {
                                break;
                            }
                        }
                    }
                    break;
                case 3:
                    int WeekCount = 0;
                    for (int year = startYear; year <= endYear; year++) {
                        for (int i = 1; i <= 12; i++) {
                            YearMonth yearMonth = YearMonth.of(year, i);
                            LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
                            String month;
                            if (i < 10) {
                                month = "0" + i;
                            } else {
                                month = String.valueOf(i);
                            }
                            LocalDate WeekDate = LocalDate.of(year, i, 1);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
                            while (WeekDate.getMonthValue() == i) {
                                LocalDate startOfWeek = WeekDate.with(DayOfWeek.MONDAY);
                                LocalDate endOfWeek = WeekDate.with(DayOfWeek.SUNDAY);
                                if (new Date().before(Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                                    break;
                                }
                                if (endOfWeek.plusDays(1).getMonthValue() != i) {
                                    break;
                                }
                                String StartOfWeekString = startOfWeek.format(formatter);
                                String EndOfWeekString = endOfWeek.format(formatter);
                                String period = "BETWEEN '" + year + "-" + month + "-" + StartOfWeekString + "' AND '" + year + "-" + month + "-" + EndOfWeekString + "'";
                                sql = "SELECT amount, type FROM operations WHERE date " + period;
                                cursor = db.rawQuery(sql, null);
                                int expenses = 0;
                                int incomes = 0;
                                int profit = 0;
                                int loss = 0;
                                if (cursor.moveToFirst()) {
                                    do {
                                        if (cursor.getInt(cursor.getColumnIndexOrThrow("type")) == 1) {
                                            incomes += cursor.getInt(cursor.getColumnIndexOrThrow("amount"));
                                        } else {
                                            expenses += cursor.getInt(cursor.getColumnIndexOrThrow("amount"));
                                        }
                                    } while (cursor.moveToNext());
                                }

                                //Получаем прибыль и убыток
                                if (incomes > expenses) {
                                    profit = incomes - expenses;
                                } else {
                                    loss = expenses - incomes;
                                }

                                //Получаем самую убыточную и прибыльную неделю
                                if (expensiveAmount < loss) {
                                    expensiveAmount = loss;
                                    expensiveDate = String.valueOf(year);
                                }
                                if (profitableAmount < profit) {
                                    profitableAmount = profit;
                                    profitableDate = String.valueOf(year);
                                }

                                expensesData.add(new BarEntry(WeekCount, expenses));
                                incomeData.add(new BarEntry(WeekCount, incomes));
                                profitData.add(new BarEntry(WeekCount, profit));
                                lossData.add(new BarEntry(WeekCount, loss));
                                years.add(startOfWeek.format(formatter) + "." + month + " " + year);
                                WeekDate = endOfWeek.plusDays(1);
                            }
                            WeekCount++;
                            if (lastDayOfMonth.isAfter(LocalDate.now())) {
                                break;
                            }
                        }
                    }
                    break;
                case 4:
                    break;
            }
        }
        cursor.close();
        db.close();

        BarDataSet set1, set2, set3, set4;
        set1 = new BarDataSet(expensesData, "Расходы");
        set1.setColor(Color.rgb(104, 241, 175));
        set2 = new BarDataSet(incomeData, "Доходы");
        set2.setColor(Color.rgb(164, 228, 251));
        set3 = new BarDataSet(profitData, "Прибыль");
        set3.setColor(Color.rgb(224, 208, 255));
        set4 = new BarDataSet(lossData, "Убыток");
        set4.setColor(Color.rgb(224, 20, 120));
        data = new BarData(set1, set2, set3, set4);
    }

    void updateBarChart() {
        barChart.clear();
        barChart.setData(data);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(years));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(4);

        float barSpace = 0.05f;
        float groupSpace = 0.16f;
        data.setBarWidth(0.16f);

        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * years.size());
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(900);
        barChart.setScaleEnabled(false);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.moveViewToX(barChart.getData().getXMax());
        barChart.invalidate();
    }
}