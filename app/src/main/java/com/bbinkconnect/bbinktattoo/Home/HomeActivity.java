package com.bbinkconnect.bbinktattoo.Home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bbinkconnect.bbinktattoo.Login.LoginActivity;
import com.bbinkconnect.bbinktattoo.Share.NextActivity;
import com.bbinkconnect.bbinktattoo.Utils.BottomNavigationViewHelper;

import com.bbinkconnect.bbinktattoo.R;
import com.bbinkconnect.bbinktattoo.Utils.MainFeedListAdapter;
import com.bbinkconnect.bbinktattoo.Utils.UniversalImageLoader;
import com.bbinkconnect.bbinktattoo.Utils.ViewCommentsFragment;
import com.bbinkconnect.bbinktattoo.models.Photo;
import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends AppCompatActivity implements MainFeedListAdapter.OnLoadMoreItemsListener {

    private Context mContext = HomeActivity.this;
    private static final int ACTIVITY_NUM = 1;

    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;
    private ViewPager mViewPager;

    private static final int  CAMERA_REQUEST_CODE = 5;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mFrameLayout = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.relLayoutParent);

        setupBottomNavigationView();
        setupFirebaseAuth();
        initImageLoader();

        HomeFragment fragment = new HomeFragment();
        FragmentTransaction transaction = HomeActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.home_fragment));
        transaction.commit();

    }

    public void opencamera(View View){

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE){

            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");



            Intent intent = new Intent(this, NextActivity.class);
            intent.putExtra(getString(R.string.selected_bitmap), bitmap);
            startActivity(intent);


        }
    }



    public void hideLayout(){

        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    public void showLayout(){

        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(mFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }

    public void onCommentThreadSelected(Photo photo, String callingActivity){

        ViewCommentsFragment fragment  = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void setupFirebaseAuth(){

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out

                }

            }
        };
    }


    private void checkCurrentUser(FirebaseUser user){

        if(user == null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

         mAuth.addAuthStateListener(mAuthListener);
         checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();



         if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
         }
    }

    @Override
    public void onLoadMoreItems() {

        HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        if(fragment != null){
            fragment.displayMorePhotos();
        }
    }
}



