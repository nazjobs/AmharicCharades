package com.example.amhariccharades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // 1. Initialize Views
        TextView tvScore = findViewById(R.id.tvFinalScore);
        TextView tvCorrectList = findViewById(R.id.tvCorrectList);
        TextView tvPassList = findViewById(R.id.tvPassList);
        Button btnPlayAgain = findViewById(R.id.btnPlayAgain);
        Button btnHome = findViewById(R.id.btnHome);

        // 2. Get Data from GameActivity
        int score = getIntent().getIntExtra("TOTAL_SCORE", 0);
        ArrayList<String> correctWords = getIntent().getStringArrayListExtra("CORRECT_LIST");
        ArrayList<String> passedWords = getIntent().getStringArrayListExtra("PASS_LIST");

        // 3. Set Score
        tvScore.setText(String.valueOf(score));

        // 4. Populate Lists (Helper function below)
        tvCorrectList.setText(formatList(correctWords));
        tvPassList.setText(formatList(passedWords));

        // 5. Button Logic
        btnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to Category Selection
                Intent intent = new Intent(ScoreActivity.this, CategoryActivity.class);
                // Clear the back stack so they can't "back" into the old game result
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to Main Menu
                Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    // Helper to turn an ArrayList into a nice String with bullet points
    private String formatList(ArrayList<String> list) {
        if (list == null || list.isEmpty()) return "- None";

        StringBuilder sb = new StringBuilder();
        for (String word : list) {
            sb.append("• ").append(word).append("\n");
        }
        return sb.toString();
    }
}