package com.example.kmmwebsocket.MessageApp.viewModel

import com.example.kmmwebsocket.MessageApp.model.Message
import com.example.kmmwebsocket.MessageApp.model.MessageType
import com.example.kmmwebsocket.MessageApp.networkHelper.createWebSocketHttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WebSocketManager {
    private var webSocket: DefaultClientWebSocketSession? = null
    private var isConnected: Boolean = false
    private val reconnectDelay = 5000L
    private var reconnectJob: Job? = null

    private val listeners = mutableListOf<WebSocketListener>()
//    private val ktorClient = HttpClient() {
//        install(WebSockets)
//    }
    private val ktorClient = createWebSocketHttpClient()

    interface WebSocketListener {
        fun onMessageReceived(message: Message)
        fun onConnectionChanged(connected: Boolean)
        fun onError(error: String)
    }


    suspend fun connect() {
        try {
            ktorClient.webSocket("wss://echo.websocket.org") {
//            ktorClient.webSocket("wss://ws.ifelse.io") {
                webSocket = this
                isConnected = true
                notifyConnectionChanged(true)

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val messageText = frame.readText()
                            val newMessage = Message(
                                messageTxt = messageText,
                                isSentByMe = false,
                                type = MessageType.TEXT,
                            )
                            notifyMessageReceived(newMessage)
                        }
                        is Frame.Binary -> {
                            val imageBinary = frame.readBytes()
                            val newMessage = Message(
                                isSentByMe = false,
                                type = MessageType.IMAGE,
                                imageData = imageBinary,
                            )
                            notifyMessageReceived(newMessage)

                        }
                        is Frame.Close -> {

                        }
                        is Frame.Ping -> {

                        }
                        is Frame.Pong -> {

                        }
                        else -> {
                            println("some error")
                        }
                    }
                }

            }
        }catch (e: Exception) {
            isConnected = false
            notifyConnectionChanged(false)
            notifyError("Connection failed: ${e.message}")
            scheduleReconnect()
        }
    }
    suspend fun connect1() {
        withContext(Dispatchers.IO){
            if (isConnected) return@withContext

            try {

                ktorClient.webSocket(
                    method = HttpMethod.Get,
                    host = "echo.websocket.org",
                    port = 443,
                    path = "/",
                    block = {
                        webSocket = this
                        isConnected = true
                        reconnectJob?.cancel()
                        notifyConnectionChanged(true)

                        for (frame in incoming) {
                            when (frame) {
                                is Frame.Text -> {
                                    val messageText = frame.readText()
                                    val newMessage = Message(
                                        messageTxt = messageText,
                                        isSentByMe = false,
                                        type = MessageType.TEXT,
                                    )
                                    notifyMessageReceived(newMessage)
                                }
                                is Frame.Binary -> {
                                    val imageBinary = frame.readBytes()
                                    val newMessage = Message(
                                        isSentByMe = false,
                                        type = MessageType.TEXT,
                                        imageData = imageBinary,
                                    )
                                    notifyMessageReceived(newMessage)
                                }
                                is Frame.Close -> {

                                }
                                is Frame.Ping -> {

                                }
                                is Frame.Pong -> {

                                }
                                else -> {
                                    println("some error")
                                }
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                isConnected = false
                notifyConnectionChanged(false)
                notifyError("Connection failed: ${e.message}")
                scheduleReconnect()
            }
        }
    }

    suspend fun sendMessage(message: Message) {
        withContext(Dispatchers.IO){
            if (!isConnected) {
                notifyError("Not connected to server")
                return@withContext
            }

            try {
                when(message.type){
                    MessageType.TEXT -> {
                        val jsonMessage = message.messageTxt ?: "default message"
                        webSocket?.send(Frame.Text(jsonMessage))
                    }
                    MessageType.IMAGE -> {
                        message.imageData?.let {
                            webSocket?.send(frame = Frame.Binary(
                                data = it,
                                fin = true
                            ))

                        }
                    }
                }


            } catch (e: Exception) {
                notifyError("Failed to send message: ${e.message}")
            }
        }
    }

    suspend fun disconnect() {
        reconnectJob?.cancel()
        webSocket?.close()
        webSocket = null
        isConnected = false
        notifyConnectionChanged(false)
    }

     suspend fun scheduleReconnect() {
        reconnectJob?.cancel()
        reconnectJob = CoroutineScope(Dispatchers.Default).launch {
            delay(reconnectDelay)
            connect()
        }
    }

    fun addListener(listener: WebSocketListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: WebSocketListener) {
        listeners.remove(listener)
    }

     private fun notifyMessageReceived(message: Message) {
        listeners.forEach { it.onMessageReceived(message) }
    }

    private fun notifyConnectionChanged(connected: Boolean) {
        listeners.forEach { it.onConnectionChanged(connected) }
    }

    private fun notifyError(error: String) {
        listeners.forEach { it.onError(error) }
    }
}