package com.example.tire.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LifecycleObserver
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.SCRA.data.ItemPass
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class Repository @Inject constructor(
    @ApplicationContext
    private var context: Context,
    private var remoteSource: RemoteSource,
    private var localSource: LocalSource,
) {

    fun setLogin(login:String)
    {
        localSource.saveTokenString("login",login)
    }
    fun getLogin():String
    {
        var login = localSource.getTokenString("login");
        if (login == null)
            login = ""
        return login
    }

    fun setPassword(password:String)
    {
        localSource.saveTokenString("password",password)
    }
    fun getPassword():String
    {
        var login = localSource.getTokenString("password");
        if (login == null)
            login = ""
        return login
    }

    fun startApp(){
        localSource.saveTokenString("startApp","1")
    }

    fun startLoadImage(){
        localSource.saveTokenString("startApp","2")
    }

    fun endLoadImage(){
        startApp()
    }

    fun getStatusLoadImage():String{
        return localSource.getTokenString("startApp")
    }

    fun getSessionHandle():String{
        return localSource.getTokenString("sessionHandle")
    }
    suspend fun requestSession() {
        val result = remoteSource.requestSession()
        if (result.isNotBlank())
            localSource.saveTokenString("sessionHandle",result)

    }

    suspend fun autorisation(login: String, pass: String) {
        requestSession()
        var sessionHandle: String
        sessionHandle = getSessionHandle()

        val result  = remoteSource.autorisation(login, pass, sessionHandle)
        if (result){
            setLogin(login)
            setPassword(pass)


        }
        localSource.saveTokenBoolean("ststusAutorisation",result)
    }

    fun getStatusAutorisation(): Boolean{
        return localSource.getTokenBoolean("ststusAutorisation")
    }

    suspend fun getDataByQrCode(qrCode:String):List<ItemPass>{
        var sessionHandle: String
        sessionHandle = getSessionHandle()
        return remoteSource.getDataByQrCode(qrCode,sessionHandle)
    }

}

