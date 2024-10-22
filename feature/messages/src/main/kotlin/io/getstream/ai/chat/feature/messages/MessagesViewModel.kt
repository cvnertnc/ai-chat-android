/*
 * Designed and developed by 2024 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.getstream.ai.chat.feature.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.generationConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.ai.chat.core.data.repository.ChannelsRepository
import io.getstream.ai.chat.core.data.repository.MessagesRepository
import io.getstream.ai.chat.core.model.Channel
import io.getstream.ai.chat.core.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = MessagesViewModel.Factory::class)
class MessagesViewModel @AssistedInject constructor(
  channelsRepository: ChannelsRepository,
  private val messagesRepository: MessagesRepository,
  @Assisted private val index: Int,
) : ViewModel() {

  val channelState: StateFlow<Channel?> = channelsRepository.fetchChannel(index)
    .flatMapLatest { result -> flowOf(result.getOrNull()) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = null,
    )

  val messages: StateFlow<List<Message>> = messagesRepository.fetchMessages(index = index)
    .flatMapLatest { result ->
      flowOf(result.getOrNull())
    }
    .filterNotNull()
    .map { snapshot -> snapshot.messages }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList(),
    )

  private val model = GenerativeModel(
    modelName = "gemini-pro",
    apiKey = BuildConfig.GEMINI_API_KEY,
    generationConfig = generationConfig {
      temperature = 0.5f
      candidateCount = 1
      maxOutputTokens = 500
      topK = 30
      topP = 0.5f
    },
  )

  private val events: MutableStateFlow<MessagesEvent> = MutableStateFlow(MessagesEvent.Nothing)
  val latestResponse: StateFlow<String?> = events.flatMapLatest { event ->
    if (event is MessagesEvent.SendMessage) {
      generativeChat.sendMessageStream(event.message).map { it.text }
    } else {
      flowOf("")
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = null,
  )

  private val generativeChat: Chat = model.startChat()

  fun isCompleted(text: String?): Boolean {
    return generativeChat.history.any { it.parts.any { it.asTextOrNull() == text } }
  }

  fun handleEvents(messagesEvent: MessagesEvent) {
    this.events.value = messagesEvent
    when (messagesEvent) {
      is MessagesEvent.SendMessage -> sendMessage(
        message = messagesEvent.message,
        sender = messagesEvent.sender,
      )

      is MessagesEvent.CompleteGeneration -> {
        sendMessage(
          message = messagesEvent.message,
          sender = messagesEvent.sender,
        )
      }

      is MessagesEvent.Nothing -> Unit
    }
  }

  private fun sendMessage(message: String, sender: String) {
    messagesRepository.sendMessage(
      index = index,
      channel = channelState.value!!,
      message = message,
      sender = sender,
    )
  }

  @AssistedFactory
  internal interface Factory {
    fun create(index: Int): MessagesViewModel
  }
}

sealed interface MessagesEvent {

  data object Nothing : MessagesEvent

  data class SendMessage(val message: String, val sender: String) : MessagesEvent

  data class CompleteGeneration(val message: String, val sender: String) : MessagesEvent
}
