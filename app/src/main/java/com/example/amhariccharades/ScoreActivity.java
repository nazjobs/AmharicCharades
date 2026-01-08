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
        Button btnNewCategory = findViewById(R.id.btnNewCategory); // New
        Button btnHome = findViewById(R.id.btnHome);

        // 2. Get Data
        int score = getIntent().getIntExtra("TOTAL_SCORE", 0);
        ArrayList<String> correctWords = getIntent().getStringArrayListExtra("CORRECT_LIST");
        ArrayList<String> passedWords = getIntent().getStringArrayListExtra("PASS_LIST");

        // Get the category so we can replay it
        String currentCategory = getIntent().getStringExtra("CATEGORY_NAME");

        // 3. Display Data
        tvScore.setText(String.valueOf(score));
        tvCorrectList.setText(formatList(correctWords));
        tvPassList.setText(formatList(passedWords));

        // 4. BUTTON LOGIC

        // Play Again -> Go to Instructions (Restart same category)
        btnPlayAgain.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreActivity.this, InstructionActivity.class);
            intent.putExtra("CATEGORY_NAME", currentCategory);
            startActivity(intent);
            finish();
        });

        // Change Category -> Go to Grid
        btnNewCategory.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreActivity.this, CategoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clears back stack
            startActivity(intent);
            finish();
        });

        // Home -> Main Menu
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private String formatList(ArrayList<String> list) {
        if (list == null || list.isEmpty()) return "- None";
        StringBuilder sb = new StringBuilder();
        for (String word : list) {
            sb.append("• ").append(word).append("\n");
        }
        return sb.toString();
    }
}