package com.example.kmmwebsocket

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform