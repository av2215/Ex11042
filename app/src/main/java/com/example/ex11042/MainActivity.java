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
 * @author itay vaknin av2215@bs.amalnet.k12.il
 * @version 1.0
 * @since 22/03/2026
 * Main activity that displays the list of all expenditures and the monthly total.
 * Handles CRUD operations via context menu/dialog and navigation through the main menu.
 */
public class MainActivity extends AppCompatActivity {

    private ListView lvExpenditures;
    private TextView tvTotalMonthly;
    private DatabaseHelper dbHelper;
    private List<Expenditure> expenditureList;
    private ExpenditureAdapter adapter;

    /**
     * Initializes the activity, UI components, and data.
     * <p>
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
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

    /**
     * Called when the activity will start interacting with the user.
     * <p>
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    /**
     * Refreshes the list and the monthly total from the database.
     * <p>
     *
     */
    private void refreshData() {
        expenditureList = dbHelper.getAllExpenditures();
        adapter = new ExpenditureAdapter(this, expenditureList);
        lvExpenditures.setAdapter(adapter);

        double total = dbHelper.getTotalMonthlyExpenses();
        tvTotalMonthly.setText(String.format(Locale.getDefault(), "Total This Month: %.2f", total));
    }

    /**
     * Displays an options dialog (Update/Delete) for a selected expenditure.
     * <p>
     *
     * @param expenditure The expenditure object to be managed.
     */
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

    /**
     * Initializes the contents of the Activity's standard options menu.
     * <p>
     *
     * @param menu The options menu in which you place your items.
     * @return boolean You must return true for the menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * <p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed.
     */
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
     * @author itay vaknin av2215@bs.amalnet.k12.il
     * @version 1.0
     * @since 22/03/2026
     * Custom adapter for displaying expenditure items in the list.
     */
    private class ExpenditureAdapter extends ArrayAdapter<Expenditure> {
        /**
         * Constructor for the ExpenditureAdapter.
         * <p>
         *
         * @param context The current context.
         * @param list The list of expenditures to display.
         */
        public ExpenditureAdapter(android.content.Context context, List<Expenditure> list) {
            super(context, R.layout.list_item_expenditure, list);
        }

        /**
         * Provides a view for an AdapterView (ListView).
         * <p>
         *
         * @param position The position of the item within the adapter's data set.
         * @param convertView The old view to reuse, if possible.
         * @param parent The parent that this view will eventually be attached to.
         * @return View A View corresponding to the data at the specified position.
         */
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
