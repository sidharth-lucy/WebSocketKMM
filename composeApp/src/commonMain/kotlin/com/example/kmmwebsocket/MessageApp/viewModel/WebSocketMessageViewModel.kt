package com.example.kmmwebsocket.MessageApp.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmmwebsocket.MessageApp.model.Message
import com.example.kmmwebsocket.MessageApp.model.MessageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WebSocketMessageViewModel: ViewModel() {

    private val socketManager = WebSocketManager()
    private var listeners: WebSocketManager.WebSocketListener?= null
    private val _message = mutableStateListOf<Message>()
    val messageList = _message

    private val _isClientConnected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isClientConnected: StateFlow<Boolean> = _isClientConnected


    init {
        connectSocket()
    }

    fun connectSocket(){
        listeners= object : WebSocketManager.WebSocketListener{
            override fun onMessageReceived(message: Message) {
                _message.add(message)
            }

            override fun onConnectionChanged(connected: Boolean) {
                _isClientConnected.value = connected
            }

            override fun onError(error: String) {
                _isClientConnected.value = false
                println(error)
            }
        }

        listeners?.let { socketManager.addListener(listener = it) }
    }

    fun connect(){
        viewModelScope.launch(Dispatchers.IO) {
            socketManager.connect()
        }
    }

    fun disconnect(){
        viewModelScope.launch {
            socketManager.disconnect()
        }
    }


    fun sendMessage(text: String){
        viewModelScope.launch(Dispatchers.IO) {
            if(_isClientConnected.value){
                val message = Message(
                    type = MessageType.TEXT,
                    messageTxt = text,
                    isSentByMe = true
                )
                socketManager.sendMessage(message)
                _message.add(message)
            }else{
                _message.add(Message(MessageType.TEXT, "Error Connection", false))
            }
        }
    }

    fun sendImage(imageData: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            if(_isClientConnected.value){
                val message = Message(
                    type = MessageType.IMAGE,
                    imageData=imageData ,
                    isSentByMe = true
                )
                socketManager.sendMessage(message)
                _message.add(message)
            }else{
                _message.add(Message(MessageType.TEXT, "Error Connection", false))
            }
        }

    }
    override fun onCleared() {
        listeners?.let {
            socketManager.removeListener(it)
        }
        super.onCleared()
    }

}