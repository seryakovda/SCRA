package com.example.SCRA

import android.util.Log
import java.nio.charset.Charset

class myLog {
    companion object {
        private fun fixEncoding(s: String): String {
//            val bytes = s.toByteArray(Charset.forName("CP1251")) // читаем «сырые» байты
//            return String(bytes, Charset.forName("CP1251"))
            return s
        }

        fun i(tag: String, s: String) {
            Log.i(tag, fixEncoding(s))
        }

        fun w(tag: String, s: String) {
            Log.w(tag, fixEncoding(s))
        }

        fun e(tag: String, s: String) {
            Log.e(tag, fixEncoding(s))
        }
    }
}
