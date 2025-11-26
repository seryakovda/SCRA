import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bumptech.glide.gifdecoder.GifDecoder
import com.example.SCRA.NavHostViewModel
import com.example.SCRA.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingContent() {
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
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Простой способ без внешних библиотек
//        AndroidView(
//            factory = { context ->
//                ImageView(context).apply {
//                    setImageResource(R.drawable.loading)
//                    scaleType = ImageView.ScaleType.FIT_CENTER
//                }
//            },
//            modifier = Modifier
//                .size(120.dp)
//                .padding(16.dp)
//        )

        Text(
            text = "Загрузка...",
            modifier = Modifier.padding(top = 16.dp),
            color = MaterialTheme.colorScheme.onTertiary
        )
    }
}
