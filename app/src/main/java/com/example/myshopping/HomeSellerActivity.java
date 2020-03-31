package com.example.myshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeSellerActivity extends AppCompatActivity {

    private Button addNewProduct, logout;
    private FirebaseAuth mAuth;
    private TextView sellerName, sellerId;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_seller);
        addNewProduct = findViewById(R.id.button3);
        logout = findViewById(R.id.button2);
        databaseReference = FirebaseDatabase.getInstance().getReference("Seller/");
        mAuth = FirebaseAuth.getInstance();
        sellerId = findViewById(R.id.sellerId);
        sellerName = findViewById(R.id.sellerName);

        getNameAndId();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                Toast.makeText(getApplicationContext(),"You logged out", Toast.LENGTH_SHORT).show();
                Intent i =new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        addNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ProductCategoryActivity.class);
                startActivity(i);
            }
        });
    }

    private void getNameAndId() {
        FirebaseUser user = mAuth.getCurrentUser();

        sellerId.setText(user.getEmail());
        DatabaseReference databaseReference1 = databaseReference.child(user.getEmail().replace('.','_'));
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sellerName.setText(dataSnapshot.child("Full Name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("error",databaseError.getMessage());
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
