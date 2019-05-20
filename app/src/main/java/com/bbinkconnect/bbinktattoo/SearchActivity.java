package com.bbinkconnect.bbinktattoo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.bbinkconnect.bbinktattoo.fragments.UsersFragment;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_search, new UsersFragment()).commit();
    }
}
