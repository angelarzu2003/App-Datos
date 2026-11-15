package mx.datoreal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import mx.datoreal.app.ui.AppRoot
import mx.datoreal.app.ui.theme.DatoRealTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DatoRealTheme {
                AppRoot()
            }
        }
    }
}
