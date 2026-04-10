package com.example.ex11042;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

/**
 * Main activity that displays the list of all expenditures and the monthly total.
 * Handles CRUD operations via context menu/dialog and navigation through the main menu.
 */
public class MainActivity extends AppCompatActivity {

    private ListView lvExpenditures;
    private TextView tvTotalMonthly;
    private DatabaseHelper dbHelper;
    private List<Expenditure> expenditureList;
    private ExpenditureAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        lvExpenditures = findViewById(R.id.lvExpenditures);
        tvTotalMonthly = findViewById(R.id.tvTotalMonthly);

        refreshData();

        lvExpenditures.setOnItemLongClickListener((parent, view, position, id) -> {
            showOptionsDialog(expenditureList.get(position));
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    /**
     * Refreshes the list and the monthly total from the database.
     */
    private void refreshData() {
        expenditureList = dbHelper.getAllExpenditures();
        adapter = new ExpenditureAdapter(this, expenditureList);
        lvExpenditures.setAdapter(adapter);

        double total = dbHelper.getTotalMonthlyExpenses();
        tvTotalMonthly.setText(String.format(Locale.getDefault(), "Total This Month: %.2f", total));
    }

    private void showOptionsDialog(final Expenditure expenditure) {
        String[] options = {"Update", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_option);
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // Update
                Intent intent = new Intent(MainActivity.this, ExpenditureActivity.class);
                intent.putExtra("expenditure", expenditure);
                startActivity(intent);
            } else { // Delete
                dbHelper.deleteExpenditure(expenditure.getId());
                refreshData();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            startActivity(new Intent(this, ExpenditureActivity.class));
            return true;
        } else if (id == R.id.menu_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        } else if (id == R.id.menu_credits) {
            startActivity(new Intent(this, CreditActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Custom adapter for displaying expenditure items in the list.
     */
    private class ExpenditureAdapter extends ArrayAdapter<Expenditure> {
        public ExpenditureAdapter(android.content.Context context, List<Expenditure> list) {
            super(context, R.layout.list_item_expenditure, list);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expenditure, parent, false);
            }
            Expenditure e = getItem(position);
            if (e != null) {
                ((TextView) convertView.findViewById(R.id.tvDescription)).setText(e.getDescription());
                ((TextView) convertView.findViewById(R.id.tvAmount)).setText(String.format(Locale.getDefault(), "%.2f", e.getAmount()));
                ((TextView) convertView.findViewById(R.id.tvCategory)).setText(e.getCategory());
                ((TextView) convertView.findViewById(R.id.tvDate)).setText(e.getDate());
            }
            return convertView;
        }
    }
}
