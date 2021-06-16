package com.akapps.puzka;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.jetbrains.annotations.NotNull;

public class RoutineAdjust extends Fragment {
    ViewPager2 viewPager2;
    RadioGroup radioGroup;
    Context myContext;
    FragChildCalendar fragChildCalendar;
    FragChildRegular fragChildRegular;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_routineadjust, container ,false);
        viewPager2 = view.findViewById(R.id.viewpager);
        radioGroup = view.findViewById(R.id.radiogrp);
        viewPager2.setAdapter(new ViewpagerHandler(getChildFragmentManager(), getLifecycle()));
        fragChildCalendar = new FragChildCalendar();
        fragChildRegular = new FragChildRegular();

        return view;
    }

    class ViewpagerHandler extends FragmentStateAdapter{


        public ViewpagerHandler(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @NotNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 0){
                return fragChildCalendar;
            }
            return fragChildRegular;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        myContext = context;
    }
}
