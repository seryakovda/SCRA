package com.example.SCRA

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.SCRA.screens.auth.AuthViewModel
import com.example.SCRA.screens.edit.mainEditScreen
import com.example.SCRA.ui.theme.SCRATheme
import com.example.tire.screens.auth.authError.AuthScreenError
import com.example.tire.screens.auth.enterLogin.AuthScreen2
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //private lateinit var mainBinding: ActivityMainBinding
    //val uploadImagesViewModel: UploadImagesViewModel by viewModels()
    override fun onResume() {
        super.onResume()
        initLocation()
        initCamera()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //private val viewModel by viewModels()
        super.onCreate(savedInstanceState)
        //requestCameraPermition()
        setContent {
            SCRATheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TireNavHost()
                }

            }
        }
//------------------------------------------------------------


//------------------------------------------------------------
    }

    //private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    @SuppressLint("MissingPermission", "SetTextI18n")
    fun initLocation() {
        if (checkPermissions()) {

        } else {
            requestPermissions()
        }
    }
    fun initCamera(){
        if (checkPermissionsCamera()) {

        } else {
            requestPermissionsCamera()
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    private fun checkPermissionsCamera(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissionsCamera() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initLocation()
            }
        }
    }


}



//******************************************************************************************
//******************************************************************************************
//******************************************************************************************
//******************************************************************************************
//******************************************************************************************
//******************************************************************************************
//******************************************************************************************


sealed class Destination(val route: String) {
    object AuthScreen: Destination("AuthScreen")
    object AuthScreenError: Destination("AuthScreenError")
    object ScreenMainJob: Destination("ScreenMainJob")
    object ScreenEdit: Destination("ScreenEdit")


}



@SuppressLint("RestrictedApi")
@Composable
fun TireNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destination.AuthScreen.route
) {
    val navHostViewModel = hiltViewModel<NavHostViewModel>()
    val viewModel = hiltViewModel<AuthViewModel>()



    val state = navHostViewModel.authState.observeAsState()
    when (state.value) {
        NavHostViewModel.AuthState.FAIL ->                  navController.navigate(Destination.AuthScreenError.route)
        NavHostViewModel.AuthState.AUTH ->                  navController.popBackStack()
        NavHostViewModel.AuthState.SUCCESS ->               navController.navigate(Destination.ScreenMainJob.route)
        NavHostViewModel.AuthState.EDIT ->                  navController.navigate(Destination.ScreenEdit.route)


        else -> {}
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {


        composable(Destination.AuthScreen.route){
//            val lambda: (String, String) -> Unit = { login, pass, ->
//                viewModel.autorisation(login, pass)
//            }
            //AuthScreen(lambda)

            AuthScreen2(navController = navController)
        }

        composable(Destination.AuthScreenError.route) {
            AuthScreenError(
                navController = navController
            )
        }

        composable(Destination.ScreenEdit.route) {
            mainEditScreen(
                navController = navController
            )
        }

    }
}


///======================================================================================================='
//class MainActivity_ : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            SCRATheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Greeting("Android")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    SCRATheme {
//        Greeting("Android")
//    }
//}