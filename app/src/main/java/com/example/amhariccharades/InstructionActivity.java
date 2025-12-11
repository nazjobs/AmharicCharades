package com.example.amhariccharades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class InstructionActivity extends AppCompatActivity {

    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        // 1. Get the category passed from CategoryActivity
        selectedCategory = getIntent().getStringExtra("CATEGORY_NAME");

        // 2. Setup Start Button
        Button btnStart = findViewById(R.id.btnStartGame);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
    }

    private void startGame() {
        // Pass the category to the final Game Activity
        Intent intent = new Intent(InstructionActivity.this, GameActivity.class);
        intent.putExtra("CATEGORY_NAME", selectedCategory);
        startActivity(intent);
        finish(); // Remove instruction screen from back stack so they don't return here
    }
}