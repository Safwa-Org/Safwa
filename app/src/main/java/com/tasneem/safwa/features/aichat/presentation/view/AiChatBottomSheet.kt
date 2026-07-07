package com.tasneem.safwa.features.aichat.presentation.view

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tasneem.safwa.R
import com.tasneem.safwa.features.aichat.presentation.state.AiChatEvent
import com.tasneem.safwa.features.aichat.presentation.state.AiChatState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatBottomSheet(
   state: AiChatState,
   onEvent: (AiChatEvent) -> Unit,
   onDismiss: () -> Unit,
   sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
   modifier: Modifier = Modifier
) {
   val listState = rememberLazyListState()

   // Auto-scroll to bottom when messages change
   LaunchedEffect(state.messages.size, state.isTyping) {
       if (state.messages.isNotEmpty()) {
           listState.animateScrollToItem(
               // Scroll to typing indicator if visible, otherwise last message
               index = state.messages.size - 1 + if (state.isTyping) 1 else 0
           )
       }
   }

   ModalBottomSheet(
       onDismissRequest = onDismiss,
       sheetState = sheetState,
       shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
       containerColor = MaterialTheme.colorScheme.surface,
       dragHandle = {
           Box(
               modifier = Modifier
                   .padding(top = 12.dp)
                   .width(40.dp)
                   .height(4.dp)
                   .clip(RoundedCornerShape(2.dp))
                   .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
           )
       },
       modifier = modifier
   ) {
       Column(
           modifier = Modifier
               .fillMaxSize()
               .imePadding()
       ) {
           // ── Header ──
           ChatHeader(onDismiss = onDismiss)

           Spacer(modifier = Modifier.height(8.dp))

           // ── Messages ──
           LazyColumn(
               state = listState,
               modifier = Modifier
                   .weight(1f)
                   .fillMaxWidth(),
               contentPadding = PaddingValues(vertical = 8.dp)
           ) {
               items(
                   items = state.messages,
                   key = { it.id }
               ) { message ->
                   ChatBubble(message = message)
                   Spacer(modifier = Modifier.height(4.dp))
               }

               // Typing indicator
               if (state.isTyping) {
                   item(key = "typing_indicator") {
                       TypingIndicator()
                   }
               }
           }

           // ── Suggestion Chips ──
           if (state.suggestionChips.isNotEmpty()) {
               LazyRow(
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(vertical = 8.dp),
                   horizontalArrangement = Arrangement.spacedBy(8.dp),
                   contentPadding = PaddingValues(horizontal = 16.dp)
               ) {
                   items(state.suggestionChips) { chip ->
                       SuggestionChip(
                           onClick = { onEvent(AiChatEvent.SuggestionChipClicked(chip)) },
                           label = {
                               Text(
                                   text = chip,
                                   style = MaterialTheme.typography.bodySmall,
                                   maxLines = 1,
                                   overflow = TextOverflow.Ellipsis
                               )
                           },
                           shape = RoundedCornerShape(20.dp),
                           border = SuggestionChipDefaults.suggestionChipBorder(
                               enabled = true,
                               borderColor = MaterialTheme.colorScheme.outline
                           )
                       )
                   }
               }
           }

           // ── Input Field ──
           ChatInputBar(
               inputText = state.inputText,
               onInputChanged = { onEvent(AiChatEvent.InputChanged(it)) },
               onSend = { onEvent(AiChatEvent.SendMessage) }
           )
       }
   }
}

@Composable
private fun ChatHeader(
   onDismiss: () -> Unit,
   modifier: Modifier = Modifier
) {
   Row(
       modifier = modifier
           .fillMaxWidth()
           .padding(horizontal = 16.dp, vertical = 8.dp),
       verticalAlignment = Alignment.CenterVertically
   ) {
       // AI Avatar
       Box(
           modifier = Modifier
               .size(44.dp)
               .background(
                   color = MaterialTheme.colorScheme.primary,
                   shape = CircleShape
               ),
           contentAlignment = Alignment.Center
       ) {
           Text(
               text = "✦",
               color = Color.White,
               fontSize = 18.sp,
               fontWeight = FontWeight.Bold
           )
       }

       Spacer(modifier = Modifier.width(12.dp))

       // Title + Subtitle
       Column(modifier = Modifier.weight(1f)) {
           Text(
               text = stringResource(R.string.ai_chat_title),
               style = MaterialTheme.typography.titleMedium,
               fontWeight = FontWeight.Bold,
               color = MaterialTheme.colorScheme.onSurface
           )
           Text(
               text = stringResource(R.string.ai_chat_subtitle),
               style = MaterialTheme.typography.labelSmall,
               color = MaterialTheme.colorScheme.onSurfaceVariant,
               letterSpacing = 1.sp
           )
       }

       // Close button
       IconButton(onClick = onDismiss) {
           Icon(
               imageVector = Icons.Default.Close,
               contentDescription = stringResource(R.string.ai_chat_close),
               tint = MaterialTheme.colorScheme.onSurfaceVariant
           )
       }
   }
}

