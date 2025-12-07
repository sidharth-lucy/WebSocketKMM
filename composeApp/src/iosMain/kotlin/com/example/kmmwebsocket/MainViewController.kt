package com.example.kmmwebsocket

import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.example.kmmwebsocket.MessageApp.screens.ChatScreen
import com.example.kmmwebsocket.MessageApp.viewModel.WebSocketMessageViewModel

fun MainViewController() = ComposeUIViewController {
    val viewModel= WebSocketMessageViewModel()
    ChatScreen(
        modifier = Modifier,
        viewModel = viewModel
    )
}