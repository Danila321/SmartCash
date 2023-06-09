package com.example.mysamsungapp.ui.statistics;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mysamsungapp.DBHelper;
import com.example.mysamsungapp.R;
import com.example.mysamsungapp.databinding.FragmentStatisticsBinding;
import com.example.mysamsungapp.ui.PagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class StatisticsFragment extends Fragment {
    private FragmentStatisticsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView infoTextChart = root.findViewById(R.id.InfoTextChart);
        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = root.findViewById(R.id.StatView);

        SQLiteDatabase db = new DBHelper(getContext()).getReadableDatabase();
        String sql = "SELECT id from operations";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()){
            infoTextChart.setVisibility(View.INVISIBLE);
            //Отображаем фрагменты со статистикой расходов и доходов и общую
            PagerAdapter pagerAdapter = new PagerAdapter(this);
            pagerAdapter.addFragment(new GeneralChartFragment(), "Общее");
            pagerAdapter.addFragment(ExpensesIncomeChartFragment.newInstance(1), "Расходы");
            pagerAdapter.addFragment(ExpensesIncomeChartFragment.newInstance(2), "Доходы");
            viewPager.setAdapter(pagerAdapter);
            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> tab.setText(pagerAdapter.getPageTitle(position))
            ).attach();
        } else {
            tabLayout.setVisibility(View.INVISIBLE);
            viewPager.setVisibility(View.INVISIBLE);
        }
        cursor.close();



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}