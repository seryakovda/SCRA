import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SCRA.NavHostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthContent(
    authState: NavHostViewModel.AuthState = NavHostViewModel.AuthState.AUTH,
    navigateToError: () -> Unit = { },
    navigateToSuccess: () -> Unit = { },
    auth: (login: String, password: String, IpServer: String, IdDoor: String) -> Unit = { login, password, IpServer, IdDoor -> },
    login0:String = "",
    password0:String = "",
    IpServer0:String = "",
    IdDoor0:String = ""
) {
    val login = remember { mutableStateOf(login0) }
    val pass = remember { mutableStateOf(password0) }
    val IpServer = remember { mutableStateOf(IpServer0) }
    val IdDoor = remember { mutableStateOf(IdDoor0) }

    LaunchedEffect(authState) {
        when (authState) {
            NavHostViewModel.AuthState.SUCCESS -> navigateToSuccess()
            NavHostViewModel.AuthState.FAIL -> navigateToError()
            else -> { }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp)
            .padding(start = 50.dp, top = 90.dp, end = 50.dp, bottom = 280.dp)
            .shadow(
                elevation = 4.dp,
            )
            .background(
                color = MaterialTheme.colorScheme.tertiary,
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
                    color = MaterialTheme.colorScheme.background,
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
                    .padding(top = 7.dp,start = 40.dp)
                    , text = "Авторизация"
                    ,color = Color(0xFFebf7ff)
                    ,fontSize = 20.sp
                )
                Text(modifier = Modifier
                    .padding(start = 60.dp)
                    , text = "Для входа, введите свои:"
                    ,color = Color(0xFFebf7ff)
                    , fontSize = 12.sp
                )
                Text(modifier = Modifier
                    .padding(start = 60.dp)
                    , text = "имя пользователя и пароль"
                    ,color = Color(0xFFebf7ff)
                    ,fontSize = 12.sp
                )
            }


        }
        Row(modifier = Modifier.padding(all = 8.dp)
        ) {

            Column (

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    label = { Text(text = "Сервер") },
                    value = IpServer.value
                    ,modifier = Modifier
                        .width(200.dp)
                    ,textStyle = TextStyle(fontSize = 20.sp)
                    ,colors = TextFieldDefaults.textFieldColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ,onValueChange = { ti -> IpServer.value = ti }
                )
                TextField(
                    label = { Text(text = "Логин") },
                    value = login.value
                    ,modifier = Modifier
                        .width(200.dp)
                    ,textStyle = TextStyle(fontSize = 20.sp)
                    ,colors = TextFieldDefaults.textFieldColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ,onValueChange = { ti -> login.value = ti }
                )
                //Text("Пароль", fontSize = 20.sp)
                TextField(
                    label = { Text(text = "Пароль") },

                    value = pass.value
                    ,textStyle = TextStyle(fontSize = 20.sp)
                    ,colors = TextFieldDefaults.textFieldColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ,singleLine = true
                    ,modifier = Modifier
                        .width(200.dp)
                    ,onValueChange = { newText -> pass.value = newText }
                    ,visualTransformation = PasswordVisualTransformation()

                )
                TextField(
                    label = { Text(text = "Дверь") },
                    value = IdDoor.value
                    ,modifier = Modifier
                        .width(200.dp)
                    ,textStyle = TextStyle(fontSize = 20.sp)
                    ,colors = TextFieldDefaults.textFieldColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ,onValueChange = { ti -> IdDoor.value = ti }
                )
            }
        }

        Row(modifier = Modifier.padding(all = 30.dp)) {
            Button(onClick = {
                auth(login.value, pass.value,IpServer.value,IdDoor.value)

            }) {
                Text("Вход", fontSize = 18.sp)
            }


        }

    }



}
