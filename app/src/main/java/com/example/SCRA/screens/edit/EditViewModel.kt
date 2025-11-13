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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class EditViewModel @Inject constructor(
    private var repository: Repository,
    private var context: Context
): ViewModel() {

   // val scraList: MutableLiveData<List<ScraList>> = MutableLiveData()

    val dataByQrCode: MutableLiveData<List<ItemPass>?> = MutableLiveData()
    private var job: Job? = null
//    val f = listOf(1, 2, 3).asFlow()
//
//    val qr = f.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = 0
//    )
    fun getDataByQrCode(qrCode:String){
        dataByQrCode.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendBinaryData(qrCode,"QR_Code") // регистрация прохода не взирая на отображение
            dataByQrCode.postValue(repository.getDataByQrCode(qrCode,"QR_Code"))

            playSound()
        }
    }

    fun getDataByQrCode2(){
        dataByQrCode.postValue(null)
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            repository.getValueCode().collect { scraList ->
                myLog.e("UsbForegroundService", scraList.toString())
                scraList?.let {
                    dataByQrCode.postValue(repository.getDataByQrCode(it.code,"FR_Code"))
                    playSound()
                }
            }
        }
    }

    private var mediaPlayer: MediaPlayer? = null

    fun playSound() {
        // Создаем MediaPlayer с передачей контекста
        mediaPlayer = MediaPlayer.create(context, R.raw.payment_succes)

        // Проверяем успешность создания
        mediaPlayer?.let {
            it.setOnCompletionListener {
                // Освобождаем ресурсы после завершения
                releaseMediaPlayer()
            }
            it.start()
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.let {
            it.release()
            mediaPlayer = null
        }
    }

    // Освобождаем ресурсы при уничтожении ViewModel
    override fun onCleared() {
        super.onCleared()
        releaseMediaPlayer()
    }
}