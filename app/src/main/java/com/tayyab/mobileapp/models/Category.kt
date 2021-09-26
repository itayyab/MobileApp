package com.tayyab.mobileapp.models

data class Category(
    val cat_id: Int,
    val cat_name: String,
    val products: List<Product>?
)