package com.tayyab.mobileapp.interfaces

import com.tayyab.mobileapp.models.CartDetail

interface OnCartItemClickListener {
    fun onItemClick( obj: CartDetail)
}