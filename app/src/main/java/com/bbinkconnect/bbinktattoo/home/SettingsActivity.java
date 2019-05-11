package com.bbinkconnect.bbinktattoo.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bbinkconnect.bbinktattoo.R;
import com.bbinkconnect.bbinktattoo.settings.AppInfoFragment;
import com.bbinkconnect.bbinktattoo.settings.EditProfileFragment;
import com.bbinkconnect.bbinktattoo.settings.FAQFragment;
import com.bbinkconnect.bbinktattoo.settings.SignOutFragment;
import com.bbinkconnect.bbinktattoo.settings.UploadTattooFragment;
import com.bbinkconnect.bbinktattoo.utils.FirebaseMethods;
import com.bbinkconnect.bbinktattoo.utils.SectionsStatePagerAdapter;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {


    private Context mContext;

    private ViewPager mViewPager;
    public SectionsStatePagerAdapter pagerAdapter;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = SettingsActivity.this;

        mViewPager = findViewById(R.id.viewpager_container);
        mRelativeLayout = findViewById(R.id.relLayout1);

        setupSettingsList();
        setupFragments();
        getIncomingIntent();

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setupFragments(){
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new AppInfoFragment(), getString(R.string.app_info_fragment)); //fragment 0
        pagerAdapter.addFragment(new UploadTattooFragment(), getString(R.string.upload_tattoo_fragment));//fragment 1
        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment));//fragment 2
        pagerAdapter.addFragment(new FAQFragment(), getString(R.string.faq_fragment));//fragment 3
        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_info_fragment));//fragment 4

    }

    public void setViewPager(int fragmentNumber){
        mRelativeLayout.setVisibility(View.GONE);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }

    private void setupSettingsList(){
        ListView listView = findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.app_info_fragment)); //fragment 0
        options.add(getString(R.string.upload_tattoo_fragment)); //fragment 1
        options.add(getString(R.string.edit_profile_fragment)); //fragment 2
        options.add(getString(R.string.faq_fragment)); //fragment 3
        options.add(getString(R.string.sign_out_info_fragment)); //fragment 4

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, options);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setViewPager(position);
            }
        });

    }

    private void getIncomingIntent(){
        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.selected_image))
                || intent.hasExtra(getString(R.string.selected_bitmap))){

            //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment

            if(intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))){

                if(intent.hasExtra(getString(R.string.selected_image))){
                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(SettingsActivity.this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            intent.getStringExtra(getString(R.string.selected_image)), null);
                }
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(SettingsActivity.this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            null,(Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));
                }

            }

        }

        if(intent.hasExtra(getString(R.string.calling_activity))){

            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }
}
