package com.example.myshopping.Models;

public class ProductCategoryModels {
    public int ProductImage;
    public String ProductText;

    public ProductCategoryModels(int productImage, String productText) {
        ProductImage = productImage;
        ProductText = productText;
    }

    public int getProductImage() {
        return ProductImage;
    }

    public void setProductImage(int productImage) {
        ProductImage = productImage;
    }

    public String getProductText() {
        return ProductText;
    }

    public void setProductText(String productText) {
        ProductText = productText;
    }
}
