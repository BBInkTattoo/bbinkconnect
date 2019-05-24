package com.bbinkconnect.bbinktattoo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.bbinkconnect.bbinktattoo.fragments.ChatNotificationFragment;
import com.bbinkconnect.bbinktattoo.fragments.HomeFragment;
import com.bbinkconnect.bbinktattoo.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private Fragment selectedfragment = null;
    boolean shouldExecuteOnResume;
    private DatabaseReference referencechat;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shouldExecuteOnResume = false;

        ImageButton ClickImageButton = findViewById(R.id.btnLaunchCamera);
        ImageButton ClickImageButton2 = findViewById(R.id.imageButton2);
        BottomNavigationView bottom_navigation = findViewById(R.id.bottom_navigation);
        SearchView searchView = findViewById(R.id.searchView_home);
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottom_navigation.getMenu().findItem(R.id.home).setChecked(true);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referencechat = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        ClickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
        });

        ClickImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OptionsActivity.class));
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.chat_notification:
                            selectedfragment = new ChatNotificationFragment();
                            break;
                        case R.id.home:
                            selectedfragment = new HomeFragment();
                            break;
                        case R.id.profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("profileid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                            editor.apply();
                            selectedfragment = new ProfileFragment();
                            break;
                    }
                    if (selectedfragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedfragment).commit();
                    }
                    return true;
                }
            };


    private void status(String status){
        referencechat = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        referencechat.updateChildren(hashMap);
    }

        @Override
    public void onResume() {
        super.onResume();
            if(shouldExecuteOnResume){
                status("online");
            } else{
                shouldExecuteOnResume = true;
            }
    }

    @Override
    public void onPause() {
        super.onPause();
        status("offline");
    }
}




