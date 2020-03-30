package com.example.myshopping.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myshopping.AddNewProductActivity;
import com.example.myshopping.Models.ProductCategoryModels;
import com.example.myshopping.R;

import java.util.ArrayList;

public class ProductCategoryAdapters extends RecyclerView.Adapter<ProductCategoryAdapters.ViewHolder> {

    ArrayList<ProductCategoryModels> list;
    Context context;

    public ProductCategoryAdapters(ArrayList<ProductCategoryModels> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_recycler_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final ProductCategoryModels model = list.get(position);

        holder.imageView.setImageResource(model.getProductImage());
        holder.textView.setText(model.getProductText());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AddNewProductActivity.class);
                i.putExtra("ImageId",model.getProductImage());
                i.putExtra("Text",model.getProductText());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView2);
            textView = itemView.findViewById(R.id.textView7);
        }
    }

}
