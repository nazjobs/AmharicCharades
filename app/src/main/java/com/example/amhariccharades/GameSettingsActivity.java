package com.example.amhariccharades;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class GameSettingsActivity extends AppCompatActivity {

    private RadioGroup radioGroupTime;
    private RadioButton rb60, rb90, rb120;
    private SwitchCompat switchSound;

    // Constants for storage
    public static final String PREFS_NAME = "CharadesPrefs";
    public static final String KEY_TIME = "RoundTime";
    public static final String KEY_SOUND = "SoundEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        // 1. Init Views
        radioGroupTime = findViewById(R.id.radioGroupTime);
        rb60 = findViewById(R.id.rb60);
        rb90 = findViewById(R.id.rb90);
        rb120 = findViewById(R.id.rb120);
        switchSound = findViewById(R.id.switchSound);
        Button btnSave = findViewById(R.id.btnSaveSettings);

        // 2. Load Saved Preferences (or default)
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedTime = prefs.getInt(KEY_TIME, 60); // Default 60s
        boolean soundOn = prefs.getBoolean(KEY_SOUND, true); // Default On

        // 3. Update UI to match saved data
        if (savedTime == 90) rb90.setChecked(true);
        else if (savedTime == 120) rb120.setChecked(true);
        else rb60.setChecked(true);

        switchSound.setChecked(soundOn);

        // 4. Save Logic
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    private void saveSettings() {
        int selectedTime = 60;
        int selectedId = radioGroupTime.getCheckedRadioButtonId();

        if (selectedId == R.id.rb90) selectedTime = 90;
        else if (selectedId == R.id.rb120) selectedTime = 120;

        boolean isSoundEnabled = switchSound.isChecked();

        // Write to storage
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(KEY_TIME, selectedTime);
        editor.putBoolean(KEY_SOUND, isSoundEnabled);
        editor.apply(); // Apply saves in background

        finish(); // Close settings and go back
    }
}