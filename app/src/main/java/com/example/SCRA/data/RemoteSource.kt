package com.example.tire.data
import android.util.Log
import com.example.SCRA.ConstApp.keyAPI
import com.example.SCRA.ConstApp.urlRemoteServer
import com.example.SCRA.data.ItemPass

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.date
import io.ktor.http.isSuccess
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import java.io.File
import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.UUID
import javax.inject.Inject


@Serializable
data class SessionHandle (val state: String, val sessionHandle: String)

@Serializable
data class AutorisationResult(val state: String)

@Serializable
data class ImageList (
    var id_image:String
)
@Serializable
data class TireEvents (
    var id:Int,
    var name:String
)
data class ControlHttpResponse(
    var error:Boolean,
    var httpResponse:HttpResponse?
)
class RemoteSource @Inject constructor(private val client: HttpClient) {

//    private suspend inline fun <reified T> obrabotka(requestTxt: String, default: T): T {
//        val requestTxt1 = requestTxt.replace(" ","%20")
//        return try {
//            val response = client.request(requestTxt1)
//            if (response.status.isSuccess()) response.body<T>() else default
//        } catch (e: Exception) {
//            default
//        }
//    }

////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun myRequest(requestTxt1:String):HttpResponse{
        var requestTxt = requestTxt1.replace(" ","%20")
            requestTxt = requestTxt.replace("/","%2F")
            requestTxt = requestTxt.replace(":","%3A")
            requestTxt = requestTxt.replace(".","%2E")
            
            requestTxt = "$urlRemoteServer?" + requestTxt
    
            //Log.i("MyMSG",requestTxt)
        var retStatus = ControlHttpResponse(httpResponse = null, error = true)
        while (retStatus.error) {
            retStatus = statusRequest(requestTxt)
        }
        return retStatus.httpResponse!!
    }

    suspend fun statusRequest(requestTxt:String):ControlHttpResponse{
        var retStatus = ControlHttpResponse(httpResponse = null, error = true)

        retStatus.error = try {
            retStatus.httpResponse = client.request(requestTxt)

            //Log.i("MyMSG",retStatus.toString())
            false
        } catch (e: Exception) {
            true
        }

        return retStatus
    }
////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun requestSession(): String {
        val requestSessionUrl = 
                "r0=SYS" +
                "&r1=registrationKeyAPI" +
                "&keyAPI=$keyAPI"

        val response = myRequest(requestSessionUrl)
        var retValue = ""
        if (response.status.isSuccess()) {
            retValue = response.body<SessionHandle>().sessionHandle
        } else {
            retValue = ""
        }
        return retValue
    }

    suspend fun autorisation(login: String, pass: String, sessionHandle: String): Boolean {

        val requestTxt =
                "r0=SYS" +
                "&r1=autorisation" +
                "&login=$login" +
                "&pass=$pass" +
                "&sessionHandle=$sessionHandle"
        //Log.v("MyMSG",requestTxt)
        val response = myRequest(requestTxt)
        if (response.status.isSuccess()) {
            val state  = response.body<AutorisationResult>().state
            if ( state == "false"){
                return false
            }else
                return true
        } else {
            return false
        }

    }

    suspend fun getDataByQrCode(qrCode:String, sessionHandle: String):List<ItemPass>{
        val requestTxt =
            "r0=SYS" +
                    "&r1=getDataByQrCode" +
                    "&qrCode=$qrCode" +
                    "&sessionHandle=$sessionHandle"
        val response = myRequest(requestTxt)
        Log.v("MyMSG",response.body<String>().toString())
        return  response.body<List<ItemPass>>()
    }

    suspend fun sendBinaryData(binaryData:String, sessionHandle: String){
        val requestTxt =
            "r0=SYS" +
                    "&r1=sendBinaryData" +
                    "&binaryData=$binaryData" +
                    "&sessionHandle=$sessionHandle"
        val response = myRequest(requestTxt)
    }
}


