package com.thedancercodes.travel3r;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class DealActivity extends AppCompatActivity {

    // FirebaseDatabase & DatabaseReference variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private static final int PICTURE_RESULT = 42;

    // EditText variables
    EditText txtTitle;
    EditText txtDescription;
    EditText txtPrice;
    TravelDeal mDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        // Create an instance of the FirebaseDatabase & DatabaseReference
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;

        // Reference to EditTexts
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice =  findViewById(R.id.txtPrice);

        // Receive the Deal that was passed
        Intent intent = getIntent();

        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");

        // When you click the New Travel Deal Menu in the ListActivity
        if (deal == null) {
            deal = new TravelDeal();
        }

        // Put deal variable into the deal member
        this.mDeal = deal;

        // Set text for each text view.
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
        Button btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent,
                        "Insert Picture"), PICTURE_RESULT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Instance of MenuInflater Object. The object that creates menus from XML menu resources.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        // Restricting a normal user from editing or deleting a travel deal
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
        }
        else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
        }

        return true;
    }

    // Write to the DB as the use clicks on the save menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // As often happens with menus, use a switch on the id of the menu item that was clicked.
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal Saved ✅", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;

            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal Deleted ⛔️", Toast.LENGTH_LONG).show();
                backToList();
                return true;

             default:
                 return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        String uriData = data.getStringExtra("")

        // Check whether the requestCode is out PICTURE_RESULT constant
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            StorageReference ref = FirebaseUtil.storageRef.child(imageUri.getLastPathSegment());

            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getDownloadUrl().toString();

                    mDeal.setImageUrl(url);

                }
            });
        }
    }

    private void saveDeal() {

        // Read content of the 3 Edit Texts
        mDeal.setTitle(txtTitle.getText().toString());
        mDeal.setDescription(txtDescription.getText().toString());
        mDeal.setPrice(txtPrice.getText().toString());

        // Determine whether this deal is new or is an existing one via the ID
        if (mDeal.getId() == null) {

            // Insert a new item into the DB
            databaseReference.push().setValue(mDeal);
        }
        else {

            // Call the child node that has an ID of mDeal.getId()
            databaseReference.child(mDeal.getId()).setValue(mDeal);
        }
    }

    private void deleteDeal() {

        // Check whether the deal exists
        if (mDeal == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        else {

            // Get the reference of the current deal
            databaseReference.child(mDeal.getId()).removeValue();
        }
    }

    // Returns back to ListActivity after saving or deleting
    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    // Reset edit text's content after data has been sent to DB
    private void clean() {
        txtTitle.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtTitle.requestFocus();
    }

    private void enableEditTexts(boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

}
