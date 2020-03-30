package com.example.myshopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.myshopping.Adapters.ProductCategoryAdapters;
import com.example.myshopping.Models.ProductCategoryModels;

import java.util.ArrayList;

public class ProductCategoryActivity extends AppCompatActivity {

    private ArrayList<ProductCategoryModels> list;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_category);
        recyclerView = findViewById(R.id.rec);
        list = new ArrayList<>();

        int [] productImages = {
                R.drawable.tshirt,
                R.drawable.shirt,
                R.drawable.suit,
                R.drawable.dress,
                R.drawable.jeans,
                R.drawable.leggins,
                R.drawable.cap,
                R.drawable.sunglasses,
                R.drawable.shoes,
                R.drawable.ladiesshoe,
                R.drawable.laptop,
                R.drawable.mobile
        };

        String [] productNames = {
                "T-shirts","Shirts","Suits","Dresses","Jeans","Leggins","Cap","Sunglasses","Shoes","Ladies Shoes","Laptops","Mobiles"
        };

        for( int i = 0 ; i< productImages.length ; i++){
            list.add(new ProductCategoryModels(productImages[i], productNames[i]));
        }


        ProductCategoryAdapters productCategoryAdapters = new ProductCategoryAdapters(list,getApplicationContext());
        recyclerView.setAdapter(productCategoryAdapters);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);

    }
}
