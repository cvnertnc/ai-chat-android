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
package io.getstream.ai.chat.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.ai.chat.android.navigation.AIChatNavHost
import io.getstream.ai.chat.core.designsystem.theme.AIChatTheme
import io.getstream.ai.chat.core.navigation.AIChatScreen
import io.getstream.ai.chat.core.navigation.AppComposeNavigator
import io.getstream.ai.chat.core.navigation.LocalComposeNavigator
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var composeNavigator: AppComposeNavigator<AIChatScreen>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      CompositionLocalProvider(
        LocalComposeNavigator provides composeNavigator,
      ) {
        AIChatTheme {
          AIChatNavHost(composeNavigator = composeNavigator)
        }
      }
    }
  }
}
