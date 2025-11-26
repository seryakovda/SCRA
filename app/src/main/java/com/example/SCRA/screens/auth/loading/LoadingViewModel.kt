package com.example.SCRA.screens.auth.loading

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.SCRA.NavHostViewModel
import com.example.SCRA.navigation.Destination

import com.example.tire.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@Immutable
sealed interface LoadingState {
    @Immutable
    data object Sucsess: LoadingState
    @Immutable
    data object Fail: LoadingState
    @Immutable
    data object Loading: LoadingState
}
@HiltViewModel
class LoadingViewModel @Inject constructor(
    private var repository: Repository,
    //private var context: Context
): ViewModel() {

//    var _authState: MutableLiveData<NavHostViewModel.AuthState> = MutableLiveData(NavHostViewModel.AuthState.LOADING)
    private var _loadingState: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.Loading)
    var loadingState: LiveData<LoadingState> = _loadingState
    fun testConnection() {
        Log.i("testConnection", "Start")
        viewModelScope.launch(Dispatchers.IO) {
            var isConn = repository.testConnection()
            if (isConn) {
                _loadingState.postValue(LoadingState.Sucsess)
                Log.i("testConnection", "True")
             //   _authState = MutableLiveData(NavHostViewModel.AuthState.EDIT)
            } else {
                _loadingState.postValue(LoadingState.Fail)
                Log.i("testConnection", "false")
            //    _authState = MutableLiveData(NavHostViewModel.AuthState.AUTH)
            }
        }
    }

}