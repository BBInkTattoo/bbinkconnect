package com.bbinkconnect.bbinktattoo.Home;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bbinkconnect.bbinktattoo.ChatNotification.ChatFragment;
import com.bbinkconnect.bbinktattoo.ChatNotification.NotificationFragment;
import com.bbinkconnect.bbinktattoo.R;
import com.bbinkconnect.bbinktattoo.Utils.BottomNavigationViewHelper;
import com.bbinkconnect.bbinktattoo.Utils.SectionsPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.Objects;


public class ChatNotificationActivity extends AppCompatActivity {

    private Context mContext = ChatNotificationActivity.this;
    private static final int ACTIVITY_NUM = 0;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_notification);
        mViewPager = findViewById(R.id.pager);

        setupBottomNavigationView();
        setupViewPager();
    }

    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragment()); //index 0
        adapter.addFragment(new NotificationFragment()); //index 1
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(R.string.chat);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(R.string.notification);

    }

    private void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
