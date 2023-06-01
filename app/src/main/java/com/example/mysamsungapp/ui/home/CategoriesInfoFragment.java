package com.example.mysamsungapp.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mysamsungapp.DBHelper;
import com.example.mysamsungapp.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CategoriesInfoFragment extends Fragment implements OnChangeDate {
    private static final String ARG_IMAGES = "images";
    private static final String ARG_CATEGORIES = "categories";
    ItemCategoryAdapter adapter;
    TextView period, sum;
    int totalSum = 0;
    ArrayList<ItemCategory> items = new ArrayList<>();
    String sortDate = "";
    String todayDate, dateOfStartWeek, dateOfStartMonth, dateOfStartYear;
    String todayText, WeekText, MonthText, YearText;
    private ArrayList<String> categories;
    private ArrayList<Integer> images;
    private OnDeleteOrChangeOperation onDeleteOrChangeOperation;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onDeleteOrChangeOperation = (OnDeleteOrChangeOperation) getParentFragment();
    }

    public CategoriesInfoFragment() {

    }

    public static CategoriesInfoFragment newInstance(ArrayList<Integer> images, ArrayList<String> categories) {
        CategoriesInfoFragment fragment = new CategoriesInfoFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList(ARG_IMAGES, images);
        args.putStringArrayList(ARG_CATEGORIES, categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            images = getArguments().getIntegerArrayList(ARG_IMAGES);
            categories = getArguments().getStringArrayList(ARG_CATEGORIES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_category_fragment, container, false);

        ChipGroup dateFilter = view.findViewById(R.id.chipGroup);
        Chip chipDay = view.findViewById(R.id.chipYear);
        period = view.findViewById(R.id.textViewDate);
        sum = view.findViewById(R.id.textViewSum);

        //Первоначальные настройки
        chipDay.setChecked(true);
        period.setPaintFlags(period.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ListView lv = view.findViewById(R.id.list);
        adapter = new ItemCategoryAdapter(getActivity(), items);
        lv.setAdapter(adapter);

        //Обновляем все данные
        makeDates();
        period.setText(todayText);
        sortDate = "= '" + todayDate + "'";
        updateList();

        //Устанавливаем слушатель на дату для выбора
        period.setOnClickListener(v -> {
            List<Integer> checkedIds = dateFilter.getCheckedChipIds();
            if (checkedIds.size() > 0) {
                switch (dateFilter.indexOfChild(view.findViewById(checkedIds.get(0))) + 1) {
                    case 1:
                        showDatePickerDialog();
                        break;
                    case 2:
                        SetWeekDialog weekDialog = new SetWeekDialog();
                        weekDialog.show(getChildFragmentManager(), "weekDialog");
                        break;
                    case 3:
                        SetMonthDialog monthDialog = new SetMonthDialog();
                        monthDialog.show(getChildFragmentManager(), "monthDialog");
                        break;
                    case 4:
                        SetYearDialog yearDialog = new SetYearDialog();
                        yearDialog.show(getChildFragmentManager(), "yearDialog");
                        break;
                    default:
                        break;
                }
            }
        });

        //устанавливаем слушатель на chipGroup для фильтрации по дате
        dateFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.size() > 0) {
                switch (dateFilter.indexOfChild(view.findViewById(checkedIds.get(0))) + 1) {
                    case 1:
                        sortDate = "= '" + todayDate + "'";
                        period.setText(todayText);
                        break;
                    case 2:
                        sortDate = "BETWEEN '" + dateOfStartWeek + "' AND '" + todayDate + "'";
                        period.setText(WeekText);
                        break;
                    case 3:
                        sortDate = "BETWEEN '" + dateOfStartMonth + "' AND '" + todayDate + "'";
                        period.setText(MonthText);
                        break;
                    case 4:
                        sortDate = "BETWEEN '" + dateOfStartYear + "' AND '" + todayDate + "'";
                        period.setText(YearText);
                        break;
                }
            }
            //Обновляем все данные
            updateList();
        });

        //Открытие activity с операциями по выбранной категории
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getBooleanExtra("deleted", true)) {
                            updateList();
                            onDeleteOrChangeOperation.onDeleteOrChangeOperation();
                        }
                    }
                });
        lv.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(getActivity(), OperationsInfoActivity.class);
            intent.putExtra("sortPeriod", sortDate);
            intent.putExtra("image", images.get(position));
            intent.putExtra("category", categories.get(position));
            activityResultLauncher.launch(intent);
        });

        return view;
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

    @SuppressLint("Range")
    void getDataFromDB() {
        items.clear();
        totalSum = 0;
        SQLiteDatabase db = new DBHelper(getActivity()).getReadableDatabase();
        for (int i = 0; i < images.size(); i++) {
            ItemCategory item = new ItemCategory();
            item.image = images.get(i);
            item.category = categories.get(i);
            String sql = "SELECT amount FROM operations JOIN categories ON operations.category_id = categories.id WHERE categories.name = '" + item.category + "' AND date " + sortDate;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    item.amount += cursor.getInt(cursor.getColumnIndex("amount"));
                } while (cursor.moveToNext());
            } else {
                item.amount = 0;
            }
            totalSum += item.amount;
            cursor.close();
            items.add(item);
        }
        db.close();
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
            updateList();
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onChangeDate(String data, int type) {
        String periodText;
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
                calendar.setTime(date);
                Log.i("WEEK", String.valueOf(calendar.getTime()));
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
                updateList();
                break;
            case 2:
                try {
                    date = new SimpleDateFormat("M yyyy").parse(data);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                calendar.setTime(date);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Date startOfMonth = calendar.getTime();
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date endOfMonth = calendar.getTime();
                //Обновляем все данные
                periodText = new SimpleDateFormat("LLLL yyyy").format(date);
                period.setText(periodText);
                sortDate = "BETWEEN '" + sdfDB.format(startOfMonth) + "' AND '" + sdfDB.format(endOfMonth) + "'";
                updateList();
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
                updateList();
                break;
        }
    }

    void updateList() {
        getDataFromDB();
        String totalSumText = "Итого: " + totalSum + " руб.";
        sum.setText(totalSumText);
        adapter.notifyDataSetChanged();
    }
}