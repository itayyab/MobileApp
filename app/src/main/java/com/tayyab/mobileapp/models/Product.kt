package com.tayyab.mobileapp.models

data class Product(
    val category: Category,
    val categoryForeignKey: Int,
    val pr_Picture: String,
    val pr_desc: String,
    val pr_id: Int,
    val pr_name: String,
    val pr_price: Int
)