package com.example.SCRA

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import com.example.SCRA.navigation.AppNavHost
import com.example.SCRA.ui.theme.SCRATheme
import com.example.tire.data.Repository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var usbService: UsbForegroundService? = null
    private var isBound = false

    // ServiceConnection для связи с сервисом
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as UsbForegroundService.UsbServiceBinder
            usbService = binder.getService()
            isBound = true
            Log.i("MainActivity", "USB service ON")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            usbService = null
            isBound = false
            Log.i("MainActivity", "USB service off")
        }
    }

    // BroadcastReceiver для получения данных из сервиса
    private val usbDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "USB_DATA_RECEIVED" -> {
                    val data = intent.getStringExtra("data")
                    val length = intent.getIntExtra("length", 0)
                    Log.i("MainActivity", "Получены данные от RFID: $data (длина: $length)")
                    // Здесь можно обработать данные - обновить UI, отправить на сервер и т.д.
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Регистрируем BroadcastReceiver для получения данных от USB сервиса
        // Исправление для Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                usbDataReceiver,
                IntentFilter("USB_DATA_RECEIVED"),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(usbDataReceiver, IntentFilter("USB_DATA_RECEIVED"))
        }
        setContent {
            SCRATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }

        // Запускаем USB сервис при создании Activity
        startUsbService()
    }

    override fun onResume() {
        super.onResume()
        initLocation()
        initCamera()
    }

    override fun onPause() {
        super.onPause()
        // Не останавливаем сервис здесь, чтобы он работал в фоне
    }

    override fun onDestroy() {
        super.onDestroy()
        // Отключаемся от сервиса
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        unregisterReceiver(usbDataReceiver)
    }


    private fun startUsbService() {
        try {
            val serviceIntent = Intent(this, UsbForegroundService::class.java)

            // Запускаем сервис
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }

            // Привязываемся к сервису для взаимодействия
           // bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            Log.i("MainActivity", "2200001 USB сервис запущен")
        } catch (e: Exception) {
            Log.e("MainActivity", "2200002 Ошибка запуска USB сервиса: ${e.message}")
        }
    }

    // Методы для взаимодействия с сервисом из других частей приложения
    fun sendCommandToUsb(command: String) {
        if (isBound) {
            usbService?.sendCommand(command)
            Log.i("MainActivity", "2200003 Команда отправлена в USB сервис: $command")
        } else {
            Log.w("MainActivity", "2200004 Сервис не подключен, команда не отправлена: $command")
        }
    }

    fun isUsbConnected(): Boolean {
        return isBound && usbService?.isConnected() == true
    }

    // Остальные методы для разрешений остаются без изменений
    private val permissionId = 2

    @SuppressLint("MissingPermission", "SetTextI18n")
    fun initLocation() {
        if (checkPermissions()) {
            // Разрешения есть, можно работать с локацией
            Log.i("MainActivity", "2200005 Разрешения локации получены")
        } else {
            requestPermissions()
            Log.i("MainActivity", "2200006 Запрошены разрешения локации")
        }
    }

    fun initCamera(){
        if (checkPermissionsCamera()) {
            // Разрешения есть, можно работать с камерой
            Log.i("MainActivity", "2200007 Разрешения камеры получены")
        } else {
            requestPermissionsCamera()
            Log.i("MainActivity", "2200008 Запрошены разрешения камеры")
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    private fun checkPermissionsCamera(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissionsCamera() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initLocation()
                Log.i("MainActivity", "2200009 Разрешения получены после запроса")
            } else {
                Log.w("MainActivity", "2200010 Разрешения отклонены пользователем")
            }
        }
    }
}


