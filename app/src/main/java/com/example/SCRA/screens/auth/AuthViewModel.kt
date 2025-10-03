package com.example.SCRA.screens.auth

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

import com.example.tire.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private var repository: Repository,
    private var context: Context
): ViewModel() {

    val _authState: MutableLiveData<NavHostViewModel.AuthState> = MutableLiveData(NavHostViewModel.AuthState.BALLAST)

    fun getLogin():String
    {
        return repository.getLogin()
    }

    fun getPassword():String
    {
        return repository.getPassword();
    }


    fun autorisation(login: String, pass: String){
        val path = context.getFilesDir()
        Log.i("MyMSG","autorisation " + path.toString())
        viewModelScope.launch(Dispatchers.IO) {
             repository.autorisation(login, pass)
             if(repository.getStatusAutorisation()) {
                 //navHostViewModel._authState.postValue(NavHostViewModel.AuthState.SUCCESS)

                 _authState.postValue(NavHostViewModel.AuthState.SUCCESS)

             }
            else
                 //navHostViewModel._authState.postValue(NavHostViewModel.AuthState.FAIL)
                 _authState.postValue(NavHostViewModel.AuthState.FAIL)
        }
    }

    fun upload() {

        viewModelScope.launch(Dispatchers.IO) {
           // repository.uploadAllImageFromLocalTable()
        }
    }

    fun BtnOK() {
        _authState.postValue(NavHostViewModel.AuthState.AUTH)
        //_authState.postValue(NavHostViewModel.AuthState.SUCCESS)
       // navHostViewModel._authState.value = NavHostViewModel.AuthState.AUTH
    }
}