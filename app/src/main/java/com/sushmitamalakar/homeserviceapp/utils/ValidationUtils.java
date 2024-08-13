package com.sushmitamalakar.homeserviceapp.utils;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

public class ValidationUtils {

    public static boolean validateFullName(String fullName, EditText fullNameEditText) {
        if (TextUtils.isEmpty(fullName)) {
            fullNameEditText.setError("Full name is required");
            return false;
        }
        return true;
    }

    public static boolean validateEmail(String email, EditText emailEditText) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required");
            return false;
        }
        return true;
    }

    public static boolean validateMobileNo(String mobileNo, EditText mobileEditText) {
        if (TextUtils.isEmpty(mobileNo)) {
            mobileEditText.setError("Mobile no. is required");
            return false;
        }
        if (mobileNo.length() != 10) {
            mobileEditText.setError("Mobile no. should be 10 digits");
            return false;
        }
        return true;
    }

    public static boolean validatePassword(String password, EditText passwordEditText) {
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password too weak");
            return false;
        }
        return true;
    }

    public static boolean validateConfirmPassword(String password, String confirmPassword, EditText confirmPasswordEditText, EditText passwordEditText) {
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Password confirmation is required");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords should match");
            passwordEditText.clearComposingText();
            confirmPasswordEditText.clearComposingText();
            return false;
        }
        return true;
    }
}
