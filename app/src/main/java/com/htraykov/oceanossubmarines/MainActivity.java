package com.htraykov.oceanossubmarines;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

// !IMPORTANT TODO: Replace ScrollViews with RecyclerViews for better performance
public class MainActivity extends AppCompatActivity {
    final String BG_LANG_CODE = "bg";
    static final String EN_LANG_CODE = "en";
    static final int TRANSITION_DURATION = 450;
    static String currentLangCode = EN_LANG_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        ImageButton btnBG = findViewById(R.id.btnBGFlag);
        ImageButton btnEN = findViewById(R.id.btnENFlag);
        Button btnSelectSubm = findViewById(R.id.btnSelectSubm);

        // Create colors for transitions, transitions and set them to the flag buttons
        ColorDrawable[] transitionColors = new ColorDrawable[] {
            new ColorDrawable(getResources().getColor(R.color.transparent)),
            new ColorDrawable(getResources().getColor(R.color.transparent_90))
        };
        TransitionDrawable transitionBtnBG = new TransitionDrawable(transitionColors);
        TransitionDrawable transitionBtnEN = new TransitionDrawable(transitionColors);
        AtomicBoolean isLanguageBG = new AtomicBoolean(currentLangCode.equals(BG_LANG_CODE));

        btnBG.setBackground(transitionBtnBG);
        btnEN.setBackground(transitionBtnEN);

        if (isLanguageBG.get()) { // Select the proper button and update the language
            UpdateActivityLanguage(BG_LANG_CODE, btnSelectSubm);
            transitionBtnBG.reverseTransition(TRANSITION_DURATION);
        }
        else
            transitionBtnEN.reverseTransition(TRANSITION_DURATION);

        btnSelectSubm.setOnClickListener(view -> { // Change to the next screen with slide up effect
            Intent intent = new Intent(this, SubmSelectActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
        });

        btnBG.setOnClickListener(view -> { // Change language to Bulgarian on click
            if (!isLanguageBG.get()) {
                UpdateActivityLanguage(BG_LANG_CODE, btnSelectSubm);

                // Animate language buttons
                transitionBtnEN.reverseTransition(TRANSITION_DURATION);
                transitionBtnBG.reverseTransition(TRANSITION_DURATION);

                isLanguageBG.set(true);
            }
        });

        btnEN.setOnClickListener(view -> { // Change language to English on click
            if (!Locale.getDefault().getLanguage().equals(EN_LANG_CODE)) {
                UpdateActivityLanguage(EN_LANG_CODE, btnSelectSubm);

                // Animate language buttons
                transitionBtnBG.reverseTransition(TRANSITION_DURATION);
                transitionBtnEN.reverseTransition(TRANSITION_DURATION);

                isLanguageBG.set(false);
            }
        });
    }

    private void UpdateActivityLanguage(String languageCode, Button btn) {
        Resources resources = LocaleHelper.setLocale(this, languageCode);
        currentLangCode = languageCode;

        ((TextView)findViewById(R.id.langTextView)).setText(resources.getString(R.string.lang_textview));
        btn.setText(resources.getString(R.string.btn_select_subm));
    }
}
