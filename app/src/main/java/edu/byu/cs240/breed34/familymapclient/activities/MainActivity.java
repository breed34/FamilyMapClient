package edu.byu.cs240.breed34.familymapclient.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import edu.byu.cs240.breed34.familymapclient.R;
import edu.byu.cs240.breed34.familymapclient.fragments.LoginFragment;
import edu.byu.cs240.breed34.familymapclient.fragments.MapFragment;
import edu.byu.cs240.breed34.familymapclient.listeners.LoginListener;

/**
 * The main activity of the application.
 */
public class MainActivity extends AppCompatActivity implements LoginListener {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set login fragment.
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentFrameLayout);
        if (fragment == null) {
            fragment = new LoginFragment();
            ((LoginFragment)fragment).registerListener(this);
        } else if (fragment instanceof LoginFragment) {
            ((LoginFragment)fragment).registerListener(this);
        }

        // Commit fragment.
        fragmentManager.beginTransaction()
                .add(R.id.fragmentFrameLayout, fragment)
                .commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLogin() {
        // Switch to map fragment.
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentFrameLayout, fragment)
                .commit();
    }
}