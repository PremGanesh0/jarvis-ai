package com.jarvis.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jarvis.coremodel.ChatMessage
import com.jarvis.coremodel.MessageRole
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main chat screen composable.
 * POC: Minimal functional UI - no decorations.
 */
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ChatEvent.ScrollToBottom -> {
                    if (uiState.messages.isNotEmpty()) {
                        listState.animateScrollToItem(uiState.messages.size - 1)
                    }
                }
                is ChatEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is ChatEvent.CorrectionSaved -> {
                    snackbarHostState.showSnackbar("Correction saved! I'll learn from this.")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
            ) {
                Text(
                    text = "J.A.R.V.I.S. POC",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            // Messages list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(uiState.messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        onLongClick = {
                            if (message.role == MessageRole.ASSISTANT) {
                                viewModel.onStartCorrection(message.id)
                            }
                        },
                    )
                }

                // Streaming indicator
                if (uiState.isStreaming && uiState.streamingText.isNotEmpty()) {
                    item {
                        MessageBubble(
                            message = ChatMessage(
                                id = "streaming",
                                role = MessageRole.ASSISTANT,
                                content = uiState.streamingText + "▌",
                                timestamp = System.currentTimeMillis(),
                                conversationId = "",
                            ),
                            onLongClick = {},
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            // Correction mode banner
            if (uiState.correctionMode != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3CD))
                        .padding(8.dp),
                ) {
                    Text(
                        text = "✏️ Correction mode - Edit the response below",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            // Input area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = viewModel::onInputChanged,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            if (uiState.correctionMode != null) {
                                "Edit the response..."
                            } else {
                                "Type a message..."
                            }
                        )
                    },
                    enabled = !uiState.isLoading,
                    maxLines = 4,
                )

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(start = 8.dp),
                    )
                } else if (uiState.correctionMode != null) {
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Button(onClick = viewModel::onSubmitCorrection) {
                            Text("Save")
                        }
                        OutlinedButton(onClick = viewModel::onCancelCorrection) {
                            Text("Cancel")
                        }
                    }
                } else {
                    Button(
                        onClick = viewModel::onSendMessage,
                        modifier = Modifier.padding(start = 8.dp),
                        enabled = uiState.inputText.isNotBlank(),
                    ) {
                        Text("Send")
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    onLongClick: () -> Unit,
) {
    val isUser = message.role == MessageRole.USER

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(
                    color = if (isUser) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(12.dp),
                )
                .clickable(onClick = onLongClick)
                .padding(12.dp),
        ) {
            Text(
                text = message.content,
                color = if (isUser) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )

            if (!isUser && message.id != "streaming") {
                Text(
                    text = "Tap to correct",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
