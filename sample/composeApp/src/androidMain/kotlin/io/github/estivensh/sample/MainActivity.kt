package io.github.estivensh.sample

import io.github.estivensh.sample.App
import io.github.estivensh.sample.AppPermission
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppPermission { App() }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}