package com.cmloopy.quizzi;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cmloopy.quizzi.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "food_manager.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    public static final String TABLE_FOODS = "foods";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_RATING = "rating";

    // Create table statement
    private static final String CREATE_TABLE_FOODS = "CREATE TABLE " + TABLE_FOODS + "("
            + COLUMN_ID + " TEXT PRIMARY KEY,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_TYPE + " TEXT,"
            + COLUMN_PRICE + " REAL,"
            + COLUMN_QUANTITY + " INTEGER,"
            + COLUMN_RATING + " REAL"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FOODS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOODS);
        onCreate(db);
    }

    // Insert a new food item
    public boolean insertFood(FoodItem food) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, food.getId());
        values.put(COLUMN_NAME, food.getName());
        values.put(COLUMN_TYPE, food.getType());
        values.put(COLUMN_PRICE, food.getPrice());
        values.put(COLUMN_QUANTITY, food.getQuantity());
        values.put(COLUMN_RATING, food.getRating());

        long result = db.insert(TABLE_FOODS, null, values);
        db.close();

        return result != -1;
    }

    // Update an existing food item
    public boolean updateFood(FoodItem food) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, food.getName());
        values.put(COLUMN_TYPE, food.getType());
        values.put(COLUMN_PRICE, food.getPrice());
        values.put(COLUMN_QUANTITY, food.getQuantity());
        values.put(COLUMN_RATING, food.getRating());

        int result = db.update(TABLE_FOODS, values, COLUMN_ID + " = ?", new String[]{food.getId()});
        db.close();

        return result > 0;
    }

    // Delete a food item
    public boolean deleteFood(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_FOODS, COLUMN_ID + " = ?", new String[]{id});
        db.close();

        return result > 0;
    }

    // Get all food items
    @SuppressLint("Range")
    public List<FoodItem> getAllFoodItems() {
        List<FoodItem> foodList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FOODS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                FoodItem food = new FoodItem();
                food.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                food.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                food.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
                food.setPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)));
                food.setQuantity(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)));
                food.setRating(cursor.getFloat(cursor.getColumnIndex(COLUMN_RATING)));

                foodList.add(food);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return foodList;
    }

    // Search food items by name and/or type
    @SuppressLint("Range")
    public List<FoodItem> searchFoodItems(String name, String type) {
        List<FoodItem> foodList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_FOODS;
        ArrayList<String> whereArgs = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            whereArgs.add(COLUMN_NAME + " LIKE '%" + name + "%'");
        }

        if (type != null && !type.isEmpty()) {
            whereArgs.add(COLUMN_TYPE + " = '" + type + "'");
        }

        if (!whereArgs.isEmpty()) {
            selectQuery += " WHERE " + String.join(" AND ", whereArgs);
        }

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                FoodItem food = new FoodItem();
                food.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                food.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                food.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
                food.setPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)));
                food.setQuantity(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)));
                food.setRating(cursor.getFloat(cursor.getColumnIndex(COLUMN_RATING)));

                foodList.add(food);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return foodList;
    }

    // Get the highest ID for a specific type to generate the next ID
    public String getHighestId(String type) {
        String prefix = FoodItem.getIdPrefix(type);
        String selectQuery = "SELECT MAX(" + COLUMN_ID + ") FROM " + TABLE_FOODS +
                " WHERE " + COLUMN_ID + " LIKE '" + prefix + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String highestId = prefix + "000";

        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            highestId = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return highestId;
    }

    // Generate next ID for a specific type
    public String generateNextId(String type) {
        String highestId = getHighestId(type);
        String prefix = highestId.substring(0, 1);
        int number = Integer.parseInt(highestId.substring(1)) + 1;

        return prefix + String.format("%03d", number);
    }
}