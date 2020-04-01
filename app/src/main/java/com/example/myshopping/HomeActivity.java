package com.example.myshopping;

import android.app.usage.StorageStatsManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myshopping.Adapters.ProductCategoryAdapters;
import com.example.myshopping.Adapters.ProductPropertiesAdapter;
import com.example.myshopping.Models.ProductProperties;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseAuth mAuth;

    private TextView userEmailId,uerFullName;
    private CircleImageView userPhoto;
    private DatabaseReference user_details;
    private RecyclerView recyclerView;
    private ArrayList<ProductProperties> list;
    private DatabaseReference databaseReference;
    private ProductPropertiesAdapter productPropertiesAdapter;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        list = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user_details = FirebaseDatabase.getInstance().getReference("User");
        databaseReference = FirebaseDatabase.getInstance().getReference("Products/");
        recyclerView = findViewById(R.id.recylcerivew);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

        View view_header = navigationView.getHeaderView(0);
        uerFullName = view_header.findViewById(R.id.name);
        userEmailId = view_header.findViewById(R.id.email);
        userPhoto = view_header.findViewById(R.id.profile_image);
        setUserDetailsOnNav();



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        getAllImages();


    }
 
    @Override
    protected void onResume() {
        super.onResume();
        setUserDetailsOnNav();
    }

    private void getAllImages() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    ProductProperties productProperties = dataSnapshot1.getValue(ProductProperties.class);
                    list.add(productProperties);

                }
                productPropertiesAdapter = new ProductPropertiesAdapter(list,getApplicationContext());
                recyclerView.setAdapter(productPropertiesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage().toString(),Toast.LENGTH_SHORT).show();
                    Log.d("Error", databaseError.getMessage());
                    Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUserDetailsOnNav() {

        //userPhoto


        FirebaseUser currentUser= mAuth.getCurrentUser();
        String email = currentUser.getEmail();

        userEmailId.setText(email.replace('_','.'));

        DatabaseReference databaseReference = user_details.child(email.replace('.','_'));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Full Name").getValue().toString();
                uerFullName.setText(name);
                Picasso.get().load(dataSnapshot.child("Url").getValue().toString()).placeholder(R.drawable.user).into(userPhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if( id == R.id.nav_cart){

        }
        else if( id == R.id.nav_order){

        }
        else if( id == R.id.nav_category){

        }
        else if( id == R.id.nav_settings){
            Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
            i.putExtra("Person","User");
            startActivity(i);
        }
        else if( id == R.id.nav_logout){
            mAuth.signOut();
            Toast.makeText(getApplicationContext(),"Logged out success",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(i);
        }

        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
