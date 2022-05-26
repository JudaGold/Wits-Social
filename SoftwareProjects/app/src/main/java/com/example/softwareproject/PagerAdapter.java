package com.example.softwareproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class PagerAdapter extends FragmentPagerAdapter {
    public final ArrayList<Fragment>fragmentArrayList = new ArrayList<>();
    public final ArrayList<String>fragmentTitleList = new ArrayList<>();

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public Fragment getItem(int pos){
     return fragmentArrayList.get(pos);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public void addFragmentTitle(Fragment fragment,String title){
        fragmentArrayList.add(fragment);
        fragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position){
        return fragmentTitleList.get(position);
    }

}
