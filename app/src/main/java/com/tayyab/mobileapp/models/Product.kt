package com.tayyab.mobileapp.models

data class Product(
    val pr_id: Int,
    val pr_name: String,
    val pr_price: Int,
    val pr_desc: String,
    var pr_Picture: String,
    val category: Category?,
    val categoryForeignKey: Int,
)