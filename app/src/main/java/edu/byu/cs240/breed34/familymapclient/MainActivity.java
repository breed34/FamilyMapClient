package edu.byu.cs240.breed34.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import edu.byu.cs240.breed34.familymapclient.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentFrameLayout);
        if (fragment == null) {
            fragment = createLoginFragment();
        }

        fragmentManager.beginTransaction()
                .add(R.id.fragmentFrameLayout, fragment)
                .commit();
    }

    private Fragment createLoginFragment() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }
}