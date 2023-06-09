package com.example.mysamsungapp.ui.categories;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mysamsungapp.DBHelper;
import com.example.mysamsungapp.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ActionCategoriesFragment extends Fragment implements OnActionCategory {
    private int type;
    ArrayList<ItemCategories> items = new ArrayList<>();
    ItemCategoriesAdapter adapter;
    ListView listView;

    public ActionCategoriesFragment() {

    }

    public static ActionCategoriesFragment newInstance(int type) {
        ActionCategoriesFragment fragment = new ActionCategoriesFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categories_action_fragment, container, false);

        listView = view.findViewById(R.id.categoriesListView);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);

        getCategories();

        //Изображения для категорий
        int[] imagesExpenses = new int[]{R.drawable.products, R.drawable.food, R.drawable.education, R.drawable.family, R.drawable.sport, R.drawable.airplane,
                R.drawable.metro, R.drawable.gas_station, R.drawable.ship, R.drawable.wine, R.drawable.taxi, R.drawable.other};
        int[] imagesIncomes = new int[]{R.drawable.gift, R.drawable.salary, R.drawable.percent, R.drawable.other};

        //Формируем список всех категорий
        adapter = new ItemCategoriesAdapter(requireActivity(), items, getChildFragmentManager(), type, imagesExpenses, imagesIncomes);
        listView.setAdapter(adapter);

        buttonAdd.setOnClickListener(v -> {
            AddCategoryDialog addCategoryDialog = AddCategoryDialog.newInstance(type, imagesExpenses, imagesIncomes);
            addCategoryDialog.show(getChildFragmentManager(), "addCategory");
        });

        return view;
    }

    @SuppressLint("Range")
    public void getCategories() {
        SQLiteDatabase db = new DBHelper(getContext()).getReadableDatabase();
        String sql = "SELECT * FROM categories WHERE type = '" + type + "'";
        Cursor cursor = db.rawQuery(sql, null);
        items.clear();
        int id = 0;
        String name = "";
        int image = 0;
        int type = 0;
        boolean flag = true;
        int count = 1;
        if (cursor.moveToNext()) {
            do {
                if (flag) {
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                    name = cursor.getString(cursor.getColumnIndex("name"));
                    image = cursor.getInt(cursor.getColumnIndex("image"));
                    type = cursor.getInt(cursor.getColumnIndex("type"));
                    if (count == cursor.getCount()) {
                        items.add(new ItemCategories(cursor.getInt(cursor.getColumnIndex("id")), 0,
                                cursor.getString(cursor.getColumnIndex("name")), "",
                                cursor.getInt(cursor.getColumnIndex("image")), 0,
                                cursor.getInt(cursor.getColumnIndex("type")), 0)
                        );
                    }
                } else {
                    items.add(new ItemCategories(id, cursor.getInt(cursor.getColumnIndex("id")),
                            name, cursor.getString(cursor.getColumnIndex("name")),
                            image, cursor.getInt(cursor.getColumnIndex("image")),
                            type, cursor.getInt(cursor.getColumnIndex("type")))
                    );
                }
                flag = !flag;
                count++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    @Override
    public void onDelete(int id) {
        SQLiteDatabase db = new DBHelper(getContext()).getWritableDatabase();
        if (db.delete("categories", "id =?", new String[]{String.valueOf(id)}) != 0) {
            Snackbar.make(listView, "Операция успешно удалена!", Snackbar.LENGTH_LONG).show();
            getCategories();
            adapter.notifyDataSetChanged();
        } else {
            Snackbar.make(listView, "Произошла непредвиденная ошибка!", Snackbar.LENGTH_LONG).show();
        }
        db.close();
    }

    @Override
    public void onAdd() {
        getCategories();
        adapter.notifyDataSetChanged();
        Snackbar.make(listView, "Категория успешно удалена", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onChange() {
        getCategories();
        adapter.notifyDataSetChanged();
        Snackbar.make(listView, "Категория успешно отредактирована", Snackbar.LENGTH_LONG).show();
    }
}
