package com.example.amhariccharades;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tvWord, tvTimer;
    private ConstraintLayout gameContainer;
    private ImageButton btnPause;

    private ArrayList<String> wordList;
    private ArrayList<String> correctWords = new ArrayList<>();
    private ArrayList<String> passedWords = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isGameActive = false;
    private boolean isPaused = false;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean canGuess = true;

    private CountDownTimer timer;
    private long totalTimeInMillis;
    private long timeLeftInMillis;

    private boolean isSoundEnabled;
    private MediaPlayer mpCorrect, mpPass;

    // Handler to fix the "Stuck on Green" bug
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable resetRunnable;

    // SafeNet Variables
    private boolean isReadyForTilt = true; // Prevents spamming
    private TextView tvWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_game);

        tvWarning = findViewById(R.id.tvWarning);

        SharedPreferences prefs = getSharedPreferences("CharadesPrefs", MODE_PRIVATE);
        int seconds = prefs.getInt("RoundTime", 60);
        isSoundEnabled = prefs.getBoolean("SoundEnabled", true);
        totalTimeInMillis = seconds * 1000L;
        timeLeftInMillis = totalTimeInMillis;

        tvWord = findViewById(R.id.tvWord);
        tvTimer = findViewById(R.id.tvTimer);
        gameContainer = findViewById(R.id.gameContainer);
        btnPause = findViewById(R.id.btnPause);

        try {
            mpCorrect = MediaPlayer.create(this, R.raw.sound_correct);
            mpPass = MediaPlayer.create(this, R.raw.sound_pass);
        } catch (Exception e) { e.printStackTrace(); }

        setupGameData();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        findViewById(R.id.touchCorrect).setOnClickListener(v -> processGuess(true));
        findViewById(R.id.touchPass).setOnClickListener(v -> processGuess(false));

        btnPause.setOnClickListener(v -> pauseGame());

        startGame();
    }

    private void setupGameData() {
        String category = getIntent().getStringExtra("CATEGORY_NAME");
        wordList = DataManager.getWordsForCategory(category);
        Collections.shuffle(wordList);
        correctWords.clear();
        passedWords.clear();
        currentIndex = 0;
    }

    private void startGame() {
        isGameActive = true;
        isPaused = false;
        canGuess = true;

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        showNextWord();
        startTimer(timeLeftInMillis);
    }

    private void startTimer(long duration) {
        if (timer != null) timer.cancel();
        timer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                tvTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }
            @Override
            public void onFinish() { endGame(); }
        }.start();
    }

    @Override
    public void onBackPressed() {
        pauseGame();
    }

    private void pauseGame() {
        if (!isGameActive || isPaused) return;

        isPaused = true;
        isGameActive = false;

        if (timer != null) timer.cancel();
        sensorManager.unregisterListener(this);

        // CRITICAL FIX: Cancel any pending "Reset to Blue" commands
        if (resetRunnable != null) handler.removeCallbacks(resetRunnable);

        showPauseDialog();
    }

    private void showPauseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PAUSED");
        builder.setMessage("Time Remaining: " + (timeLeftInMillis / 1000) + "s");
        builder.setCancelable(false);
        builder.setPositiveButton("RESUME", (dialog, which) -> resumeGame());
        builder.setNeutralButton("RESTART", (dialog, which) -> restartGame());
        builder.setNegativeButton("QUIT", (dialog, which) -> finish());
        builder.show();
    }

    private void resumeGame() {
        isPaused = false;
        isGameActive = true;
        resetCardState(); // Force UI back to neutral immediately
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        startTimer(timeLeftInMillis);
    }

    private void restartGame() {
        resetCardState();
        setupGameData();
        timeLeftInMillis = totalTimeInMillis;
        startGame();
    }

    private void resetCardState() {
        gameContainer.setBackgroundResource(R.color.game_neutral);
        canGuess = true;
        if (currentIndex < wordList.size()) {
            tvWord.setText(wordList.get(currentIndex));
        }
    }

    private void showNextWord() {
        if (currentIndex < wordList.size()) {
            tvWord.setText(wordList.get(currentIndex));
        } else {
            endGame();
        }
    }

    private void processGuess(boolean isCorrect) {
        if (!isGameActive || isPaused || !canGuess) return;

        if (isCorrect) {
            if (isSoundEnabled && mpCorrect != null) mpCorrect.start();
            correctWords.add(wordList.get(currentIndex));
            flashScreen(R.color.game_correct, "CORRECT!");
        } else {
            if (isSoundEnabled && mpPass != null) mpPass.start();
            passedWords.add(wordList.get(currentIndex));
            flashScreen(R.color.game_pass, "PASS");
        }

        currentIndex++;
        canGuess = false;

        resetRunnable = () -> {
            if (!isPaused) {
                resetCardState();
                showNextWord();
            }
        };
        handler.postDelayed(resetRunnable, 1000);
    }

    private void flashScreen(int colorResId, String text) {
        gameContainer.setBackgroundResource(colorResId);
        tvWord.setText(text);
    }

    private void endGame() {
        isGameActive = false;
        if (timer != null) timer.cancel();
        Intent intent = new Intent(GameActivity.this, ScoreActivity.class);
        intent.putStringArrayListExtra("CORRECT_LIST", correctWords);
        intent.putStringArrayListExtra("PASS_LIST", passedWords);
        intent.putExtra("TOTAL_SCORE", correctWords.size());
        // Pass the category name forward to the Score Screen
        intent.putExtra("CATEGORY_NAME", getIntent().getStringExtra("CATEGORY_NAME"));
        startActivity(intent);
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isGameActive || isPaused) return;

        float x = event.values[0]; // Vertical Gravity in Landscape
        float y = event.values[1]; // Side-to-side tilt
        float z = event.values[2]; // Forward/Back tilt (The guess)

        // 1. SAFENET: Check if phone is UPRIGHT (Landscape)
        // If X is close to 0, the phone is likely lying flat on a table.
        // We want X to be high (gravity pulling down on the short edge).
        if (Math.abs(x) < 5.0f) {
            if (tvWarning.getVisibility() != View.VISIBLE) {
                tvWarning.setVisibility(View.VISIBLE);
                canGuess = false;
            }
            return; // STOP HERE. Phone is flat or in portrait mode.
        } else {
            // Hide warning if they fixed it
            if (tvWarning.getVisibility() == View.VISIBLE) {
                tvWarning.setVisibility(View.GONE);
                canGuess = true;
            }
        }

        if (!canGuess) return;

        // 2. RESET MECHANISM (Anti-Spam)
        // User must return phone to neutral position (Z between -2 and 2)
        if (z > -2.0f && z < 2.0f) {
            isReadyForTilt = true;
        }

        // 3. GUESS LOGIC
        if (isReadyForTilt) {
            if (z < -7.0f) {
                // TILT DOWN (Screen to floor) -> CORRECT (Green)
                processGuess(true);
                isReadyForTilt = false;
            }
            else if (z > 7.0f) {
                // TILT UP (Screen to sky) -> PASS (Red)
                processGuess(false);
                isReadyForTilt = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    protected void onPause() {
        super.onPause();
        if (isGameActive && !isPaused) pauseGame();
    }
}