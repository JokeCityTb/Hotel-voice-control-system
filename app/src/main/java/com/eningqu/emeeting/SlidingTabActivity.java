package com.eningqu.emeeting;

import android.Manifest;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;


import com.eningqu.emeeting.bean.TabEntity;
import com.eningqu.emeeting.fragment.LanguageFragment;
import com.eningqu.emeeting.fragment.SettingsFragment;
import com.eningqu.emeeting.utils.ViewFindUtils;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;


public class SlidingTabActivity extends AppCompatActivity {


    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private final String[] mTitles = {"会议翻译", "设置"};
    private MyPagerAdapter mAdapter;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private CommonTabLayout tabLayout_3 = null;
    private ViewPager mViewPager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_tab);

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], 0, 0));
        }
        mFragments.add(LanguageFragment.getInstance(mTitles[0]));
        mFragments.add(SettingsFragment.getInstance(mTitles[1]));

        View decorView = getWindow().getDecorView();
        mViewPager = ViewFindUtils.find(decorView, R.id.main_weihan_vp);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        tabLayout_3 = ViewFindUtils.find(decorView, R.id.main_weihan_tab);

        tabLayout_3.setTabData(mTabEntities);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout_3.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout_3.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        mViewPager.setCurrentItem(0);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to READ_PHONE_STATE - requesting it");
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};

                requestPermissions(permissions, 1);

            }
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to RECORD_AUDIO - requesting it");
                String[] permissions = {Manifest.permission.RECORD_AUDIO};

                requestPermissions(permissions, 2);

            }
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("onKeyDown", "keyCode：" + keyCode);

        return false;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }








}


