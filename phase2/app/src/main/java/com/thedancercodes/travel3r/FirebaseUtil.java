package com.thedancercodes.travel3r;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    private static ListActivity caller;
    public static boolean isAdmin;

    // Empty private constructor: To avoid this class being instantiated from outside itself.
    private FirebaseUtil(){}

    // Generic static method that will open a reference of the child that is passed as a parameter
    // If this method has already been called, it will do nothing and return itself.
    // Otherwise, it will create a single instance of itself.
    public static void openFbReference(String ref, final ListActivity callerActivity) {
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
                    else {
                        // Retrieve the UID of the current user
                        String userId = firebaseAuth.getUid();

                        // Method that checks whether the user is an admin
                        checkAdmin(userId);

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

    // Method that checks whether the user is an admin
    private static void checkAdmin(String uid) {

        // FALSE by default
        FirebaseUtil.isAdmin = false;

        // DB Reference to the administrator nodes, but only for child that has the passed uid.
        DatabaseReference ref = firebaseDatabase.getReference().child("administrators")
                .child(uid);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                // Set variable to TRUE
                FirebaseUtil.isAdmin = true;

                caller.showMenu();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        // Add ChildEventListener to the DB reference.
        ref.addChildEventListener(listener);
    }

    // Methods to attach and detach the AuthStateListener
    public static void attachListener() {
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static void detachListener() {
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

}
