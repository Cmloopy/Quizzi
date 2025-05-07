package com.cmloopy.quizzi.views;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cmloopy.quizzi.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class CreateAccountStep1Activity extends AppCompatActivity {
    private EditText fullNameInput;
    private EditText dobInput;
    private EditText phoneInput;
    private EditText ageInput;
    private Spinner countrySpinner;
    private Button continueButton;
    private ImageButton backButton;
    private ImageView calendarIcon;
    private View fullNameDivider;
    private View dobDivider;
    private View phoneDivider;
    private View countryDivider;
    private View ageDivider;
    private TextView countryLabel;
    private boolean countrySelected = false;
    private boolean firstTimeCountrySelected = false; // Added missing variable

    // Error message TextViews
    private TextView fullNameError;
    private TextView dobError;
    private TextView phoneError;
    private TextView ageError;
    private TextView countryError;
    // Validation patterns
    private static final Pattern VIETNAM_PHONE_PATTERN = Pattern.compile("^(\\+84|84|0)\\d{9,10}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");
    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 120;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    private boolean isFullNameInteracted = false;
    private boolean isDobInteracted = false;
    private boolean isPhoneInteracted = false;
    private boolean isAgeInteracted = false;
    private boolean isCountryInteracted = false;
    private boolean isFirstContinueClick = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_step1);

        // Initialize views
        fullNameInput = findViewById(R.id.fullNameInput);
        dobInput = findViewById(R.id.dobInput);
        phoneInput = findViewById(R.id.phoneInput);
        ageInput = findViewById(R.id.ageInput);
        countrySpinner = findViewById(R.id.countrySpinner);
        continueButton = findViewById(R.id.continueButton);
        backButton = findViewById(R.id.backButton);
        calendarIcon = findViewById(R.id.calendarIcon);
        fullNameDivider = findViewById(R.id.fullNameDivider);
        dobDivider = findViewById(R.id.dobDivider);
        phoneDivider = findViewById(R.id.phoneDivider);
        countryDivider = findViewById(R.id.countryDivider);
        ageDivider = findViewById(R.id.ageDivider);
        countryLabel = findViewById(R.id.countryLabel);

        // Initialize error message TextViews
        fullNameError = findViewById(R.id.fullNameError);
        dobError = findViewById(R.id.dobError);
        phoneError = findViewById(R.id.phoneError);
        ageError = findViewById(R.id.ageError);
        countryError = findViewById(R.id.countryError);

        // Hide error messages initially
        hideAllErrorMessages();
