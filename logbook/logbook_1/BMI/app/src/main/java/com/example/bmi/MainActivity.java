package com.example.bmi;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout tilWeight, tilHeight;
    private TextInputEditText etWeight, etHeight;
    private RadioButton rbCm;
    private Button btnCalculate;
    private CardView cardResult;
    private TextView tvBmiValue, tvBmiCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilWeight = findViewById(R.id.tilWeight);
        tilHeight = findViewById(R.id.tilHeight);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        rbCm = findViewById(R.id.rbCm);
        btnCalculate = findViewById(R.id.btnCalculate);
        cardResult = findViewById(R.id.cardResult);
        tvBmiValue = findViewById(R.id.tvBmiValue);
        tvBmiCategory = findViewById(R.id.tvBmiCategory);
    }

    private void setupListeners() {
        btnCalculate.setOnClickListener(v -> calculateBmi());
    }

    private void calculateBmi() {
        // Reset errors
        tilWeight.setError(null);
        tilHeight.setError(null);

        String weightStr = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";
        String heightStr = etHeight.getText() != null ? etHeight.getText().toString().trim() : "";

        // Validate weight
        if (TextUtils.isEmpty(weightStr)) {
            tilWeight.setError(getString(R.string.error_empty_weight));
            etWeight.requestFocus();
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightStr);
        } catch (NumberFormatException e) {
            tilWeight.setError(getString(R.string.error_invalid_weight));
            etWeight.requestFocus();
            return;
        }

        if (weight <= 0 || weight > 500) {
            tilWeight.setError(getString(R.string.error_invalid_weight));
            etWeight.requestFocus();
            return;
        }

        // Validate height
        if (TextUtils.isEmpty(heightStr)) {
            tilHeight.setError(getString(R.string.error_empty_height));
            etHeight.requestFocus();
            return;
        }

        double height;
        try {
            height = Double.parseDouble(heightStr);
        } catch (NumberFormatException e) {
            tilHeight.setError(getString(R.string.error_invalid_height));
            etHeight.requestFocus();
            return;
        }

        if (height <= 0) {
            tilHeight.setError(getString(R.string.error_invalid_height));
            etHeight.requestFocus();
            return;
        }

        // Convert height to meters
        double heightInMeters;
        if (rbCm.isChecked()) {
            if (height > 300) {
                tilHeight.setError(getString(R.string.error_invalid_height));
                etHeight.requestFocus();
                return;
            }
            heightInMeters = height / 100.0;
        } else {
            if (height > 3.0) {
                tilHeight.setError(getString(R.string.error_invalid_height));
                etHeight.requestFocus();
                return;
            }
            heightInMeters = height;
        }

        // Calculate BMI = weight / (height^2)
        double bmi = weight / (heightInMeters * heightInMeters);

        // Display result and color coding
        displayResult(bmi);
    }

    private void displayResult(double bmi) {
        cardResult.setVisibility(View.VISIBLE);
        tvBmiValue.setText(String.format(Locale.getDefault(), "%.1f", bmi));

        String category;
        int colorResId;

        if (bmi < 18.5) {
            category = getString(R.string.bmi_underweight);
            colorResId = R.color.bmi_underweight;
        } else if (bmi < 25.0) {
            category = getString(R.string.bmi_normal);
            colorResId = R.color.bmi_normal;
        } else if (bmi < 30.0) {
            category = getString(R.string.bmi_overweight);
            colorResId = R.color.bmi_overweight;
        } else {
            category = getString(R.string.bmi_obese);
            colorResId = R.color.bmi_obese;
        }

        tvBmiCategory.setText(category);
        int color = ContextCompat.getColor(this, colorResId);
        tvBmiCategory.setTextColor(color);
        tvBmiValue.setTextColor(color);
    }
}
