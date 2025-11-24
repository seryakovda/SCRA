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
import com.example.SCRA.data.ScraDataStore
import com.example.SCRA.data.ScraList
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
    private var tireDataStore: ScraDataStore
) {
    suspend fun testConnection(): Boolean
    {
        return remoteSource.testConnection()
    }

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

    fun setIpServer(value:String)
    {
        localSource.saveTokenString("IpServer",value)
    }

    fun setIdDoor(value:String)
    {
        localSource.saveTokenString("IdDoor",value)
    }

    fun getIpServer():String
    {
        var value = localSource.getTokenString("IpServer");
        if (value == null)
            value = ""
        return value
    }

    fun getIdDoor():String
    {
        var value = localSource.getTokenString("IdDoor");
        if (value == null)
            value = ""
        return value
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

    suspend fun getDataByQrCode(qrCode:String,typeCode:String):List<ItemPass>{
        var sessionHandle: String
        sessionHandle = getSessionHandle()
        var inOut: String
        inOut = getStateInOut()
        return remoteSource.getDataByQrCode(qrCode,typeCode,inOut,sessionHandle)
    }

    suspend fun sendBinaryData(binaryData:String,typeCode:String){
        var sessionHandle: String
        sessionHandle = getSessionHandle()
        remoteSource.sendBinaryData(binaryData,typeCode,sessionHandle)
    }


    suspend fun setValueCode(value:String){
        tireDataStore.setValueCode(value)
    }
    suspend fun getValueCode(): Flow<ScraList?> {
       return tireDataStore.getValueCode()
    }


    suspend fun setValueTypeCode(value:String){
        tireDataStore.setValueTypeCode(value)
    }

    fun setStateInOut(value: Boolean){
        if (value)
            localSource.saveTokenString("StateInOut","1")
        else
            localSource.saveTokenString("StateInOut","0")
        localSource.getTokenString("StateInOut")
    }
    fun getStateInOut():String{
        var ret = localSource.getTokenString("StateInOut");
        if (ret == "") {
            setStateInOut(true)
            ret = "1";
        }
        return ret;
    }
}

