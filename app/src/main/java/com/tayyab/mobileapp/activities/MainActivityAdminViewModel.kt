package com.tayyab.mobileapp.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tayyab.mobileapp.models.DialogModel

class MainActivityAdminViewModel(application: Application) : AndroidViewModel(application) {
    var data: MutableLiveData<Int>? = MutableLiveData()
    private var dailog: MutableLiveData<DialogModel>? = MutableLiveData()

    fun getDialogStart() = dailog!!

    fun getDialogs(product: Boolean, data: Any) {
        val dia =
            DialogModel(product, data)
        dailog!!.value = dia
    }


    var dataupdated: MutableLiveData<Boolean>? = MutableLiveData()

    fun getDataUpdated() {
        dataupdated!!.value = true
    }
}