@Composable
private fun ChatInputBar(
   inputText: String,
   onInputChanged: (String) -> Unit,
   onSend: () -> Unit,
   modifier: Modifier = Modifier
) {
   Row(
       modifier = modifier
           .fillMaxWidth()
           .padding(horizontal = 16.dp, vertical = 12.dp)
           .navigationBarsPadding(),
       verticalAlignment = Alignment.CenterVertically,
       horizontalArrangement = Arrangement.spacedBy(8.dp)
   ) {
       OutlinedTextField(
           value = inputText,
           onValueChange = onInputChanged,
           modifier = Modifier.weight(1f),
           placeholder = {
               Text(
                   text = stringResource(R.string.ai_chat_input_placeholder),
                   style = MaterialTheme.typography.bodyMedium
               )
           },
           shape = RoundedCornerShape(24.dp),
           colors = OutlinedTextFieldDefaults.colors(
               focusedBorderColor = MaterialTheme.colorScheme.primary,
               unfocusedBorderColor = MaterialTheme.colorScheme.outline
           ),
           singleLine = true,
           textStyle = MaterialTheme.typography.bodyMedium
       )

       // Send button
       Surface(
           onClick = onSend,
           modifier = Modifier.size(48.dp),
           shape = CircleShape,
           color = MaterialTheme.colorScheme.primary,
           contentColor = Color.White
       ) {
           Box(contentAlignment = Alignment.Center) {
               Icon(
                   imageVector = Icons.AutoMirrored.Filled.Send,
                   contentDescription = stringResource(R.string.ai_chat_send),
                   modifier = Modifier.size(20.dp)
               )
           }
       }
   }
}

@Composable
private fun TypingIndicator(
   modifier: Modifier = Modifier
) {
   val infiniteTransition = rememberInfiniteTransition(label = "typing")

   Row(
       modifier = modifier
           .fillMaxWidth()
           .padding(horizontal = 16.dp, vertical = 4.dp),
       verticalAlignment = Alignment.Top
   ) {
       // AI avatar – matches ChatBubble style
       Box(
           modifier = Modifier
               .padding(top = 4.dp)
               .size(24.dp)
               .background(
                   color = MaterialTheme.colorScheme.primary,
                   shape = CircleShape
               ),
           contentAlignment = Alignment.Center
       ) {
           Text(
               text = "✦",
               color = Color.White,
               fontSize = 10.sp,
               fontWeight = FontWeight.Bold
           )
       }

       Spacer(modifier = Modifier.width(8.dp))

       Surface(
           shape = RoundedCornerShape(
               topStart = 4.dp,
               topEnd = 20.dp,
               bottomStart = 20.dp,
               bottomEnd = 20.dp
           ),
           color = MaterialTheme.colorScheme.surfaceVariant,
           tonalElevation = 1.dp
       ) {
           Row(
               modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
               horizontalArrangement = Arrangement.spacedBy(4.dp),
               verticalAlignment = Alignment.CenterVertically
           ) {
               repeat(3) { index ->
                   val alpha by infiniteTransition.animateFloat(
                       initialValue = 0.3f,
                       targetValue = 1f,
                       animationSpec = infiniteRepeatable(
                           animation = tween(
                               durationMillis = 600,
                               delayMillis = index * 200
                           ),
                           repeatMode = RepeatMode.Reverse
                       ),
                       label = "dot_$index"
                   )
                   Box(
                       modifier = Modifier
                           .size(8.dp)
                           .background(
                               color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                               shape = CircleShape
                           )
                   )
               }
           }
       }
   }
}
