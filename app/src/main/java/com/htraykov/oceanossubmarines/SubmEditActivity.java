package com.htraykov.oceanossubmarines;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

// TODO: Implement logic for keeping the current state of the game on clicking back button in SubmEditActivity
// TODO: Implement logic for setting player names to each selected submarine in SubmEditActivity
// TODO: Add logic for negative points with octopus tokens like in the game
// TODO: Add some VFX for up- downgrading submarine parts' buttons
public class SubmEditActivity extends AppCompatActivity {
    String currentActivityLangCode = MainActivity.EN_LANG_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        ArrayList<String> submNames;

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_edit_subm);

        // Update language of all elements if needed on activity creation
        if (!currentActivityLangCode.equals(MainActivity.currentLangCode)) {
            Resources resources = LocaleHelper.setLocale(this, MainActivity.currentLangCode);

            ((TextView)findViewById(R.id.submEditTextView))
                .setText(resources.getString(R.string.subm_edit_textview));

            currentActivityLangCode = MainActivity.currentLangCode; // Update the current activity language
        }

        Intent intent = getIntent();
        submNames = intent.getStringArrayListExtra(getResources().getString(R.string.selected_subm_list));
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setReorderingAllowed(true);

        int submNamesSize = submNames.size();

        ((GridLayout)findViewById(R.id.submEditGridLayout)).setRowCount(submNamesSize);

        if (savedInstanceState == null) { // Prevents adding fragments if the screen is rotated
            for (int i = 0; i < submNamesSize; i++) {
                SubmarineFragment sf = SubmarineFragment.newInstance(submNames.get(i), this);
                ft.add(R.id.submEditGridLayout, sf);
            }
        }

        ft.commit();
    }

    @Override
    public void onBackPressed() { // Change to the previous screen with slide down effect
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
    }
}
