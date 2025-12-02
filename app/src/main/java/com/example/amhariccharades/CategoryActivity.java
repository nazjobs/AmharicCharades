package com.example.amhariccharades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        setupCategory(R.id.btnCatAnimals, DataManager.CAT_ANIMALS);
        setupCategory(R.id.btnCatMovies, DataManager.CAT_MOVIES);
        setupCategory(R.id.btnCatLit, DataManager.CAT_LIT);
        setupCategory(R.id.btnCatPlaces, DataManager.CAT_PLACES);
        setupCategory(R.id.btnCatCelebs, DataManager.CAT_CELEBS);

        // NEW CATEGORIES
        setupCategory(R.id.btnCatProverbs, DataManager.CAT_PROVERBS);
        setupCategory(R.id.btnCatRandom, DataManager.CAT_RANDOM);
    }

    private void setupCategory(int buttonId, final String categoryName) {
        TextView btn = findViewById(buttonId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, we go to InstructionActivity
                // We pass the "Category" name so the Game knows what words to load later
                Intent intent = new Intent(CategoryActivity.this, InstructionActivity.class);
                intent.putExtra("CATEGORY_NAME", categoryName);
                startActivity(intent);
            }
        });
    }
}