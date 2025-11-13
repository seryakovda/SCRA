package com.example.SCRA

import android.app.*
import android.content.*
import android.content.pm.ServiceInfo
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

    companion object {
        private const val TAG = "UsbForegroundService"
        private const val ACTION_USB_PERMISSION = "com.example.SCRA.USB_PERMISSION"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "usb_rfid_channel"
    }

    // Binder для связи с Activity
    inner class UsbServiceBinder : Binder() {
        fun getService(): UsbForegroundService = this@UsbForegroundService
    }

    private val permissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_USB_PERMISSION) {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                if (granted && device != null) {
                    myLog.i(TAG, "0000001 Разрешение получено для ${device.deviceName}")
                    openDevice(device)
                } else {
                    myLog.w(TAG, "0000002 Разрешение отклонено для ${device?.deviceName}")
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        myLog.i(TAG, "0000003 Сервис создан")
        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                permissionReceiver,
                IntentFilter(ACTION_USB_PERMISSION),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(permissionReceiver, IntentFilter(ACTION_USB_PERMISSION))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        myLog.i(TAG, "0000004 Сервис запущен")
        startForegroundIfNeeded()
        discoverAndRequest()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    private fun startForegroundIfNeeded() {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("USB RFID Service")
            .setContentText("0000005 Сервис работает в фоне")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .build()

        // Упрощенный подход - используем dataSync для всех версий
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // FOREGROUND_SERVICE_TYPE_DATA_SYNC = 1
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            // Для Android 8-9
            startForeground(NOTIFICATION_ID, notification)
        }

        myLog.i(TAG, "0000006 Foreground service запущен")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "USB RFID Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "0000007  Канал для USB RFID сервиса"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Поиск драйверов через UsbSerialProber и запрос разрешения
     */
    private fun discoverAndRequest() {
        scope.launch {
            try {
                val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
                if (drivers.isEmpty()) {
                    myLog.i(TAG, "0000009 USB-ридеры не найдены")
                    return@launch
                }

                val driver = drivers[0]
                val device = driver.device

                if (!usbManager.hasPermission(device)) {
                    val pi = PendingIntent.getBroadcast(
                        this@UsbForegroundService,
                        0,
                        Intent(ACTION_USB_PERMISSION),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                    usbManager.requestPermission(device, pi)
                    myLog.i(TAG, "0000010 Запрошено разрешение на ${device.deviceName}")
                } else {
                    openDevice(device)
                }
            } catch (e: Exception) {
                myLog.e(TAG, "0000011 Ошибка при поиске USB устройств: ${e.message}")
            }
        }
    }

    /**
     * Открытие устройства и настройка COM-порта
     */
    private fun openDevice(device: UsbDevice) {
        try {
            val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
            val driver = drivers.firstOrNull { it.device.deviceId == device.deviceId } ?: return

            connection = usbManager.openDevice(driver.device)
            if (connection == null) {
                myLog.e(TAG, "0000012 Не удалось открыть устройство")
                return
            }

            port = driver.ports.firstOrNull()
            if (port == null) {
                myLog.e(TAG, "0000013 У драйвера нет портов")
                return
            }

            port!!.open(connection)
            port!!.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

            myLog.i(TAG, "0000014 Порт открыт, запускаем чтение")
            startReadingLoop()
        } catch (e: Exception) {
            myLog.e(TAG, "0000015 Ошибка открытия: ${e.message}")
        }
    }

    private val buffer = StringBuilder()
    /**
     * Бесконечный цикл чтения данных с устройства
     */
    private fun startReadingLoop() {
        scope.launch {
            val buf = ByteArray(1024)
            var mem_code = "123";
            while (isActive) {
                try {
                    val len = port?.read(buf, 100) ?: 0
                    if (len > 0) {
                        val dataBytes = buf.copyOf(len)
                        val chunk  = dataBytes.toHex()
                        if (chunk.startsWith("23") && buffer.length > 2) {
                            // закончили предыдущее
                            val message = buffer.toString()
                            buffer.clear()
                            myLog.e(TAG, "====== ${message} ======")
                            if (message.length == 28){ // отправлять только длинну кода карты
                                if (mem_code != message){ // не отправлять один и тотже код многократно
                                    repository.sendBinaryData(message,"FR_Code") // регистрация прохода не взирая на отображение
                                    repository.setValueCode(message)
                                    mem_code = message
                                }
                            }
                            myLog.e(TAG, "!!!!! ${message} !!!!!")
                        }
                        buffer.append(chunk)

                    }
                } catch (io: IOException) {
                    myLog.e(TAG, "0000017 Ошибка чтения: ${io.message}")
                    discoverAndRequest()
                    delay(1000)
                } catch (e: Exception) {
                    myLog.e(TAG, "0000018 Общая ошибка в цикле чтения: ${e.message}")
                    delay(1000)
                }
            }
        }
    }

    /**
     * Методы для взаимодействия с Activity
     */
    fun sendCommand(command: String) {
        scope.launch {
            try {
                port?.write(command.toByteArray(), 1000)
                myLog.i(TAG, "0000019 Команда отправлена: $command")
            } catch (e: Exception) {
                myLog.e(TAG, "0000020 Ошибка отправки команды: ${e.message}")
            }
        }
    }

    fun isConnected(): Boolean = port != null

    override fun onDestroy() {
        super.onDestroy()
        myLog.i(TAG, "0000021 Сервис уничтожается")

        try {
            unregisterReceiver(permissionReceiver)
        } catch (e: Exception) {
            myLog.e(TAG, "0000022 Ошибка при отмене регистрации receiver: ${e.message}")
        }

        scope.cancel()

        try {
            port?.close()
        } catch (e: Exception) {
            myLog.e(TAG, "0000023 Ошибка закрытия порта: ${e.message}")
        }

        connection?.close()
    }
}

// Расширение для преобразования байтов в HEX
fun ByteArray.toHex(): String = joinToString("") { "%02X".format(it) }