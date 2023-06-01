package com.example.mysamsungapp.ui.home;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mysamsungapp.DBHelper;
import com.example.mysamsungapp.R;
import com.example.mysamsungapp.databinding.FragmentHomeBinding;
import com.example.mysamsungapp.ui.PagerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnAddOperation, OnDeleteOrChangeOperation {
    ArrayList<String> expensesCategories = new ArrayList<>();
    ArrayList<String> incomeCategories = new ArrayList<>();
    ArrayList<Integer> expensesImages = new ArrayList<>();
    ArrayList<Integer> incomeImages = new ArrayList<>();
    private FragmentHomeBinding binding;
    TextView balanceView;

    /*String[] expensesCategories = {"Продукты", "Спорт", "Транспорт", "Образование", "Семья", "Еда", "Прочее"};
    String[] incomeCategories = {"Зарплата", "Подарок", "% по вкладу", "Другое"};
    int[] expensesImages = {R.drawable.products, R.drawable.sport, R.drawable.transport, R.drawable.education, R.drawable.family, R.drawable.food, R.drawable.other};
    int[] incomeImages = {R.drawable.salary, R.drawable.gift, R.drawable.percent, R.drawable.other};*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        balanceView = root.findViewById(R.id.home_balance);
        Button addButton = root.findViewById(R.id.IncomeButton);
        Button removeButton = root.findViewById(R.id.expenseButton);
        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = root.findViewById(R.id.view_pager);

        //Получаем баланс из БД и отображаем его
        getBalance();

        //Получаем все категории
        getCategories();

        addButton.setOnClickListener(v -> {
            AddOperationDialog dialog = AddOperationDialog.newInstance(1, incomeCategories, incomeImages);
            dialog.show(getChildFragmentManager(), "addIncome");
        });
        removeButton.setOnClickListener(v -> {
            AddOperationDialog dialog = AddOperationDialog.newInstance(0, expensesCategories, expensesImages);
            dialog.show(getChildFragmentManager(), "addExpenses");
        });

        //Отображаем фрагменты с расходами и доходами
        PagerAdapter pagerAdapter = new PagerAdapter(this);
        pagerAdapter.addFragment(CategoriesInfoFragment.newInstance(expensesImages, expensesCategories), "Расходы");
        pagerAdapter.addFragment(CategoriesInfoFragment.newInstance(incomeImages, incomeCategories), "Доходы");
        viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(pagerAdapter.getPageTitle(position))
        ).attach();

        return root;
    }

    @SuppressLint("Range")
    public void getBalance() {
        SQLiteDatabase db = new DBHelper(getActivity()).getReadableDatabase();
        String sql = "SELECT amount, type FROM operations";
        Cursor cursor = db.rawQuery(sql, null);
        int db_balance = 0;
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndex("type")) == 1) {
                    db_balance += cursor.getInt(cursor.getColumnIndex("amount"));
                } else {
                    db_balance -= cursor.getInt(cursor.getColumnIndex("amount"));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        String balance = db_balance + " руб.";
        balanceView.setText(balance);
    }

    @SuppressLint("Range")
    public void getCategories() {
        expensesCategories.clear();
        expensesImages.clear();
        incomeCategories.clear();
        incomeImages.clear();
        SQLiteDatabase db = new DBHelper(getActivity()).getReadableDatabase();
        String sql = "SELECT name, image, type FROM categories";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndex("type")) == 1) {
                    expensesCategories.add(cursor.getString(cursor.getColumnIndex("name")));
                    expensesImages.add(cursor.getInt(cursor.getColumnIndex("image")));
                } else {
                    incomeCategories.add(cursor.getString(cursor.getColumnIndex("name")));
                    incomeImages.add(cursor.getInt(cursor.getColumnIndex("image")));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAddOperation() {
        Snackbar.make(requireView(), "Операция успешно добавлена!", Snackbar.LENGTH_LONG).show();
        getBalance();
    }

    @Override
    public void onDeleteOrChangeOperation() {
        getBalance();
    }
}