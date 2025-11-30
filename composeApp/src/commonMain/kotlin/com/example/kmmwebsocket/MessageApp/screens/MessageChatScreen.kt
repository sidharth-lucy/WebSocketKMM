package com.example.kmmwebsocket.MessageApp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmmwebsocket.MessageApp.Utility.DateTimeUtils
import com.example.kmmwebsocket.MessageApp.model.Message
import com.example.kmmwebsocket.MessageApp.model.MessageType
import com.example.kmmwebsocket.MessageApp.viewModel.WebSocketMessageViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(modifier: Modifier = Modifier,viewModel: WebSocketMessageViewModel) {
    val messages =  remember { derivedStateOf { viewModel.messageList } }
    val isConnected = viewModel.isClientConnected.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.value) {
        if (messages.value.isNotEmpty()) {
            listState.animateScrollToItem(messages.value.size - 1)
        }
    }

//    val imagepicker = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) {uri ->
//        uri?.let {
//            val imageData = uriToByteArray(context,it)
//            imageData?.let {
//                viewModel.sendImage(imageData= it)
//            } ?: run { viewModel.sendMessage("Failed to send image") }
//        }
//    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("WebSocket Based ChatBox") },
            actions = {
                Button(
                    onClick = { if (isConnected.value) viewModel.disconnect() else viewModel.connect() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected.value) Color.Red else Color.Green
                    )
                ) {
                    Text(if (isConnected.value) "Disconnect" else "Connect")
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages.value) { message ->
                MessageViewComponent(message= message)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val messageText = remember { mutableStateOf("") }
            OutlinedTextField(
                value = messageText.value,
                onValueChange = { messageText.value = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Type a message") },
                shape = RoundedCornerShape(24.dp)
            )
            Button(
                onClick = {
                    if (messageText.value.isNotBlank()) {
                        viewModel.sendMessage(messageText.value)
                        messageText.value = ""
                    }
                },
                enabled = isConnected.value && messageText.value.isNotBlank()

            ) {
                Text("Send")
            }

            Button(onClick = {
//                imagepicker.launch("image/*")
            }) {
                Text("Image")
            }
        }
    }
}



@Composable
fun MessageViewComponent(message: Message) {
    val alignment = if (message.isSentByMe) Alignment.TopEnd else Alignment.TopStart
    val backgroundColor = if (message.isSentByMe) Color(0xFFDCF8C6) else Color(0xFFFFFFFF)
    val shape = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
        bottomStart = if (message.isSentByMe) 8.dp else 0.dp,
        bottomEnd = if (message.isSentByMe) 0.dp else 8.dp
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor, shape)
                .padding(12.dp)
        ) {
            if(message.type== MessageType.TEXT){
                Text(
                    text = message.messageTxt ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }else{
//                message.imageData?.let { data ->
//                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
//                    bitmap?.let {
//                        Image(
//                            bitmap = it.asImageBitmap(),
//                            contentDescription = "Chat image",
//                            modifier = Modifier
//                                .size(200.dp)
//                                .padding(bottom = 4.dp)
//                        )
//                    } ?: Text(
//                        text = "[Invalid Image]",
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
            }

            Text(
                text = DateTimeUtils.formatTime(
                    timestamp = message.createdTime,
                    pattern = "MMM dd, yyyy"
                ),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

//fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
//    return try {
//        context.contentResolver.openInputStream(uri)?.use { inputStream ->
//            val byteArrayOutputStream = ByteArrayOutputStream()
//            val buffer = ByteArray(1024)
//            var bytesRead: Int
//            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                byteArrayOutputStream.write(buffer, 0, bytesRead)
//            }
//            byteArrayOutputStream.toByteArray()
//        }
//    } catch (e: Exception) {
//        null
//    }
//}