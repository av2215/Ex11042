package com.example.ex11042;

import java.io.Serializable;

/**
 * @author itay vaknin av2215@bs.amalnet.k12.il
 * @version 1.0
 * @since 22/03/2026
 * Model class representing an expenditure record.
 */
public class Expenditure implements Serializable {
    private int id;
    private String description;
    private double amount;
    private String category;
    private String date;

    /**
     * Constructor for an existing expenditure with an ID.
     * <p>
     *
     * @param id The unique identifier of the expenditure.
     * @param description A brief description of the expense.
     * @param amount The cost of the expenditure.
     * @param category The category of the expenditure.
     * @param date The date when the expenditure occurred.
     */
    public Expenditure(int id, String description, double amount, String category, String date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    /**
     * Constructor for a new expenditure without an ID.
     * <p>
     *
     * @param description A brief description of the expense.
     * @param amount The cost of the expenditure.
     * @param category The category of the expenditure.
     * @param date The date when the expenditure occurred.
     */
    public Expenditure(String description, double amount, String category, String date) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    /**
     * Gets the expenditure ID.
     * <p>
     *
     * @return int The unique identifier of the expenditure.
     */
    public int getId() { return id; }

    /**
     * Sets the expenditure ID.
     * <p>
     *
     * @param id The unique identifier to set.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Gets the expenditure description.
     * <p>
     *
     * @return String The description of the expense.
     */
    public String getDescription() { return description; }

    /**
     * Gets the expenditure amount.
     * <p>
     *
     * @return double The cost of the expenditure.
     */
    public double getAmount() { return amount; }

    /**
     * Gets the expenditure category.
     * <p>
     *
     * @return String The category of the expenditure.
     */
    public String getCategory() { return category; }

    /**
     * Gets the expenditure date.
     * <p>
     *
     * @return String The date when the expenditure occurred.
     */
    public String getDate() { return date; }
}
