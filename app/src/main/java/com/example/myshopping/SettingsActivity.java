package com.example.myshopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST = 12;
    private static final int OPEN_GALARY =21 ;

    private TextView cancel,upadate;
    EditText name,address;
    private CircleImageView imageView;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference,storageReference1;
    private Uri imageUri = null;
    private String email;
    private DatabaseReference databaseReference1;
    private String date,time,  imageNameForStorage, downloadUri;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        upadate = findViewById(R.id.update);
        cancel = findViewById(R.id.cancel);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        imageView = findViewById(R.id.circleImageView);
        mAuth = FirebaseAuth.getInstance();

        String person = getIntent().getStringExtra("Person");
        if(person.equals("User")){
            storageReference = FirebaseStorage.getInstance().getReference("User Images/");
            databaseReference = FirebaseDatabase.getInstance().getReference("User/");
            Toast.makeText(getApplicationContext(),"User here",Toast.LENGTH_SHORT).show();
        }
        else{
            storageReference = FirebaseStorage.getInstance().getReference("Seller Images/");
            databaseReference = FirebaseDatabase.getInstance().getReference("Seller/");
        }

        upadate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = mAuth.getCurrentUser();
                email = user.getEmail();

                update();
                Intent i = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(i);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(i);
            }
        });



        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
            }
        });


        databaseReference.child(mAuth.getCurrentUser().getEmail().toString().replace('.','_'))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        name.setHint(   dataSnapshot.child("Full Name").getValue().toString());

                        if( dataSnapshot.child("Address").exists())
                            address.setHint(  dataSnapshot.child("Address").getValue().toString());
                        else
                            address.setHint("No address is saved yet");

                        if( dataSnapshot.child("Url").exists())
                            Picasso.get().load(dataSnapshot.child("Url").getValue().toString()).placeholder(R.drawable.user).into(imageView);
                        else
                            Picasso.get().load(R.drawable.profile).into(imageView);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void update() {


        databaseReference1 = databaseReference.child(email.replace('.','_'));



        if( !TextUtils.isEmpty(name.getText().toString().trim())){
            upadateName();
        }

        if( !TextUtils.isEmpty(address.getText().toString().trim())){
            upadateAddress();
        }

        if( imageUri != null){
            upadateImage();
        }


    }



    private void upadateImage(){

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setTitle("Uploading");
        pd.show();

        imageNameForStorage = mAuth.getCurrentUser().getEmail().toString().replace('.','_');

         storageReference1 = storageReference.child(imageNameForStorage);


        storageReference1.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUri = uri.toString();
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("Url",downloadUri);
                                databaseReference1.updateChildren(hashMap);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });

                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),"Profile Updated success",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        Log.d("Error",e.getMessage());

                    }
                });

    }


    private void upadateAddress() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setTitle("Uploading");
        pd.show();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Address",address.getText().toString());
        databaseReference1.updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),"Address updated success",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),e.getMessage() ,Toast.LENGTH_LONG).show();
                        Log.d("Erorr",e.getMessage());

                    }
                });
    }

    private void upadateName() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setTitle("Uploading");
        pd.show();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Full Name",name.getText().toString());
        databaseReference1.updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Name updated success",Toast.LENGTH_SHORT).show();
                    pd.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),e.getMessage() ,Toast.LENGTH_LONG).show();
                        Log.d("Erorr",e.getMessage());

                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( requestCode == REQUEST && grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),"Permission granted...\nNow do it again",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Permission denied...\nNow do it again",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

}
