package com.tayyab.mobileapp.interfaces

import com.tayyab.mobileapp.models.Product

interface OnSpeakClickListener {
    fun onSpeakClick( obj: Product,shift:Boolean)
}