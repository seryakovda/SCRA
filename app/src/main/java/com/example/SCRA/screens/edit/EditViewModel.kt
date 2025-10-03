package com.example.SCRA.screens.edit

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.PendingIntentCompat.getActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.SCRA.NavHostViewModel
import com.example.SCRA.data.ItemPass

import com.example.tire.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class EditViewModel @Inject constructor(
    private var repository: Repository,
    private var context: Context
): ViewModel() {

    val dataByQrCode: MutableLiveData<List<ItemPass>?> = MutableLiveData()

    fun getDataByQrCode(qrCode:String){
        dataByQrCode.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            dataByQrCode.postValue(repository.getDataByQrCode(qrCode))
        }
    }

}