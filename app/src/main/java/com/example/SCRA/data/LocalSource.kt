package com.example.tire.data


import android.content.Context
import android.content.SharedPreferences

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import java.io.File
import javax.inject.Inject

@Serializable
data class ImageListLocal (
    var idTireEvents:   Int,
    var fileName:       String
)
class LocalSource @Inject constructor(
    @ApplicationContext private val context: Context,
    //private var db: DB

    ) {
    var pref : SharedPreferences? = null

    init {
        pref = context.getSharedPreferences("TABLE",Context.MODE_PRIVATE)
    }


    fun saveTokenString(nameToken:String ,token: String) {
        val editor = pref?.edit()
        editor?.putString(nameToken,token)
        editor?.apply()
    }

    fun saveTokenBoolean(nameToken:String ,token: Boolean) {
        val editor = pref?.edit()
        editor?.putBoolean(nameToken,token)
        editor?.apply()
    }
    fun getTokenString(nameToken:String):String{
        return pref?.getString(nameToken,"")!!
    }
    fun getTokenBoolean(nameToken:String):Boolean{
        return pref?.getBoolean(nameToken,false)!!
    }


}


