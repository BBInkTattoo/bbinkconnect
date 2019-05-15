package com.bbinkconnect.bbinktattoo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.bbinkconnect.bbinktattoo.home.ChatNotificationActivity;
import com.bbinkconnect.bbinktattoo.home.HomeActivity;
import com.bbinkconnect.bbinktattoo.home.ProfileActivity;
import com.bbinkconnect.bbinktattoo.R;
import android.support.design.widget.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;



public class BottomNavigationViewHelper {


    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.chat_notification:
                        Intent intent1 = new Intent(context, ChatNotificationActivity.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.home:
                        Intent intent2  = new Intent(context, HomeActivity.class);//ACTIVITY_NUM = 1
                        context.startActivity(intent2);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.profile:
                        Intent intent3 = new Intent(context, ProfileActivity.class);//ACTIVITY_NUM = 2
                        context.startActivity(intent3);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                }

                return false;
            }
        });
    }
}