package com.tayyab.mobileapp.models

data class CartDetail(
    val cD_Pr_Amnt: Int,
    val cD_Pr_Qty: Int,
    val cD_Pr_id: Int,
    val cD_Pr_price: Int,
    val cD_id: Int,
    val cart: Carts?,
    val cartForeignKey: Int,
    val product: Product?,
    val productForeignKey: Int
)