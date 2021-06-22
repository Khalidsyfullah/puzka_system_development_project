package com.akapps.puzka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

public class WaletCenterpage extends AppCompatActivity {
    TabLayout tabLayout;
    TextView textView;
    ViewPager2 viewPager2;
    String[] strings = new String[]{"Transactions", "Summary"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material6);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walet_centerpage);
        tabLayout = findViewById(R.id.tablayout);
        textView = findViewById(R.id.textView73);
        ViewStateAdapter viewStateAdapter = new ViewStateAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2 = findViewById(R.id.viewpage);
        viewPager2.setAdapter(viewStateAdapter);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
                textView.setText(strings[position]);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private static class ViewStateAdapter extends FragmentStateAdapter {

        public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }



        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new FragTransactions();
            }
            return new FragSummary();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}