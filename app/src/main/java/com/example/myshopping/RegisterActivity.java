package com.example.myshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    EditText emailId, phoneNo, password, fullName;
    Button register;
    ProgressDialog pd;
    TextView iAmSeller, iAmNotSeller;
    private boolean isSeller = false;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private boolean isPhoneExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        emailId = (EditText) findViewById(R.id.editText);
        phoneNo = (EditText) findViewById(R.id.editText3);
        password = (EditText) findViewById(R.id.editText2);
        register = (Button) findViewById(R.id.button);
        fullName = (EditText) findViewById(R.id.editText4);

        iAmSeller = (TextView) findViewById(R.id.sellers);
        iAmNotSeller = (TextView) findViewById(R.id.notseller);

        iAmNotSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 iAmNotSeller.setVisibility(View.INVISIBLE);
                iAmSeller.setVisibility(View.VISIBLE);
                register.setText("Register");
                isSeller = false;
            }
        });

        iAmSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 register.setText("Register as Seller");
                iAmNotSeller.setVisibility(View.VISIBLE);
                iAmSeller.setVisibility(View.INVISIBLE);
                isSeller = true;
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    void createAccount(){
        if( !isFilled() ){
            Toast.makeText(getApplicationContext(),"please fill all the fields",Toast.LENGTH_LONG).show();
            return;
        }

        pd = new ProgressDialog(RegisterActivity.this);
        pd.setTitle("Creating Account");
        pd.setMessage("Please wait while account is creating");
        pd.show();

        if( isSeller ){
            databaseReference = FirebaseDatabase.getInstance().getReference("Seller/");
        }
        else{
            databaseReference = FirebaseDatabase.getInstance().getReference("User/");
        }


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if( dataSnapshot.child(phoneNo.getText().toString()).exists() ){
                    Toast.makeText(getApplicationContext(),"Phone number already exists",Toast.LENGTH_LONG).show();
                    isPhoneExist = true;
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pd.dismiss();
                    Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


        if( !isPhoneExist ){

            mAuth.createUserWithEmailAndPassword(emailId.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if( task.isSuccessful() ){



                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                                String date =simpleDateFormat.format(calendar.getTime());
                                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("h:mm a");
                                String time = simpleDateFormat1.format(calendar.getTime());

                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("Full Name",fullName.getText().toString());
                                hashMap.put("Phone",phoneNo.getText().toString());
                                hashMap.put("Password",password.getText().toString());
                                hashMap.put("Date",date);
                                hashMap.put("Time",time);

                                Task task1= databaseReference.child(emailId.getText().toString().replace('.','_')).setValue(hashMap);

                                task1.addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(getApplicationContext(),"Uploaded success",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                                Log.d("Upload error",e.getMessage());
                                            }
                                        });

                                pd.dismiss();
                                Toast.makeText(getApplicationContext(),"Account creating Success",Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(i);
                            }
                            else{
                                pd.dismiss();
                                Log.d("Error",task.getException().getMessage());
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Log.d("Error",e.getMessage().toString());
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

        }
    }

    boolean isFilled(){
        if(TextUtils.isEmpty(emailId.getText().toString()))
            return  false;
        if(TextUtils.isEmpty(phoneNo.getText().toString()))
            return  false;
        if(TextUtils.isEmpty(password.getText().toString()))
            return  false;

        return true;
    }
}
