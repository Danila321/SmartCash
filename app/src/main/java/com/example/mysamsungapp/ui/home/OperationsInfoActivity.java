package com.example.mysamsungapp.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class OperationsInfoActivity extends AppCompatActivity {
    String sql = "";
    ListView studentList;
    String categoryName;
    int categoryImage;
    ArrayList<ItemOperation> items = new ArrayList<>();
    ItemOperationAdapter listAdapter;
    String[] spinnerList = {"по сумме", "по дате"};
    boolean isChanged = false;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_operations_activity);

        Toolbar toolbar = findViewById(R.id.toolBar);
        TextView infoText = findViewById(R.id.infoText);
        Spinner sortType = findViewById(R.id.sortType);
        studentList = findViewById(R.id.operationsList);

        String sortDate = getIntent().getStringExtra("sortPeriod");
        categoryName = getIntent().getStringExtra("category");
        categoryImage = getIntent().getIntExtra("image", 0);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(categoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listAdapter = new ItemOperationAdapter(this, items);
        studentList.setAdapter(listAdapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortType.setAdapter(adapter);
        sortType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        sql = "SELECT id, amount, date, description FROM operations WHERE category = '" + categoryName + "' AND date " + sortDate + " ORDER BY amount DESC";
                        break;
                    case 1:
                        sql = "SELECT id, amount, date, description FROM operations WHERE category = '" + categoryName + "' AND date " + sortDate + " ORDER BY date DESC";
                        break;
                }
                SQLiteDatabase db = new DBHelper(getApplicationContext()).getReadableDatabase();
                Cursor cursor = db.rawQuery(sql, null);
                if (cursor.moveToNext()) {
                    items.clear();
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
                    listAdapter.notifyDataSetChanged();
                } else {
                    sortType.setOnItemSelectedListener(null);
                    sortType.setVisibility(View.GONE);
                    infoText.setText("Операций по данной категории пока нет");
                }
                cursor.close();
                db.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @SuppressLint("Range")
    public void onChangeItem(int id, int amount, String category, String date, String description) {
        SQLiteDatabase db = new DBHelper(getApplicationContext()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("category", category);
        values.put("date", date);
        values.put("description", description);
        db.update("operations", values, "id =?", new String[]{String.valueOf(id)});
        db = new DBHelper(getApplicationContext()).getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            items.clear();
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
        }
        db.close();
        Snackbar.make(studentList, "Операция успешно отредактирована!", Snackbar.LENGTH_LONG).show();
        listAdapter.notifyDataSetChanged();
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