package com.example.ex11042;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

/**
 * Screen for adding or editing an expenditure record.
 * Includes validation and date selection.
 */
public class ExpenditureActivity extends AppCompatActivity {

    private EditText etDescription, etAmount, etDate;
    private Spinner spCategory;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private Expenditure existingExpenditure;

    // Predefined categories as requested
    private final String[] categories = {"Food", "Entertainment", "Transport", "Bills", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure);

        dbHelper = new DatabaseHelper(this);

        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        spCategory = findViewById(R.id.spCategory);
        btnSave = findViewById(R.id.btnSave);

        // Setup Spinner
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(catAdapter);

        // Date Picker setup
        etDate.setOnClickListener(v -> showDatePicker());

        // Check if updating an existing record
        existingExpenditure = (Expenditure) getIntent().getSerializableExtra("expenditure");
        if (existingExpenditure != null) {
            etDescription.setText(existingExpenditure.getDescription());
            etAmount.setText(String.valueOf(existingExpenditure.getAmount()));
            etDate.setText(existingExpenditure.getDate());
            // Set spinner to correct category
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(existingExpenditure.getCategory())) {
                    spCategory.setSelection(i);
                    break;
                }
            }
            btnSave.setText(R.string.update);
        }

        btnSave.setOnClickListener(v -> saveExpenditure());
    }

    /**
     * Shows a DatePickerDialog and updates the date EditText.
     */
    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    // Format as YYYY-MM-DD for easier SQLite sorting
                    String date = String.format("%d-%02d-%02d", y, (m + 1), d);
                    etDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Validates input and saves/updates the expenditure in the database.
     */
    private void saveExpenditure() {
        String desc = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        if (desc.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (existingExpenditure == null) {
            // Add New
            Expenditure newExp = new Expenditure(desc, amount, category, date);
            dbHelper.addExpenditure(newExp);
            Toast.makeText(this, "Expenditure added successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Update
            existingExpenditure = new Expenditure(existingExpenditure.getId(), desc, amount, category, date);
            dbHelper.updateExpenditure(existingExpenditure);
            Toast.makeText(this, "Expenditure updated successfully", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            return true;
        } else if (id == R.id.menu_search) {
            startActivity(new Intent(this, SearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            return true;
        } else if (id == R.id.menu_credits) {
            startActivity(new Intent(this, CreditActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
