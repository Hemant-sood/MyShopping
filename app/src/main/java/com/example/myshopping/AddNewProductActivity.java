package com.example.myshopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddNewProductActivity extends AppCompatActivity {

    private static final int REQUEST =1021 ;
    private static final int OPEN_GALARY =2 ;
    private EditText productName, productDescription, productPrice;
    private ImageView imageView;
    private TextView category;
    private Button addProduct;
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private String productCategoryText, productNameText="", productDescriptionText="", productPriceText="", productRandomKey,date,time;
    private String downloadUri ;
    private DatabaseReference databaseReference ;
    private StorageReference storageReference;
    private String imageNameForStorage;

     StorageReference storageReference1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);
        imageView = (ImageView) findViewById(R.id.imageView6);
        productName = (EditText) findViewById(R.id.editText5);
        productDescription = (EditText) findViewById(R.id.editText6);
        productPrice = (EditText) findViewById(R.id.editText7);
        addProduct =(Button) findViewById(R.id.button4);
        category = findViewById(R.id.textView8);

        databaseReference = FirebaseDatabase.getInstance().getReference("Products/");               // for Realtime database
        storageReference = FirebaseStorage.getInstance().getReference("Products/Images/");       //  for Storage database

        Intent i = getIntent();
        productCategoryText = i.getStringExtra("Text");
        category.setText(productCategoryText);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST);
                        return;
                    }
                }

                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i,"Select pic"),OPEN_GALARY);
            }
        });


        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });

    }

    private void validateProductData() {

        productNameText = productName.getText().toString().trim();
        productDescriptionText = productDescription.getText().toString().trim();
        productPriceText = productPrice.getText().toString().trim();

        if( isFilled() ){
            if( imageUri != null){
                storeImageIntoStorageServer();
            }
            else{
                Toast.makeText(getApplicationContext(),"Please Select the image",Toast.LENGTH_LONG).show();
            }

        }
        else{
            Toast.makeText(getApplicationContext(),"Please fill all the fields",Toast.LENGTH_LONG).show();
        }

    }

    private void storeImageIntoStorageServer() {


        progressDialog = new ProgressDialog(AddNewProductActivity.this);
        progressDialog.setTitle("Product details Uploading");
        progressDialog.setMessage("Please wait while details are uploaded...");
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        date =simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("h:mm a");
        time = simpleDateFormat1.format(calendar.getTime());

        productRandomKey = date + time;
        imageNameForStorage = imageUri.getLastPathSegment()+"_"+productRandomKey ;

        storageReference1 = storageReference.child(imageNameForStorage);

        storageReference1.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUri = uri.toString();
                                storeDataIntoRealTimeDatabase();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

    }


    private void storeDataIntoRealTimeDatabase() {

        DatabaseReference databaseReference1 = databaseReference.child(imageNameForStorage);

        HashMap<String,Object> map = new HashMap<>();
        map.put("Storage Url", downloadUri);
        map.put("Category", productCategoryText);
        map.put("Name", productNameText);
        map.put("Description", productDescriptionText);
        map.put("Price", productPriceText);
        map.put("Date", date);
        map.put("TIme", time);

        Task task = databaseReference1.setValue(map);

        task.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if( task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Product details uploaded...",Toast.LENGTH_SHORT).show();
                  //  Log.d("url",downloadUri);
                    progressDialog.dismiss();
                }
                else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });

    }



    private boolean isFilled() {
        if( !TextUtils.isEmpty(productNameText) && !TextUtils.isEmpty(productDescriptionText) && !TextUtils.isEmpty(productPriceText)   )
            return true;

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == OPEN_GALARY && resultCode == RESULT_OK ){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }

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


}
