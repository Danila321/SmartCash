package com.example.mysamsungapp.ui.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mysamsungapp.R;
import com.example.mysamsungapp.databinding.FragmentCategoriesBinding;
import com.example.mysamsungapp.databinding.FragmentSettingsBinding;
import com.example.mysamsungapp.ui.PagerAdapter;
import com.example.mysamsungapp.ui.statistics.ExpensesIncomeChartFragment;
import com.example.mysamsungapp.ui.statistics.GeneralChartFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CategoriesFragment extends Fragment {
    private FragmentCategoriesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TabLayout tabLayout = root.findViewById(R.id.tabLayoutCategories);
        ViewPager2 viewPager = root.findViewById(R.id.ViewPager);

        //Отображаем фрагменты со статистикой расходов и доходов и общую
        PagerAdapter pagerAdapter = new PagerAdapter(this);
        pagerAdapter.addFragment(ActionCategoriesFragment.newInstance(1), "Расходы");
        pagerAdapter.addFragment(ActionCategoriesFragment.newInstance(2), "Доходы");
        viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(pagerAdapter.getPageTitle(position))
        ).attach();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
