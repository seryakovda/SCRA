package com.example.SCRA

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class NavHostViewModel @Inject constructor(): ViewModel() {
    val _authState: MutableLiveData<AuthState> = MutableLiveData(AuthState.AUTH)
    val authState: LiveData<AuthState> get() = _authState

    enum class AuthState{
                        BALLAST,
        FAIL, SUCCESS, AUTH, EDIT,
        EDIT_BRAND,
        EDIT_STANDARD_SIZE,
        EDIT_MODEL,
        EDIT_DIVISION,
        EDIT_CHASSIS_NUMBER,
        VIEW_REMOTE_IMAGE,
        ADD_EVENT
        ;
    }
}
