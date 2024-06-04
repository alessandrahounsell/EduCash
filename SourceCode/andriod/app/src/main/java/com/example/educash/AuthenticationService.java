package com.example.educash;

import android.os.Handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationService {

    public interface SignUpCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public void createUserWithEmailAndPassword(String username, String password, SignUpCallback callback) {
        new Handler().postDelayed(() -> {
            if (isValidUsername(username) && isValidPassword(password)) {
                // Save user to DB
                callback.onSuccess();
            } else if (!isValidPassword(password)) {
                callback.onFailure("Invalid Password, please enter a password which contains at least 8 characters, at least one uppercase letter, at least one digit, and at least one spacial character and excluding */-,’");
            }
            else {
                callback.onFailure("Invalid username, please pick a different one");

            }
        }, 1000); // Simulating a network delay of 1 second
    }

        private boolean isValidUsername(String username) {
            // Check against DB
            return true;
        }

    private boolean isValidPassword(String password) {
        // At least 8 characters, at least one uppercase letter, at least one special character, and at least one digit and excluding */-,’
        String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?~\\\\-])(?!.*[*/',]).{8,}$";
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    }


