package com.cmloopy.quizzi;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.adapter.FoodAdapter;
import com.cmloopy.quizzi.models.FoodItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements FoodAdapter.OnItemClickListener {
    // UI components
    private TextView tvFoodId, tvTotalPrice;
    private EditText edtFoodName, edtPrice, edtQuantity, edtSearch;
    private Spinner spinnerFoodType, spinnerSearchType;
    private RatingBar ratingBarInput;
    private CheckBox cbSearchByName, cbSearchByType;
    private Button btnAdd, btnUpdate, btnClear, btnSearch;
    private RecyclerView recyclerViewFoods;

    // Data
    private List<FoodItem> foodList;
    private List<FoodItem> filteredList;
    private FoodAdapter adapter;
    private DatabaseHelper dbHelper;
    private String[] foodTypes = {"Bánh", "Đồ uống", "Café"};
    private FoodItem selectedFood = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize UI components
        initializeUI();

        // Set up spinners
        setupSpinners();

        // Set up RecyclerView
        setupRecyclerView();

        // Load data
        loadData();

        // Set up button click listeners
        setupClickListeners();
    }

    private void initializeUI() {
        tvFoodId = findViewById(R.id.tvFoodId);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        edtFoodName = findViewById(R.id.edtFoodName);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtSearch = findViewById(R.id.edtSearch);
        spinnerFoodType = findViewById(R.id.spinnerFoodType);
        spinnerSearchType = findViewById(R.id.spinnerSearchType);
        ratingBarInput = findViewById(R.id.ratingBarInput);
        cbSearchByName = findViewById(R.id.cbSearchByName);
        cbSearchByType = findViewById(R.id.cbSearchByType);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnClear = findViewById(R.id.btnClear);
        btnSearch = findViewById(R.id.btnSearch);
        recyclerViewFoods = findViewById(R.id.recyclerViewFoods);
    }

    private void setupSpinners() {
        // Setup food type spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, foodTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoodType.setAdapter(typeAdapter);

        // Setup search type spinner with the same adapter
        spinnerSearchType.setAdapter(typeAdapter);

        // Generate ID based on selected food type
        spinnerFoodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedFood == null) {
                    String selectedType = foodTypes[position];
                    String nextId = dbHelper.generateNextId(selectedType);
                    tvFoodId.setText(nextId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupRecyclerView() {
        foodList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new FoodAdapter(this, filteredList, this);
        recyclerViewFoods.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFoods.setAdapter(adapter);
    }

    private void loadData() {
        foodList = dbHelper.getAllFoodItems();
        filteredList.clear();
        filteredList.addAll(foodList);
        adapter.notifyDataSetChanged();
        calculateTotalPrice();
    }

    private void setupClickListeners() {
        // Add button click listener
        btnAdd.setOnClickListener(v -> addFood());

        // Update button click listener
        btnUpdate.setOnClickListener(v -> updateFood());

        // Clear button click listener
        btnClear.setOnClickListener(v -> clearForm());

        // Search button click listener
        btnSearch.setOnClickListener(v -> searchFood());
    }

    private void addFood() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        // Get values from UI
        String id = tvFoodId.getText().toString();
        String name = edtFoodName.getText().toString().trim();
        String type = foodTypes[spinnerFoodType.getSelectedItemPosition()];
        double price = Double.parseDouble(edtPrice.getText().toString());
        int quantity = Integer.parseInt(edtQuantity.getText().toString());
        float rating = ratingBarInput.getRating();

        // Create food item
        FoodItem food = new FoodItem(id, name, type, price, quantity, rating);

        // Insert into database
        if (dbHelper.insertFood(food)) {
            Toast.makeText(this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();

            // Refresh data
            loadData();

            // Clear form
            clearForm();
        } else {
            Toast.makeText(this, "Lỗi khi thêm món ăn", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFood() {
        // Check if a food item is selected
        if (selectedFood == null) {
            Toast.makeText(this, "Vui lòng chọn món ăn để sửa", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate input
        if (!validateInput()) {
            return;
        }

        // Get values from UI
        String id = tvFoodId.getText().toString();
        String name = edtFoodName.getText().toString().trim();
        String type = foodTypes[spinnerFoodType.getSelectedItemPosition()];
        double price = Double.parseDouble(edtPrice.getText().toString());
        int quantity = Integer.parseInt(edtQuantity.getText().toString());
        float rating = ratingBarInput.getRating();

        // Update food item
        selectedFood.setName(name);
        selectedFood.setType(type);
        selectedFood.setPrice(price);
        selectedFood.setQuantity(quantity);
        selectedFood.setRating(rating);

        // Update in database
        if (dbHelper.updateFood(selectedFood)) {
            Toast.makeText(this, "Cập nhật món ăn thành công", Toast.LENGTH_SHORT).show();

            // Refresh data
            loadData();

            // Clear form
            clearForm();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật món ăn", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFood(FoodItem food) {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Xóa món ăn")
                .setMessage("Bạn có chắc chắn muốn xóa " + food.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Delete from database
                    if (dbHelper.deleteFood(food.getId())) {
                        Toast.makeText(this, "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();

                        // Refresh data
                        loadData();

                        // Clear form if the deleted item was selected
                        if (selectedFood != null && selectedFood.getId().equals(food.getId())) {
                            clearForm();
                        }
                    } else {
                        Toast.makeText(this, "Lỗi khi xóa món ăn", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void searchFood() {
        filteredList.clear();

        String searchName = null;
        String searchType = null;

        // Check search criteria
        if (cbSearchByName.isChecked()) {
            searchName = edtSearch.getText().toString().trim();
        }

        if (cbSearchByType.isChecked()) {
            searchType = foodTypes[spinnerSearchType.getSelectedItemPosition()];
        }

        // Search in database
        List<FoodItem> searchResults = dbHelper.searchFoodItems(searchName, searchType);
        filteredList.addAll(searchResults);

        // Update adapter
        adapter.notifyDataSetChanged();

        // Calculate total price
        calculateTotalPrice();

        // Show message
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy món ăn phù hợp", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        // Reset selected food
        selectedFood = null;

        // Clear inputs
        edtFoodName.setText("");
        edtPrice.setText("");
        edtQuantity.setText("");
        ratingBarInput.setRating(0);

        // Reset ID
        String selectedType = foodTypes[spinnerFoodType.getSelectedItemPosition()];
        String nextId = dbHelper.generateNextId(selectedType);
        tvFoodId.setText(nextId);

        // Enable add button, disable update button
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
    }

    private boolean validateInput() {
        // Validate name
        if (TextUtils.isEmpty(edtFoodName.getText())) {
            Toast.makeText(this, "Vui lòng nhập tên món ăn", Toast.LENGTH_SHORT).show();
            edtFoodName.requestFocus();
            return false;
        }

        // Validate price
        if (TextUtils.isEmpty(edtPrice.getText())) {
            Toast.makeText(this, "Vui lòng nhập giá", Toast.LENGTH_SHORT).show();
            edtPrice.requestFocus();
            return false;
        }

        try {
            double price = Double.parseDouble(edtPrice.getText().toString());
            if (price <= 0) {
                Toast.makeText(this, "Giá phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                edtPrice.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            edtPrice.requestFocus();
            return false;
        }

        // Validate quantity
        if (TextUtils.isEmpty(edtQuantity.getText())) {
            Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
            edtQuantity.requestFocus();
            return false;
        }

        try {
            int quantity = Integer.parseInt(edtQuantity.getText().toString());
            if (quantity <= 0) {
                Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                edtQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            edtQuantity.requestFocus();
            return false;
        }

        return true;
    }

    private void calculateTotalPrice() {
        double totalPrice = 0;
        for (FoodItem food : filteredList) {
            totalPrice += food.getTotalPrice();
        }
        tvTotalPrice.setText(String.format(Locale.getDefault(), "Tổng tiền: %.0fđ", totalPrice));
    }

    @Override
    public void onItemClick(FoodItem foodItem, int position) {
        // Set the selected food
        selectedFood = foodItem;

        // Populate form with selected food data
        tvFoodId.setText(foodItem.getId());
        edtFoodName.setText(foodItem.getName());
        edtPrice.setText(String.format(Locale.getDefault(), "%.0f", foodItem.getPrice()));
        edtQuantity.setText(String.valueOf(foodItem.getQuantity()));
        ratingBarInput.setRating(foodItem.getRating());

        // Set food type spinner
        for (int i = 0; i < foodTypes.length; i++) {
            if (foodTypes[i].equals(foodItem.getType())) {
                spinnerFoodType.setSelection(i);
                break;
            }
        }

        // Disable add button, enable update button
        btnAdd.setEnabled(false);
        btnUpdate.setEnabled(true);
    }

    @Override
    public void onDeleteClick(FoodItem foodItem, int position) {
        deleteFood(foodItem);
    }
}