package com.example.bookfanapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bookfanapp.fragments.AllBooksListFragment;
import com.example.bookfanapp.fragments.MyListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        bottomNavigationView = findViewById(R.id.navBar);
        firebaseAuth=FirebaseAuth.getInstance();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.myBooks_nav_button:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view, MyListFragment.class, null)
                                .commit();
                        return true;

                    case R.id.allBooks_nav_button:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view, AllBooksListFragment.class, null)
                                .commit();
                        return true;
                }
                return false;
            }

        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, MyListFragment.class, null)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_bt:
                startActivity(new Intent(this, BookPostActivity.class));
                break;
            case R.id.sign_out:
                firebaseAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id. my_profile:
                startActivity(new Intent(this, MyProfile.class));

        }
        return super.onOptionsItemSelected(item);
    }
}
