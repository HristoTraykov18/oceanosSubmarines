package com.htraykov.oceanossubmarines;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;

// TODO: Implement logic for returning to game after clicking back button in SubmEditActivity
// TODO: Submarines' parts images need rework. Initial submarine views need to be changed for each submarine
public class SubmSelectActivity extends AppCompatActivity {
    String currentActivityLangCode = MainActivity.EN_LANG_CODE;
    //int prevSubmCount = 0;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_select_subm);

        Button btnNewGame = findViewById(R.id.newGame);
        ArrayList<String> selectedSubmList = new ArrayList<>();

        // Update language of all elements if needed on activity creation
        if (!currentActivityLangCode.equals(MainActivity.currentLangCode)) {
            resources = LocaleHelper.setLocale(this, MainActivity.currentLangCode);

            ((TextView)findViewById(R.id.submSelectTextView))
                .setText(resources.getString(R.string.subm_select_textview));
            ((TextView)findViewById(R.id.grabberTextView)).setText(resources.getString(R.string.grabber));
            ((TextView)findViewById(R.id.jawsTextView)).setText(resources.getString(R.string.jaws));
            ((TextView)findViewById(R.id.myladyTextView)).setText(resources.getString(R.string.mylady));
            ((TextView)findViewById(R.id.nansheTextView)).setText(resources.getString(R.string.nanshe));
            ((TextView)findViewById(R.id.naughtyTextView)).setText(resources.getString(R.string.naughty));
            btnNewGame.setText(resources.getString(R.string.btn_new_game));

            currentActivityLangCode = MainActivity.currentLangCode; // Update the current activity language
        }

        CardView[] submCardViews = { // Submarines' CardViews array
            findViewById(R.id.grabber),
            findViewById(R.id.jaws),
            findViewById(R.id.mylady),
            findViewById(R.id.nanshe),
            findViewById(R.id.naughty)
        };
        final String[] submNames = new String[] { // Fill submarines' names array from resources
            this.getString(R.string.grabber).toLowerCase(),
            this.getString(R.string.jaws).toLowerCase(),
            this.getString(R.string.mylady).toLowerCase(),
            this.getString(R.string.nanshe).toLowerCase(),
            this.getString(R.string.naughty).toLowerCase()
        };
        ColorDrawable[] transitionColors = new ColorDrawable[] {
            new ColorDrawable(getResources().getColor(R.color.light_grey)),
            new ColorDrawable(getResources().getColor(R.color.dark_grey))
        };

        // Set on click listeners for each submarine CardView
        for (int i = 0; i < submNames.length; i++) {
            if (!submCardViews[i].hasOnClickListeners()) {
                // Suppress "Variable used in lambda expression
                // should be final or effectively final" error
                final int k = i;
                TransitionDrawable transitionCardViews = new TransitionDrawable(transitionColors);
                submCardViews[i].setBackground(transitionCardViews);

                submCardViews[i].setOnClickListener(view -> {
                    if (selectedSubmList.contains(submNames[k])) // Update selected submarines list
                        selectedSubmList.remove(submNames[k]);
                    else
                        selectedSubmList.add(submNames[k]);

                    if (selectedSubmList.size() > 0) { // Change the continue button's color
                        btnNewGame.setBackgroundColor(getResources().getColor(R.color.blue));
                        btnNewGame.setTextColor(getResources().getColor(R.color.white));
                    }
                    else {
                        btnNewGame.setBackgroundColor(getResources().getColor(R.color.pale_blue));
                        btnNewGame.setTextColor(getResources().getColor(R.color.dimmed_white));
                    }

                    transitionCardViews.reverseTransition(MainActivity.TRANSITION_DURATION);
                });
            }
        }

        btnNewGame.setOnClickListener(view -> { // Change to the next screen with slide up effect
            int currentSubmCount = selectedSubmList.size();

            if (currentSubmCount > 0) {
                //if (prevSubmCount != currentSubmCount) {
                    //prevSubmCount = currentSubmCount;
                    Intent intent = new Intent(this, SubmEditActivity.class);

                    intent.putStringArrayListExtra( // Send submarines list to the next activity
                        getResources().getString(R.string.selected_subm_list),
                        selectedSubmList);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
                //}
                //else {
                //}
            }
            else {
                //prevSubmCount = 0;
                LayoutInflater inflater = getLayoutInflater();

                // Create new popup with the specified xml file as layout
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View popupView = inflater.inflate(R.layout.new_game_popup,
                    findViewById(R.id.newGamePopup));
                builder.setView(popupView).show();

                if (!currentActivityLangCode.equals(MainActivity.EN_LANG_CODE)) {
                    ((TextView)popupView.findViewById(R.id.newGamePopupText))
                        .setText(resources.getString(R.string.new_game_popup_text));
                }
            }
        });
    }

    @Override
    public void onBackPressed() { // Change to the previous screen with slide down effect
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
    }
}
