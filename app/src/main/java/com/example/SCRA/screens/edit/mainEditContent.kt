package com.example.SCRA.screens.edit

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.SCRA.R
import com.example.SCRA.data.ItemPass
import kotlinx.coroutines.launch
import qrscanner.QrScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mainEditContent(
    dataByQrCode:List<ItemPass>?,
    getDataByQrCode: (qrCode: String) -> Unit = { qrCode->},
){
    var qrCodeURL by remember { mutableStateOf("") }
    var startBarCodeScan by remember { mutableStateOf(false) }
    var flashlightOn by remember { mutableStateOf(false) }
    var launchGallery by remember { mutableStateOf(value = false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarHostState = compositionLocalOf<SnackbarHostState>{snackbarHostState}.current

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (qrCodeURL.isEmpty() && startBarCodeScan) {
                Column(
                    modifier = Modifier
                        .background(color = Color.Black)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(250.dp)
                            .clip(shape = RoundedCornerShape(size = 14.dp))
                            .clipToBounds()
                            .border(2.dp, Color.Gray, RoundedCornerShape(size = 14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        QrScanner(
                            modifier = Modifier
                                .clipToBounds()
                                .clip(shape = RoundedCornerShape(size = 14.dp)),
                            flashlightOn = flashlightOn,
                            launchGallery = launchGallery,
                            onCompletion = {

                                getDataByQrCode(it)
                                //qrCodeURL = it

                                startBarCodeScan = false
                            },
                            onGalleryCallBackHandler = {
                                launchGallery = it
                            },
                            onFailure = {
                                coroutineScope.launch {
                                    if (it.isEmpty()) {
                                        snackBarHostState.showSnackbar("Испорченный Qr-КОД")
                                    } else {
                                        snackBarHostState.showSnackbar(it)
                                    }
                                }
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 30.dp)
                            .background(
                                color = Color(0xFFF9F9F9),
                                shape = RoundedCornerShape(25.dp)
                            )
                            .height(35.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 5.dp, horizontal = 18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(11.dp)
                        ) {
                            //Icon(imageVector = if (flashlightOn) Icons.Filled.flashOn else Icons.Filled.FlashOff,
                            Icon( painter =  if (flashlightOn) painterResource(R.drawable.flash0) else painterResource(R.drawable.flash1),
                                "flash",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        flashlightOn = !flashlightOn
                                    })

                            VerticalDivider(
                                modifier = Modifier,
                                thickness = 1.dp,
                                color = Color(0xFFD8D8D8)
                            )

                            Image(
                                painter = painterResource(R.drawable.ic_gallery_icon),
                                contentDescription = "gallery",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        launchGallery = true
                                    }
                            )
                        }
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            startBarCodeScan = true
                            qrCodeURL = ""

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                    ) {
                        Text(
                            text = "Сканировать QR-Код",
                            modifier = Modifier
                                .background(Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            fontSize = 16.sp
                        )
                    }
                    printPass(dataByQrCode)
// отрисовка результата сканирования
//                    Text(
//                        text = qrCodeURL,
//                        color = Color.Black,
//                        modifier = Modifier.padding(top = 12.dp)
//                    )

                }
            }
        }


        if (startBarCodeScan) {
            Icon(
                imageVector = Icons.Filled.Close,
                "Close",
                modifier = Modifier
                    .padding(top = 12.dp, end = 12.dp)
                    .size(24.dp)
                    .clickable {
                        startBarCodeScan = false
                    }
                    .align(Alignment.TopEnd),
                tint = Color.White
            )
        }
    }

}
@Composable
fun printPass(dataByQrCode:List<ItemPass>?){
    Column(
        modifier = Modifier.verticalScroll(
            enabled = true,
            state = ScrollState(0),
            flingBehavior = null,
            reverseScrolling = false)
    ) {
        if (dataByQrCode != null) {
            var color: Long = 0xFFebf7ff
            dataByQrCode.forEach {
                    printPassRow(it)
            }
        }
    }
}

@Composable
fun printPassRow(it:ItemPass){
    val colorHex = it.color.replace("#", "FF")
    val color = colorHex.toLong(radix = 16)

    Column(
        modifier = Modifier
            .padding(top = 5.dp, start = 10.dp, bottom = 5.dp, end = 10.dp)
            .shadow(
                elevation = 4.dp,
            )
            .background(
                color = Color(color),
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = it.name ,
                color = Color.Black,
                modifier = Modifier.padding(start = 5.dp),
                fontSize = 12.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = it.value ,
                color = Color.Black,
                modifier = Modifier.padding(start = 5.dp),
                fontSize = 20.sp,
            )
        }
    }
}
