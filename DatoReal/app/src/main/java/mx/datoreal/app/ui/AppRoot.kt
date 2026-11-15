package mx.datoreal.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import mx.datoreal.app.measurement.UsageAccess

private data class TabItem(val label: String, val icon: ImageVector)

@Composable
fun AppRoot() {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(UsageAccess.isGranted(context)) }
    var tab by rememberSaveable { mutableIntStateOf(0) }

    // Al volver de Ajustes, se vuelve a comprobar el permiso.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        granted = UsageAccess.isGranted(context)
    }

    val tabs = listOf(
        TabItem("Estado", Icons.Default.Info),
        TabItem("Apps", Icons.AutoMirrored.Filled.List),
        TabItem("En vivo", Icons.Default.PlayArrow),
        TabItem("Prueba", Icons.Default.CheckCircle)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { tab = index },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { inner ->
        Box(Modifier.padding(inner)) {
            when (tab) {
                0 -> DiagnosticoScreen(granted)
                1 -> AppsScreen(granted)
                2 -> LiveScreen(granted)
                else -> PruebaScreen(granted)
            }
        }
    }
}
