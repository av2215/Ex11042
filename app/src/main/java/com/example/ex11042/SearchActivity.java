package com.example.ex11042;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author itay vaknin av2215@bs.amalnet.k12.il
 * @version 1.0
 * @since 22/03/2026
 * Screen for searching and filtering expenditure records.
 * Allows filtering by description, price range, and sorting.
 */
public class SearchActivity extends AppCompatActivity {

    private EditText etSearchDescription, etMinAmount, etMaxAmount;
    private Spinner spinnerSort;
    private Button btnDoSearch;
    private ListView lvSearchResults;
    private DatabaseHelper dbHelper;
    private List<Expenditure> searchResults;
    private SearchAdapter adapter;

    private final String[] sortOptions = {"Date", "Amount"};

    /**
     * Initializes the activity, UI components, and sets up adapters.
     * <p>
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new DatabaseHelper(this);

        etSearchDescription = findViewById(R.id.etSearchDescription);
        etMinAmount = findViewById(R.id.etMinAmount);
        etMaxAmount = findViewById(R.id.etMaxAmount);
        spinnerSort = findViewById(R.id.spinnerSort);
        btnDoSearch = findViewById(R.id.btnDoSearch);
        lvSearchResults = findViewById(R.id.lvSearchResults);


        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        searchResults = new ArrayList<>();
        adapter = new SearchAdapter(this, searchResults);
        lvSearchResults.setAdapter(adapter);

        btnDoSearch.setOnClickListener(v -> performSearch());
    }

    /**
     * Executes the search query based on user input and updates the result list.
     * <p>
     *
     */
    private void performSearch() {
        String desc = etSearchDescription.getText().toString().trim();
        String minStr = etMinAmount.getText().toString().trim();
        String maxStr = etMaxAmount.getText().toString().trim();
        String sortBy = spinnerSort.getSelectedItem().toString();


        String sortColumn = sortBy.equals("Date") ? DatabaseHelper.COLUMN_DATE : DatabaseHelper.COLUMN_AMOUNT;

        Double min = null;
        try {
            if (!minStr.isEmpty()) min = Double.parseDouble(minStr);
        } catch (NumberFormatException e) {
            // Treat invalid input as null or handle error
        }

        Double max = null;
        try {
            if (!maxStr.isEmpty()) max = Double.parseDouble(maxStr);
        } catch (NumberFormatException e) {
            // Treat invalid input as null or handle error
        }

        searchResults.clear();
        searchResults.addAll(dbHelper.searchExpenditures(desc, min, max, sortColumn));
        adapter.notifyDataSetChanged();
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
        if (id == R.id.menu_home) {
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            return true;
        } else if (id == R.id.menu_add) {
            startActivity(new Intent(this, ExpenditureActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            return true;
        } else if (id == R.id.menu_credits) {
            startActivity(new Intent(this, CreditActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @author itay vaknin av2215@bs.amalnet.k12.il
     * @version 1.0
     * @since 22/03/2026
     * Custom adapter for search results list.
     */
    private class SearchAdapter extends ArrayAdapter<Expenditure> {
        /**
         * Constructor for the SearchAdapter.
         * <p>
         *
         * @param context The current context.
         * @param list The list of expenditures to display.
         */
        public SearchAdapter(android.content.Context context, List<Expenditure> list) {
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
