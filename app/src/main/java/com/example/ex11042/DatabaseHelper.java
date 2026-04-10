package com.example.ex11042;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Database helper class for managing expenditure data in SQLite.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "expenses.db";
    private static final int DATABASE_VERSION = 2;

    // Constants for table and columns
    public static final String TABLE_NAME = "expenditures";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_AMOUNT = "Amount";
    public static final String COLUMN_CATEGORY = "Category";
    public static final String COLUMN_DATE = "Date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_DATE + " TEXT)";
        Log.d(TAG, "SQL Query: " + createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Adds a new expenditure record.
     * @param expenditure The expenditure object to add.
     * @return The ID of the newly inserted row.
     */
    public long addExpenditure(Expenditure expenditure) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, expenditure.getDescription());
        values.put(COLUMN_AMOUNT, expenditure.getAmount());
        values.put(COLUMN_CATEGORY, expenditure.getCategory());
        values.put(COLUMN_DATE, expenditure.getDate());
        Log.d(TAG, "Adding expenditure: " + expenditure.getDescription());
        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * Updates an existing expenditure record.
     * @param expenditure The expenditure object with updated data.
     * @return Number of rows affected.
     */
    public int updateExpenditure(Expenditure expenditure) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, expenditure.getDescription());
        values.put(COLUMN_AMOUNT, expenditure.getAmount());
        values.put(COLUMN_CATEGORY, expenditure.getCategory());
        values.put(COLUMN_DATE, expenditure.getDate());
        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(expenditure.getId())});
    }

    /**
     * Deletes an expenditure record by ID.
     * @param id The ID of the record to delete.
     */
    public void deleteExpenditure(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Retrieves all expenditures sorted by date (newest to oldest).
     * @return List of expenditures.
     */
    public List<Expenditure> getAllExpenditures() {
        List<Expenditure> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATE + " DESC";
        Log.d(TAG, "SQL Query: " + query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Expenditure(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * Performs a search and filter on expenditures.
     * @param desc Search term for description.
     * @param min Min amount.
     * @param max Max amount.
     * @param sortBy Column to sort by.
     * @return Filtered list of expenditures.
     */
    public List<Expenditure> searchExpenditures(String desc, Double min, Double max, String sortBy) {
        List<Expenditure> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder query = new StringBuilder("SELECT * FROM " + TABLE_NAME + " WHERE 1=1");
        
        if (desc != null && !desc.isEmpty()) {
            String escapedDesc = desc.replace("'", "''");
            query.append(" AND ").append(COLUMN_DESCRIPTION).append(" LIKE '%").append(escapedDesc).append("%'");
        }
        if (min != null) {
            query.append(" AND ").append(COLUMN_AMOUNT).append(" >= ").append(min);
        }
        if (max != null) {
            query.append(" AND ").append(COLUMN_AMOUNT).append(" <= ").append(max);
        }
        
        if (sortBy != null) {
            query.append(" ORDER BY ").append(sortBy).append(" DESC");
        }

        Log.d(TAG, "SQL Search Query: " + query.toString());
        Cursor cursor = db.rawQuery(query.toString(), null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Expenditure(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * Calculates the total expenses for the current month.
     * @return Sum of amounts for current month.
     */
    public double getTotalMonthlyExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Assuming current month is formatted in Date column as YYYY-MM
        String currentMonth = new java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault()).format(new java.util.Date());
        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " LIKE '" + currentMonth + "%'";
        Log.d(TAG, "SQL Query: " + query);
        Cursor cursor = db.rawQuery(query, null);
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }
}
