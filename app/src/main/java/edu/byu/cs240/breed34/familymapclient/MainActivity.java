package edu.byu.cs240.breed34.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import edu.byu.cs240.breed34.familymapclient.fragments.LoginFragment;

/**
 * The main activity of the application.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set fragment.
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentFrameLayout);
        if (fragment == null) {
            fragment = createLoginFragment();
        }

        // Commit fragment.
        fragmentManager.beginTransaction()
                .add(R.id.fragmentFrameLayout, fragment)
                .commit();
    }

    /**
     * Creates a new login fragment.
     *
     * @return the new login fragment.
     */
    private Fragment createLoginFragment() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }
}