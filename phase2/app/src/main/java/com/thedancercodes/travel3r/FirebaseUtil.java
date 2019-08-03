package com.thedancercodes.travel3r;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * All the members and methods of this class will be static.
 *
 * We will be able to call them without instantiating any object of this class.
 */

public class FirebaseUtil {
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static FirebaseUtil firebaseUtil;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseAuth.AuthStateListener authStateListener;
    public static ArrayList<TravelDeal> deals;
    private static final int RC_SIGN_IN = 123;
    private static Activity caller;


    // Empty private constructor: To avoid this class being instantiated from outside itself.
    private FirebaseUtil(){}

    // Generic static method that will open a reference of the child that is passed as a parameter
    // If this method has already been called, it will do nothing and return itself.
    // Otherwise, it will create a single instance of itself.
    public static void openFbReference(String ref, final Activity callerActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();

            // FirebaseDatabase instance
            firebaseDatabase = FirebaseDatabase.getInstance();

            // Initialize Firebase Auth object
            firebaseAuth = FirebaseAuth.getInstance();

            // Caller Initialization
            caller = callerActivity;

            // Initialize AuthStateListener
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    // Control to check whether user is logged in or not
                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                }
            };
        }

        // Empty ArrayList of deals
        // This resets the ArrayList every time the ListActivity is launched.
        deals = new ArrayList<TravelDeal>();

        // Open the path that was passed as a parameter
        databaseReference = firebaseDatabase.getReference().child(ref);
    }

    private static void signIn() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());




        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    // Methods to attach and detach the AuthStateListener
    public static void attachListener() {
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static void detachListener() {
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

}
