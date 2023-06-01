package com.example.mysamsungapp.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.mysamsungapp.DBHelper;
import com.example.mysamsungapp.R;
import com.example.mysamsungapp.ui.SpinnerAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class OperationsInfoActivity extends AppCompatActivity {
    TextView infoText;
    Spinner sortType;
    String sql = "";
    ListView studentList;
    String categoryName;
    int categoryImage;
    ArrayList<ItemOperation> items = new ArrayList<>();
    ItemOperationAdapter listAdapter;
    boolean isChanged = false;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_operations_activity);

        Toolbar toolbar = findViewById(R.id.toolBar);
        infoText = findViewById(R.id.infoText);
        sortType = findViewById(R.id.sortType);
        studentList = findViewById(R.id.operationsList);

        String sortDate = getIntent().getStringExtra("sortPeriod");
        categoryName = getIntent().getStringExtra("category");
        categoryImage = getIntent().getIntExtra("image", 0);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(categoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listAdapter = new ItemOperationAdapter(this, items);
        studentList.setAdapter(listAdapter);

        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.cash);
        images.add(R.drawable.calendar);
        ArrayList<String> names = new ArrayList<>();
        names.add("по сумме");
        names.add("по дате");
        SpinnerAdapter adapter = new SpinnerAdapter(this, images, names, false);
        sortType.setAdapter(adapter);
        sortType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Формируем запрос в зависимости от выбранного типа сортировки
                switch (position) {
                    case 0:
                        sql = "SELECT operations.id, amount, date, description FROM operations JOIN categories ON operations.category_id = categories.id " +
                                "WHERE categories.name = '" + categoryName + "' AND date " + sortDate + " ORDER BY amount DESC";
                        break;
                    case 1:
                        sql = "SELECT operations.id, amount, date, description FROM operations JOIN categories ON operations.category_id = categories.id " +
                                "WHERE categories.name = '" + categoryName + "' AND date " + sortDate + " ORDER BY date DESC";
                        break;
                }
                //Делаем запрос к БД и обновляем список
                getDataFromDB();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @SuppressLint("Range")
    public void getDataFromDB() {
        SQLiteDatabase db = new DBHelper(getApplicationContext()).getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        items.clear();
        if (cursor.moveToNext()) {
            do {
                items.add(new ItemOperation(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        categoryImage,
                        categoryName,
                        cursor.getString(cursor.getColumnIndex("description")),
                        cursor.getString(cursor.getColumnIndex("date")),
                        cursor.getInt(cursor.getColumnIndex("amount"))
                ));
            } while (cursor.moveToNext());
        } else {
            sortType.setOnItemSelectedListener(null);
            sortType.setVisibility(View.GONE);
            infoText.setText("Операций по данной категории пока нет");
        }
        listAdapter.notifyDataSetChanged();
        cursor.close();
        db.close();
    }

    @SuppressLint("Range")
    public void onChangeItem(int id, int amount, String category, String date, String description) {
        //Получаем category_id для обновления операции
        SQLiteDatabase dbCategory = new DBHelper(getApplicationContext()).getReadableDatabase();
        Cursor cursorCategory = dbCategory.rawQuery("SELECT id FROM categories WHERE name = '" + category + "'", null);
        int categoryId = 0;
        if (cursorCategory.moveToFirst()) { // Проверяем, есть ли строки в курсоре
            categoryId = cursorCategory.getInt(cursorCategory.getColumnIndex("id")); // Получаем category_id
        }
        cursorCategory.close();
        dbCategory.close();
        //Обновляем операцию в БД
        SQLiteDatabase db = new DBHelper(getApplicationContext()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("date", date);
        values.put("description", description);
        values.put("category_id", categoryId);
        db.update("operations", values, "id =?", new String[]{String.valueOf(id)});
        Snackbar.make(studentList, "Операция успешно отредактирована!", Snackbar.LENGTH_LONG).show();
        //Обновляем список
        getDataFromDB();
        isChanged = true;
    }

    public void onDeleteItem(int id) {
        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        if (db.delete("operations", "id =?", new String[]{String.valueOf(id)}) != 0) {
            Snackbar.make(studentList, "Операция успешно удалена!", Snackbar.LENGTH_LONG).show();
            int position = 0;
            for (ItemOperation item : items) {
                if (item.id == id) {
                    break;
                }
                position++;
            }
            items.remove(position);
            listAdapter.notifyDataSetChanged();
            isChanged = true;
        } else {
            Snackbar.make(studentList, "Произошла непредвиденная ошибка!", Snackbar.LENGTH_LONG).show();
        }
        db.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("deleted", isChanged);
            setResult(RESULT_OK, intent);
            finish();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("deleted", isChanged);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}