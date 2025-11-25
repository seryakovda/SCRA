package com.example.SCRA.screens.edit

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.ActivityCompat
import androidx.core.app.PendingIntentCompat.getActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.SCRA.NavHostViewModel
import com.example.SCRA.R
import com.example.SCRA.UsbForegroundService
import com.example.SCRA.data.ItemPass
import com.example.SCRA.data.ScraList
import com.example.SCRA.myLog

import com.example.tire.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class EditViewModel @Inject constructor(
    private val repository: Repository,
    private val context: Context
) : ViewModel() {

    val dataByQrCode: MutableLiveData<List<ItemPass>?> = MutableLiveData()
    private var job: Job? = null
    private var isListening = false

    fun getDataByQrCode(qrCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendBinaryData(qrCode, "QR_Code")
            if (repository.getDataByQrCode(qrCode, "QR_Code")) {
                dataByQrCode.postValue(repository.restoreDataByCode())
                playSound()
            }
        }
    }

    fun startListeningUsbCodes() {
        if (isListening) return  // Слушатель уже запущен
        isListening = true

        job = viewModelScope.launch(Dispatchers.IO) {
            repository.getValueCode()
                .distinctUntilChanged()
                .collect { value ->
                    value?.let {
                        if (repository.getDataByQrCode(it, "FR_Code")) {
                            dataByQrCode.postValue(repository.restoreDataByCode())
                            playSound()
                        }
                    }
                }
        }
    }

    private var mediaPlayer: MediaPlayer? = null

    fun playSound() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, R.raw.payment_succes).apply {
            setOnCompletionListener { releaseMediaPlayer() }
            start()
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        releaseMediaPlayer()
    }

    fun setStateInOut(inOut: Boolean) {
        repository.setStateInOut(inOut)
    }
}