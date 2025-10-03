package com.example.tire.screens.auth.authError

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.SCRA.NavHostViewModel
//import com.example.SCRA.ui.theme.TireTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthContentError(
    authState: NavHostViewModel.AuthState = NavHostViewModel.AuthState.FAIL,
    navigateToAuthScreen: () -> Unit = { },
    BtnOK: () -> Unit = { },
) {

    LaunchedEffect(authState) {
        when (authState) {
            NavHostViewModel.AuthState.AUTH -> navigateToAuthScreen()
            else -> { }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp)
            .padding(start = 50.dp, top = 90.dp, end = 50.dp, bottom = 330.dp)
            .shadow(
                elevation = 4.dp,
            )
            .background(
                color = Color(0xFFFFCCCC),
                shape = RoundedCornerShape(4.dp)
            )

        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(all = 2.dp)
                .height(80.dp)
                .background(
                    color = Color(0xFFE03C3C),
                    shape = RoundedCornerShape(4.dp)
                )
                .fillMaxWidth()
                .shadow(
                    elevation = 0.dp,
                    shape = RoundedCornerShape(10.dp),
                )
        ) {

            Column {
                Text(modifier = Modifier
                    .padding(top = 17.dp,start = 40.dp)
                    , text = "Внимание"
                    ,color = Color(0xFFebf7ff)
                    ,fontSize = 20.sp
                )
                Text(modifier = Modifier
                    .padding(start = 60.dp)
                    , text = "Ошибка авторизации"
                    ,color = Color(0xFFebf7ff)
                    , fontSize = 12.sp
                )

            }


        }
        Row(modifier = Modifier.padding(all = 8.dp)
        ) {

            Column (

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(modifier = Modifier
                    .padding(top = 50.dp)
                    , text = "Неверный "
                    ,color = Color(0xFFFF0000)
                    , fontSize = 22.sp
                )
                Text(modifier = Modifier

                    , text = "логин или пароль"
                    ,color = Color(0xFFFF0000)
                    ,fontSize = 22.sp
                )
            }
        }

        Row(modifier = Modifier.padding(all = 30.dp)) {
            Button(onClick = {
                BtnOK()
            }) {
                Text("Повторить ввод", fontSize = 16.sp)
            }
        }

    }

}
/*
@Preview(showBackground = true, showSystemUi = false)
@Composable
fun AuthPreview3() {
    TireTheme {
        AuthContentError {  }
    }
}

*/