package com.example.amhariccharades;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    // UI Elements
    private TextView tvWord, tvTimer;
    private ConstraintLayout gameContainer;

    // Game Data
    private ArrayList<String> wordList;
    private ArrayList<String> correctWords = new ArrayList<>();
    private ArrayList<String> passedWords = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isGameActive = false;
    private boolean isPaused = false;

    // Sensors
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean canGuess = true;

    // Timer Logic
    private CountDownTimer timer;
    private static final long TOTAL_TIME = 60000; // 60 Seconds
    private long timeLeftInMillis = TOTAL_TIME;   // Track time left for pausing

    // Sounds
    private MediaPlayer mpCorrect;
    private MediaPlayer mpPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_game);

        // 1. Initialize UI
        tvWord = findViewById(R.id.tvWord);
        tvTimer = findViewById(R.id.tvTimer);
        gameContainer = findViewById(R.id.gameContainer);

        // 2. Load Sounds
        try {
            mpCorrect = MediaPlayer.create(this, R.raw.sound_correct);
            mpPass = MediaPlayer.create(this, R.raw.sound_pass);
        } catch (Exception e) { e.printStackTrace(); }

        // 3. Get Data & Setup
        setupGameData();

        // 4. Setup Sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // 5. Setup Touch Fallback
        findViewById(R.id.touchCorrect).setOnClickListener(v -> processGuess(true));
        findViewById(R.id.touchPass).setOnClickListener(v -> processGuess(false));

        // 6. Start Game
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

        // Register sensor
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        showNextWord();
        startTimer(timeLeftInMillis);
    }

    private void startTimer(long duration) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished; // Update variable constantly
                tvTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                endGame();
            }
        }.start();
    }

    // --- PAUSE LOGIC ---

    @Override
    public void onBackPressed() {
        // If game is running, pause it instead of exiting
        if (isGameActive && !isPaused) {
            pauseGame();
        } else {
            super.onBackPressed();
        }
    }

    private void pauseGame() {
        isPaused = true;
        isGameActive = false; // Stop processing guesses

        // 1. Stop Timer
        if (timer != null) {
            timer.cancel();
        }

        // 2. Stop Sensor (So tilting while looking at the menu doesn't count)
        sensorManager.unregisterListener(this);

        // 3. Show Dialog
        showPauseDialog();
    }

    private void showPauseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PAUSED");
        builder.setMessage("Time Remaining: " + (timeLeftInMillis / 1000) + "s");
        builder.setCancelable(false); // User must click a button

        // Button: RESUME
        builder.setPositiveButton("RESUME", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resumeGame();
            }
        });

        // Button: RESTART ROUND
        builder.setNeutralButton("RESTART", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restartGame();
            }
        });

        // Button: QUIT
        builder.setNegativeButton("QUIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); // Close activity, go back to instructions
            }
        });

        builder.show();
    }

    private void resumeGame() {
        isPaused = false;
        isGameActive = true;

        // Re-register sensor
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        // Resume timer from where we left off
        startTimer(timeLeftInMillis);
    }

    private void restartGame() {
        // Reset everything
        setupGameData();
        timeLeftInMillis = TOTAL_TIME;
        startGame();
    }

    // --- GAMEPLAY LOGIC ---

    private void showNextWord() {
        if (currentIndex < wordList.size()) {
            tvWord.setText(wordList.get(currentIndex));
        } else {
            endGame();
        }
    }

    private void processGuess(boolean isCorrect) {
        if (!isGameActive || isPaused) return;

        if (isCorrect) {
            if (mpCorrect != null) mpCorrect.start();
            correctWords.add(wordList.get(currentIndex));
            flashScreen(R.color.game_correct);
        } else {
            if (mpPass != null) mpPass.start();
            passedWords.add(wordList.get(currentIndex));
            flashScreen(R.color.game_pass);
        }

        currentIndex++;

        new Handler().postDelayed(() -> {
            // Only update UI if we are still playing (didn't pause during the flash)
            if (!isPaused) {
                gameContainer.setBackgroundResource(R.color.game_neutral);
                showNextWord();
                canGuess = true;
            }
        }, 1000);
    }

    private void flashScreen(int colorResId) {
        gameContainer.setBackgroundResource(colorResId);
        tvWord.setText(colorResId == R.color.game_correct ? "CORRECT!" : "PASS");
        canGuess = false;
    }

    private void endGame() {
        isGameActive = false;
        if (timer != null) timer.cancel();

        Intent intent = new Intent(GameActivity.this, ScoreActivity.class);
        intent.putStringArrayListExtra("CORRECT_LIST", correctWords);
        intent.putStringArrayListExtra("PASS_LIST", passedWords);
        intent.putExtra("TOTAL_SCORE", correctWords.size());
        startActivity(intent);
        finish();
    }

    // --- SENSOR ---
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isGameActive || isPaused || !canGuess) return;

        float z = event.values[2];
        if (z > 7.0f) processGuess(true);
        else if (z < -7.0f) processGuess(false);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause if app goes to background (e.g. user presses Home button)
        if (isGameActive && !isPaused) {
            pauseGame();
        }
    }
}