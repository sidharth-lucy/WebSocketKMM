package com.example.kmmwebsocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.kmmwebsocket.MessageApp.screens.ChatScreen
import com.example.kmmwebsocket.MessageApp.viewModel.WebSocketMessageViewModel

class MainActivity : ComponentActivity() {
    lateinit var viewModel: WebSocketMessageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        viewModel= WebSocketMessageViewModel()
        setContent {
            ChatScreen(
                modifier = Modifier,
                viewModel = viewModel
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}