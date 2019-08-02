package com.thedancercodes.travel3r;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    // ArrayList of TravelDeals
    ArrayList<TravelDeal> deals;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener; // Need to listen for every time an item is added

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Reference to the RecyclerView
        RecyclerView rvDeals = findViewById(R.id.rvDeals);

        // Declare a new DealAdapter & set the Adapter on the RecyclerView
        final DealAdapter adapter = new DealAdapter();
        rvDeals.setAdapter(adapter);

        // Declare a LinearLayout Manager & set it to the RecyclerView
        LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvDeals.setLayoutManager(dealsLayoutManager);
    }
}
