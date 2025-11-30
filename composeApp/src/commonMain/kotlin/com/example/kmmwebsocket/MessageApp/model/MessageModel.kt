package com.example.kmmwebsocket.MessageApp.model

import com.example.kmmwebsocket.MessageApp.Utility.DateTimeUtils

data class Message(
    val type: MessageType,
    val messageTxt: String?= null,
    val isSentByMe: Boolean,
    val imageData: ByteArray? = null,
    val createdTime: Long= DateTimeUtils.currentTimeMillis(),
)


enum class MessageType {
    TEXT, IMAGE
}