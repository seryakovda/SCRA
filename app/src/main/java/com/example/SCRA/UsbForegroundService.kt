package com.example.SCRA

import android.app.*
import android.content.*
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.tire.data.Repository
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class UsbForegroundService : Service() {

    @Inject
    lateinit var repository: Repository

    private val binder = UsbServiceBinder()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var usbManager: UsbManager
    private var connection: UsbDeviceConnection? = null
    private var port: UsbSerialPort? = null

    private var isDeviceConnected = false
    private var currentDevice: UsbDevice? = null
    private var deviceMonitorJob: Job? = null

    companion object {
        private const val TAG = "UsbForegroundService"
        private const val ACTION_USB_PERMISSION = "com.example.SCRA.USB_PERMISSION"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "usb_rfid_channel"
        private const val MONITOR_INTERVAL = 2000L // 2 секунды
    }

    inner class UsbServiceBinder : Binder() {
        fun getService(): UsbForegroundService = this@UsbForegroundService
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    device?.let {
                        myLog.i(TAG, "0000001 USB ustrojstvo podklyucheno: ${device.deviceName}")
                        if (isTargetDevice(device)) {
                            myLog.i(TAG, "0000002 Eto RFID Reader, obrabatyvaem")
                            handleUsbDevice(device)
                        }
                    }
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    device?.let {
                        myLog.i(TAG, "0000003 USB ustrojstvo otklyucheno: ${device.deviceName}")
                        if (isTargetDevice(device) && currentDevice?.deviceId == device.deviceId) {
                            myLog.i(TAG, "0000004 Otklyuchen tekushchij RFID Reader")
                            disconnectDevice()
                        }
                    }
                }
                ACTION_USB_PERMISSION -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                    if (granted && device != null) {
                        myLog.i(TAG, "0000005 Razreshenie polucheno dlya ${device.deviceName}")
                        connectToDevice(device)
                    } else {
                        myLog.w(TAG, "0000006 Razreshenie otkloneno dlya ${device?.deviceName}")
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        myLog.i(TAG, "0000007 Servis sozdan")
        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager

        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            addAction(ACTION_USB_PERMISSION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(usbReceiver, filter)
        }

        // Запускаем мониторинг устройств
        startDeviceMonitoring()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        myLog.i(TAG, "0000008 Servis zapuschen")
        startForegroundIfNeeded()
        checkConnectedDevices()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    private fun startForegroundIfNeeded() {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("USB RFID Service")
            .setContentText("0000009 Servis rabotaet v fone")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        myLog.i(TAG, "0000010 Foreground service zapuschen")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "USB RFID Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "0000011 Kanal dlya USB RFID servisa"
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    /**
     * Запуск мониторинга USB устройств
     */
    private fun startDeviceMonitoring() {
        deviceMonitorJob = scope.launch {
            while (isActive) {
                try {
                    if (!isDeviceConnected) {
                        checkConnectedDevices()
                    }
                    delay(MONITOR_INTERVAL)
                } catch (e: Exception) {
                    myLog.e(TAG, "0000012 Oshibka v monitoringe ustrojstv: ${e.message}")
                    delay(MONITOR_INTERVAL)
                }
            }
        }
        myLog.i(TAG, "0000013 Monitoring USB ustrojstv zapuschen")
    }

    private fun isTargetDevice(device: UsbDevice): Boolean {
        return device.vendorId == 1027 && device.productId == 24577
    }

    private fun checkConnectedDevices() {
        scope.launch {
            try {
                val allDevices = usbManager.deviceList.values.toList()
                if (allDevices.isNotEmpty()) {
                    myLog.i(TAG, "0000014 Naideno vsego USB ustrojstv: ${allDevices.size}")

                    for (device in allDevices) {
                        if (isTargetDevice(device)) {
                            myLog.i(TAG, "0000015 Naiden RFID Reader: ${device.deviceName}")
                            handleUsbDevice(device)
                            break // Обрабатываем только первое найденное устройство
                        }
                    }
                }
            } catch (e: Exception) {
                myLog.e(TAG, "0000016 Oshibka pri poiske USB ustrojstv: ${e.message}")
            }
        }
    }

    /**
     * Универсальная обработка USB устройства
     */
    private fun handleUsbDevice(device: UsbDevice) {
        scope.launch {
            if (isDeviceConnected) {
                myLog.i(TAG, "0000017 Ustrojstvo uzhe podklyucheno, propuskaem")
                return@launch
            }

            if (usbManager.hasPermission(device)) {
                myLog.i(TAG, "0000018 Est' razreshenie, podklyuchaemsya")
                connectToDevice(device)
            } else {
                myLog.i(TAG, "0000019 Net razresheniya, zaprashivaem")
                requestDevicePermission(device)
            }
        }
    }

    private fun requestDevicePermission(device: UsbDevice) {
        scope.launch {
            delay(500) // Короткая задержка для стабилизации

            if (!usbManager.hasPermission(device)) {
                val pi = PendingIntent.getBroadcast(
                    this@UsbForegroundService,
                    device.deviceId,
                    Intent(ACTION_USB_PERMISSION),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                usbManager.requestPermission(device, pi)
                myLog.i(TAG, "0000020 Zaprosheno razreshenie: ${device.deviceName}")
            }
        }
    }

    private fun connectToDevice(device: UsbDevice) {
        try {
            val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
            val driver = drivers.firstOrNull {
                it.device.deviceId == device.deviceId
            } ?: run {
                myLog.e(TAG, "0000021 Ne naiden driver dlya: ${device.deviceName}")
                return
            }

            connection = usbManager.openDevice(driver.device)
            if (connection == null) {
                myLog.e(TAG, "0000022 Ne udalos' otkryt' ustrojstvo")
                return
            }

            port = driver.ports.firstOrNull()
            if (port == null) {
                myLog.e(TAG, "0000023 Net portov u drajvera")
                connection?.close()
                return
            }

            port!!.open(connection)
            port!!.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

            isDeviceConnected = true
            currentDevice = device
            myLog.i(TAG, "0000024 Uspeshno podklyucheno: ${device.deviceName}")
            startReadingLoop()

        } catch (e: Exception) {
            myLog.e(TAG, "0000025 Oshibka podklyucheniya: ${e.message}")
            disconnectDevice()
        }
    }

    private fun disconnectDevice() {
        try {
            port?.close()
            connection?.close()
            port = null
            connection = null
            isDeviceConnected = false
            currentDevice = null
            myLog.i(TAG, "0000026 Soedinenie zakryto")
        } catch (e: Exception) {
            myLog.e(TAG, "0000027 Oshibka zakrytiya soedineniya: ${e.message}")
        }
    }

    private val buffer = StringBuilder()
    private fun startReadingLoop() {
        scope.launch {
            val buf = ByteArray(1024)
            while (isActive && isDeviceConnected) {
                try {
                    delay(50);// лёгкая задержка при попытке подлучьть данные  из буфера
                    val len = port?.read(buf, 100) ?: 0
                    if (len > 0) {
                        val dataBytes = buf.copyOf(len)
                        val chunk  = dataBytes.toHex()
                        if (chunk.startsWith("23") && buffer.length > 2) {
                            val message = buffer.toString()
                            buffer.clear()
                            myLog.e(TAG, "====== ${message} ======")
                            if (message.length == 28) {
                                    repository.sendBinaryData(message,"FR_Code")
                                    repository.setValueCode(message)
                            }
                            myLog.e(TAG, "!!!!! ${message} !!!!!")
                        }
                        buffer.append(chunk)
                    }
                } catch (io: IOException) {
                    myLog.e(TAG, "0000028 Oshibka chteniya: ${io.message}")
                    disconnectDevice()
                    break
                } catch (e: Exception) {
                    myLog.e(TAG, "0000029 Obshaya oshibka chteniya: ${e.message}")
                    delay(1000)
                }
            }
        }
    }

    fun sendCommand(command: String) {
        scope.launch {
            try {
                port?.write(command.toByteArray(), 1000)
                myLog.i(TAG, "0000030 Komanda otpravlena: $command")
            } catch (e: Exception) {
                myLog.e(TAG, "0000031 Oshibka otpravki komandy: ${e.message}")
            }
        }
    }

    fun isConnected(): Boolean = isDeviceConnected && port != null

    override fun onDestroy() {
        super.onDestroy()
        myLog.i(TAG, "0000032 Servis unichtozhaetsya")
        try {
            unregisterReceiver(usbReceiver)
        } catch (e: Exception) {
            myLog.e(TAG, "0000033 Oshibka pri otmene registracii receiver: ${e.message}")
        }
        deviceMonitorJob?.cancel()
        scope.cancel()
        disconnectDevice()
    }
}

fun ByteArray.toHex(): String = joinToString("") { "%02X".format(it) }