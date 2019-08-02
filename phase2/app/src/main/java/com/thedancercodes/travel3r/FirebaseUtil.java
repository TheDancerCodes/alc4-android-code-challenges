package com.thedancercodes.travel3r;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * All the members and methods of this class will be static.
 *
 * We will be able to call them without instantiating any object of this class.
 */

public class FirebaseUtil {
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static FirebaseUtil firebaseUtil;
    public static ArrayList<TravelDeal> deals;

    // Empty private constructor: To avoid this class being instantiated from outside itself.
    private FirebaseUtil(){}

    // Generic static method that will open a reference of the child that is passed as a parameter
    // If this method has already been called, it will do nothing and return itself.
    // Otherwise, it will create a single instance of itself.
    public static void openFbReference(String ref) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();

            // FirebaseDatabase instance
            firebaseDatabase = FirebaseDatabase.getInstance();
        }

        // Empty ArrayList of deals
        // This resets the ArrayList every time the ListActivity is launched.
        deals = new ArrayList<TravelDeal>();

        // Open the path that was passed as a parameter
        databaseReference = firebaseDatabase.getReference().child(ref);
    }

}
