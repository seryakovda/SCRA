package com.example.SCRA

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.SCRA.screens.auth.AuthViewModel
import com.example.SCRA.screens.edit.mainEditScreen
import com.example.SCRA.ui.theme.SCRATheme
import com.example.tire.screens.auth.authError.AuthScreenError
import com.example.tire.screens.auth.enterLogin.AuthScreen2
import dagger.hilt.android.AndroidEntryPoint

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
            Log.i("MainActivity", "USB сервис подключен")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            usbService = null
            isBound = false
            Log.i("MainActivity", "USB сервис отключен")
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

    override fun onResume() {
        super.onResume()
        initLocation()
        initCamera()
        startUsbService() // Запускаем сервис при возобновлении Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Регистрируем BroadcastReceiver для получения данных от USB сервиса
        registerReceiver(usbDataReceiver, IntentFilter("USB_DATA_RECEIVED"))

        setContent {
            SCRATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TireNavHost()
                }
            }
        }
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
        val serviceIntent = Intent(this, UsbForegroundService::class.java)

        // Запускаем сервис
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // Привязываемся к сервису для взаимодействия
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    // Методы для взаимодействия с сервисом из других частей приложения
    fun sendCommandToUsb(command: String) {
        if (isBound) {
            usbService?.sendCommand(command)
        } else {
            Log.w("MainActivity", "Сервис не подключен, команда не отправлена: $command")
        }
    }

    fun isUsbConnected(): Boolean {
        return isBound && usbService?.isConnected() == true
    }

    // Остальные методы для разрешений остаются без изменений
    //private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    @SuppressLint("MissingPermission", "SetTextI18n")
    fun initLocation() {
        if (checkPermissions()) {
            // Разрешения есть, можно работать с локацией
        } else {
            requestPermissions()
        }
    }

    fun initCamera(){
        if (checkPermissionsCamera()) {
            // Разрешения есть, можно работать с камерой
        } else {
            requestPermissionsCamera()
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
            }
        }
    }
}

// Классы для навигации остаются без изменений
sealed class Destination(val route: String) {
    object AuthScreen: Destination("AuthScreen")
    object AuthScreenError: Destination("AuthScreenError")
    object ScreenMainJob: Destination("ScreenMainJob")
    object ScreenEdit: Destination("ScreenEdit")
}

@SuppressLint("RestrictedApi")
@Composable
fun TireNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destination.AuthScreen.route
) {
    val navHostViewModel = hiltViewModel<NavHostViewModel>()
    val viewModel = hiltViewModel<AuthViewModel>()

    val state = navHostViewModel.authState.observeAsState()
    when (state.value) {
        NavHostViewModel.AuthState.FAIL ->                  navController.navigate(Destination.AuthScreenError.route)
        NavHostViewModel.AuthState.AUTH ->                  navController.popBackStack()
        NavHostViewModel.AuthState.SUCCESS ->               navController.navigate(Destination.ScreenMainJob.route)
        NavHostViewModel.AuthState.EDIT ->                  navController.navigate(Destination.ScreenEdit.route)
        else -> {}
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.AuthScreen.route){
            AuthScreen2(navController = navController)
        }

        composable(Destination.AuthScreenError.route) {
            AuthScreenError(
                navController = navController
            )
        }

        composable(Destination.ScreenEdit.route) {
            mainEditScreen(
                navController = navController
            )
        }
    }
}