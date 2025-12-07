package com.example.kmmwebsocket.MessageApp.networkHelper

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.websocket.WebSockets


actual fun createWebSocketHttpClient() = HttpClient(Darwin) {
    install(WebSockets)
}