//        if (isFirstContinueClick) {
//            // Setup validation listeners only on first click
//            setupValidationListeners();
//        }

        // Setup color changes for edit texts
        setupEditTextColorChanges(fullNameInput, fullNameDivider, null);
        setupEditTextColorChanges(dobInput, dobDivider, calendarIcon);
        setupEditTextColorChanges(phoneInput, phoneDivider, null);
        setupEditTextColorChanges(ageInput, ageDivider, null);

        // Setup calendar icon click listener
        calendarIcon.setOnClickListener(v -> showDatePicker());
        dobInput.setOnClickListener(v -> showDatePicker());

        // Setup country spinner with placeholder
        String chooseCountryText = "Choose your country";
        String[] countries = {chooseCountryText, "Vietnam", "United States", "Canada", "United Kingdom", "Australia", "Germany"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(
                this, R.layout.ui_07_country_spinner_item, countries) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        countryAdapter.setDropDownViewResource(R.layout.ui_07_country_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        // Handle country selection
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countrySelected = position > 0;
                updateCountrySpinnerState();

                // Flag that country has been interacted with only if a real selection is made
                if (position > 0 || !countries[position].equals(chooseCountryText)) {
                    isCountryInteracted = true;
                }

                // Update phone input hint based on country
                if (position == 1) { // Vietnam
                    phoneInput.setHint("0XXXXXXXXX");
                } else {
                    phoneInput.setHint("+XX-XXX-XXX-XXXX");
                }

                // Only validate if this isn't the first time loading
                if (firstTimeCountrySelected && !countries[position].equals(chooseCountryText)) {
                    firstTimeCountrySelected = false;
                } else {
                    validateCountry();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                countrySelected = false;
                updateCountrySpinnerState();
                validateCountry();
            }
        });

        // Setup continue button
        continueButton.setOnClickListener(v -> {
            // Mark all fields as interacted when trying to continue
            setAllFieldsInteracted();
            isFirstContinueClick = true;
            if (isFirstContinueClick) {
                setupValidationListeners();
            }

            if (validateAllFields()) {
                Intent intent = new Intent(CreateAccountStep1Activity.this, CreateAccountStep2Activity.class);
                // Add user data to intent
                intent.putExtra("FULL_NAME", fullNameInput.getText().toString().trim());
                intent.putExtra("DOB", dobInput.getText().toString().trim());
                intent.putExtra("PHONE", phoneInput.getText().toString().trim());
                intent.putExtra("AGE", ageInput.getText().toString().trim());
                intent.putExtra("COUNTRY", countrySpinner.getSelectedItem().toString());
                startActivity(intent);
            } else {
                // Show a toast if validation fails
                Toast.makeText(this, "Please fix the errors before continuing", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup back button
        backButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });


        // Check initial state of EditTexts and update colors accordingly
        updateEditTextState(fullNameInput, fullNameDivider, null);
        updateEditTextState(dobInput, dobDivider, calendarIcon);
        updateEditTextState(phoneInput, phoneDivider, null);
        updateEditTextState(ageInput, ageDivider, null);
        updateCountrySpinnerState();
    }

    private void setAllFieldsInteracted() {
        isFullNameInteracted = true;
        isDobInteracted = true;
        isPhoneInteracted = true;
        isAgeInteracted = true;
        isCountryInteracted = true;
    }

    private void hideAllErrorMessages() {
        if (fullNameError != null) fullNameError.setVisibility(View.GONE);
        if (dobError != null) dobError.setVisibility(View.GONE);
        if (phoneError != null) phoneError.setVisibility(View.GONE);
        if (ageError != null) ageError.setVisibility(View.GONE);
        if (countryError != null) countryError.setVisibility(View.GONE);
    }

    private void setupValidationListeners() {
        fullNameInput.addTextChangedListener(new ValidationTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                isFullNameInteracted = true;
                validateFullName();
            }
        });

        phoneInput.addTextChangedListener(new ValidationTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                isPhoneInteracted = true;
                validatePhone();
            }
        });

        ageInput.addTextChangedListener(new ValidationTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                isAgeInteracted = true;
                validateAge();
            }
        });

        dobInput.addTextChangedListener(new ValidationTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                isDobInteracted = true;
                validateDob();
            }
        });
    }

    private boolean validateAllFields() {
        boolean isFullNameValid = validateFullName();
        boolean isDobValid = validateDob();
        boolean isPhoneValid = validatePhone();
        boolean isAgeValid = validateAge();
        boolean isCountryValid = validateCountry();

        return isFullNameValid && isDobValid && isPhoneValid && isAgeValid && isCountryValid;
    }

    private boolean validateFullName() {
        String fullName = fullNameInput.getText().toString().trim();

        // Only validate if user has interacted with this field
        if (!isFullNameInteracted) {
            return true;
        }
        int fullNameLimitLength = 3;
        if (fullName.isEmpty()) {
            showError(fullNameError, "Full name is required");
            return false;
        } else if (!NAME_PATTERN.matcher(fullName).matches()) {
            showError(fullNameError, "Please enter a valid name (should not contain special characters or numbers)");
            return false;
        } else if (fullName.length() < fullNameLimitLength) {
            showError(fullNameError, String.format("Name must be at least %d characters", fullNameLimitLength));
            return false;
        } else {
            hideError(fullNameError);
            return true;
        }
    }

    private boolean validateDob() {
        String dob = dobInput.getText().toString().trim();

        // Only validate if user has interacted with this field
        if (!isDobInteracted) {
            return true;
        }

        if (dob.isEmpty()) {
            showError(dobError, "Date of birth is required");
            return false;
        }

        try {
            Date birthDate = DATE_FORMAT.parse(dob);
            Calendar calendar = Calendar.getInstance();
            Calendar minAgeCalendar = Calendar.getInstance();
            minAgeCalendar.add(Calendar.YEAR, -MIN_AGE);

            if (birthDate.after(minAgeCalendar.getTime())) {
                showError(dobError, "You must be at least " + MIN_AGE + " years old");
                return false;
            }

            Calendar maxAgeCalendar = Calendar.getInstance();
            maxAgeCalendar.add(Calendar.YEAR, -MAX_AGE);

            if (birthDate.before(maxAgeCalendar.getTime())) {
                showError(dobError, "Please enter a valid date of birth");
                return false;
            }

            hideError(dobError);
            return true;
        } catch (ParseException e) {
            showError(dobError, "Please enter a valid date (MM/DD/YYYY)");
            return false;
        }
    }

    private boolean validatePhone() {
        String phone = phoneInput.getText().toString().trim();

        // Only validate if user has interacted with this field
        if (!isPhoneInteracted) {
            return true;
        }

        if (phone.isEmpty()) {
            showError(phoneError, "Phone number is required");
            return false;
        }

        // Get selected country
        String country = "Vietnam"; // Default
        if (countrySpinner.getSelectedItemPosition() > 0) {
            country = countrySpinner.getSelectedItem().toString();
        }

        // Validate based on country
        if ("Vietnam".equals(country)) {
            // Vietnamese phone validation
            if (!VIETNAM_PHONE_PATTERN.matcher(phone).matches()) {
                showError(phoneError, "Enter a valid Vietnamese phone number (e.g., 0912345678)");
                return false;
            }
        } else {
            // Basic validation for other countries
            if (phone.length() < 7 || phone.length() > 15) {
                showError(phoneError, "Enter a valid phone number");
                return false;
            }
        }

        hideError(phoneError);
        return true;
    }

    private boolean validateAge() {
        String ageText = ageInput.getText().toString().trim();

        // Only validate if user has interacted with this field
        if (!isAgeInteracted) {
            return true;
        }

        if (ageText.isEmpty()) {
            showError(ageError, "Age is required");
            return false;
        }

        try {
            int age = Integer.parseInt(ageText);
            if (age < MIN_AGE) {
                showError(ageError, "Minimum age is " + MIN_AGE);
                return false;
            } else if (age > MAX_AGE) {
                showError(ageError, "Please enter a valid age");
                return false;
            } else {
                hideError(ageError);
                return true;
            }
        } catch (NumberFormatException e) {
            showError(ageError, "Please enter a valid number");
            return false;
        }
    }

    private boolean validateCountry() {
        if (isCountryInteracted && !countrySelected) {
            showError(countryError, "Please select your country");
            return false;
        } else {
            hideError(countryError);
            return countrySelected || !isCountryInteracted;
        }
    }

    private void showError(TextView errorTextView, String message) {
        if (errorTextView != null) {
            errorTextView.setText(message);
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setTextColor(ContextCompat.getColor(this, R.color.errorColor));
        }
    }

    private void hideError(TextView errorTextView) {
        if (errorTextView != null) {
            errorTextView.setVisibility(View.GONE);
        }
    }

    private void updateCountrySpinnerState() {
        if (countrySelected) {
            countryLabel.setTextColor(ContextCompat.getColor(this, R.color.validColor));
            countryDivider.setBackgroundColor(ContextCompat.getColor(this, R.color.validColor));
        } else {
            countryLabel.setTextColor(ContextCompat.getColor(this, R.color.defaultTextColor));
            countryDivider.setBackgroundColor(ContextCompat.getColor(this, R.color.defaultDividerColor));
        }
    }

    private void updateEditTextState(EditText editText, View divider, ImageView icon) {
        if (editText.getText().toString().trim().isEmpty()) {
            divider.setBackgroundColor(ContextCompat.getColor(this, R.color.defaultDividerColor));
            if (icon != null) {
                icon.setColorFilter(ContextCompat.getColor(this, R.color.defaultIconColor));
            }
        } else {
            divider.setBackgroundColor(ContextCompat.getColor(this, R.color.validColor));
            if (icon != null) {
                icon.setColorFilter(ContextCompat.getColor(this, R.color.validColor));
            }
        }
    }

    private void setupEditTextColorChanges(EditText editText, View divider, ImageView icon) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                divider.setBackgroundColor(ContextCompat.getColor(this, R.color.focusedColor));
                if (icon != null) {
                    icon.setColorFilter(ContextCompat.getColor(this, R.color.focusedColor));
                }
            } else {
                updateEditTextState(editText, divider, icon);
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (view, selectedYear, selectedMonth, selectedDay) -> {
            calendar.set(selectedYear, selectedMonth, selectedDay);
            String formattedDate = DATE_FORMAT.format(calendar.getTime());
            dobInput.setText(formattedDate);
            isDobInteracted = true;
            validateDob();
        }, year, month, day);

        datePickerDialog.show();
    }

    private abstract class ValidationTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
