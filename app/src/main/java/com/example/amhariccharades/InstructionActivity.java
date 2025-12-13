package com.example.amhariccharades;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class InstructionActivity extends AppCompatActivity {

    private String selectedCategory;
    private CountDownTimer startTimer;
    private TextView tvCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        // 1. Get Category
        selectedCategory = getIntent().getStringExtra("CATEGORY_NAME");
        tvCountdown = findViewById(R.id.tvInstCountdown);

        // 2. Start 5-Second Countdown immediately
        startCountdown();
    }

    private void startCountdown() {
        // 5000ms = 5 seconds, tick every 1000ms
        startTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Determine seconds left (adding 1 to round up logically)
                int secondsLeft = (int) (millisUntilFinished / 1000) + 1;
                tvCountdown.setText(String.valueOf(secondsLeft));
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("GO!");
                startGame();
            }
        }.start();
    }

    private void startGame() {
        Intent intent = new Intent(InstructionActivity.this, GameActivity.class);
        intent.putExtra("CATEGORY_NAME", selectedCategory);
        startActivity(intent);
        finish(); // Remove instructions from back stack
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Safety: Cancel timer if user presses back or app closes
        if (startTimer != null) {
            startTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        // Explicitly cancel timer and go back
        if (startTimer != null) startTimer.cancel();
        super.onBackPressed();
    }
}