package com.example.myshopping.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myshopping.Models.ProductProperties;
import com.example.myshopping.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductPropertiesAdapter extends RecyclerView.Adapter<ProductPropertiesAdapter.ViewHolder> {

    ArrayList<ProductProperties> list;
    Context context;

    public ProductPropertiesAdapter(ArrayList<ProductProperties> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.all_products,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ProductProperties productProperties = list.get(position);

        holder.timeVal.setText(productProperties.getTime());
        holder.desVal.setText(productProperties.getDescription());
        holder.nameVale.setText(productProperties.getName());
        holder.priceVal.setText(productProperties.getPrice());
        holder.dateValue.setText(productProperties.getDate());
        holder.categoryVal.setText(productProperties.getCategory());

        Picasso.get().load(productProperties.getUrl()).placeholder(R.drawable.image) .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView nameText,nameVale;
        private TextView categoryText,categoryVal;
        private TextView desText,desVal;
        private TextView priceText,priceVal;
        private TextView dateText,dateValue;
        private TextView timeText,timeVal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageUrl);
            nameText = itemView.findViewById(R.id.nameText);
            nameVale = itemView.findViewById(R.id.nameVal);
            desText = itemView.findViewById(R.id.nameDes);
            desVal = itemView.findViewById(R.id.desValue);
            categoryText = itemView.findViewById(R.id.nameCat);
            categoryVal = itemView.findViewById(R.id.catValue);
            priceText = itemView.findViewById(R.id.nameVal);
            priceVal = itemView.findViewById(R.id.priceValue);
            dateText = itemView.findViewById(R.id.dateText);
            dateValue = itemView.findViewById(R.id.dateValue);
            timeText = itemView.findViewById(R.id.timeText);
            timeVal = itemView.findViewById(R.id.timeValue);

        }

    }

}
