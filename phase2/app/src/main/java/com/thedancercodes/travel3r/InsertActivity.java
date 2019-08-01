package com.thedancercodes.travel3r;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertActivity extends AppCompatActivity {

    // FirebaseDatabase & DatabaseReference variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    // EditText variables
    EditText txtTitle;
    EditText txtDescription;
    EditText txtPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        // Create an instance of the FirebaseDatabase & DatabaseReference
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("traveldeals");

        // Reference to EditTexts
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice =  findViewById(R.id.txtPrice);
    }

    // Write to the DB as the use clicks on the save menu


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // As often happens with menus, use a switch on the id of the menu item that was clicked.
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();
                clean();
                return true;
             default:
                 return super.onOptionsItemSelected(item);
        }
    }

    private void saveDeal() {

        // Read content of the 3 Edit Texts
        String title = txtTitle.getText().toString();
        String description = txtDescription.getText().toString();
        String price = txtPrice.getText().toString();

        // Create a new TravelDeal
        TravelDeal deal = new TravelDeal(title, description, price, "");

        // Insert a new item into the DB
        databaseReference.push().setValue(deal);
    }

    // Reset edit text's content after data has been sent to DB
    private void clean() {
        txtTitle.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtTitle.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Instance of MenuInflater Object. The object that creates menus from XML menu resources.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }
}
