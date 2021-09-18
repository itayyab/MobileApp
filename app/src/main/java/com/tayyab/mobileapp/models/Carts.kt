package com.tayyab.mobileapp.models

data class Carts(
    val cartDetails: List<CartDetail>,
    val cart_id: Int,
    val status: String,
    val totalAmount: Int,
    val totalQty: Int,
    val userID: String
)