package com.example.myshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button login;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private TextView iAmSeller, iAmNotSeller;
    private boolean seller = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        iAmSeller = findViewById(R.id.textView4);
        iAmNotSeller = findViewById(R.id.textView5);
        login = findViewById(R.id.button);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User/");


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isFilled() ){
                   checkEmailPass();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please fill all the fields",Toast.LENGTH_LONG).show();
                }
            }
        });


        iAmSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("Login as Seller");
                iAmSeller.setVisibility(View.INVISIBLE);
                iAmNotSeller.setVisibility(View.VISIBLE);
                seller = true;

            }
        });

        iAmNotSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("Login");
                iAmNotSeller.setVisibility(View.INVISIBLE);
                iAmSeller.setVisibility(View.VISIBLE);
                seller = false;

            }
        });



    }

    private void checkEmailPass() {

        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                 .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {

                         if( task.isSuccessful()){
                             checkIsSeller();
                         }
                         else{
                             Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                         }
                     }
                 })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG) .show();
                    }
                });
    }

    private void checkIsSeller() {

        if( seller ){
            goToSellerOrUser("Seller ","Seller");
        }
        else
            goToSellerOrUser("User ","User");
    }

    void goToSellerOrUser(final String toastMsg, String path){

        databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if( dataSnapshot.child(email.getText().toString().replace('.','_')).exists()){
                    Toast.makeText(getApplicationContext(),toastMsg + "logged in Sucess",Toast.LENGTH_LONG).show();

                    if( seller ){
                        Intent i = new Intent(getApplicationContext(), HomeSellerActivity.class);
                        startActivity(i);
                    }
                    else{
                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(i);
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),toastMsg + "does not exist",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    boolean isFilled(){
        if(TextUtils.isEmpty(email.getText().toString()))
            return  false;
        if(TextUtils.isEmpty(password.getText().toString()))
            return  false;

        return true;
    }
}
