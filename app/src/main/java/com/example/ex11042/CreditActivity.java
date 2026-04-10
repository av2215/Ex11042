package com.example.ex11042;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Screen showing credits and developer information.
 */
public class CreditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
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
            startActivity(intentFor(MainActivity.class));
            return true;
        } else if (id == R.id.menu_add) {
            startActivity(intentFor(ExpenditureActivity.class));
            return true;
        } else if (id == R.id.menu_search) {
            startActivity(intentFor(SearchActivity.class));
            return true;
        } else if (id == R.id.menu_credits) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent intentFor(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }
}
