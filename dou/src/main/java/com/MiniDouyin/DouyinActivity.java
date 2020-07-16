package com.MiniDouyin;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DouyinActivity extends AppCompatActivity {

    private final static int PERMISSION_REQUEST_CODE = 666;

    String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean checkPermission(String[] spermissions){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            for(String permission:spermissions){
                if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(DouyinActivity.this, spermissions,PERMISSION_REQUEST_CODE);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douyin);
        checkPermission(permissions);




        ViewPager pager = findViewById(R.id.view_pager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                if(i == 0)
                {
                    return new VideoFragment();
                }
                else
                {
                    return new IFragment();
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if(position == 0){
                    return "首页";
                }
                else {
                    return "我";
                }
            }
        });
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);
    }


}

