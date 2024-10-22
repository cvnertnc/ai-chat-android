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
package io.getstream.ai.chat.android.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.getstream.ai.chat.core.designsystem.theme.AIChatTheme
import io.getstream.ai.chat.core.navigation.AIChatScreen
import io.getstream.ai.chat.core.navigation.AppComposeNavigator
import io.getstream.ai.chat.feature.channels.Channels
import io.getstream.ai.chat.feature.messages.Messages

@Composable
fun AIChatNavHost(
  modifier: Modifier = Modifier,
  composeNavigator: AppComposeNavigator<AIChatScreen>,
  navController: NavHostController = rememberNavController(),
  startDestination: AIChatScreen = AIChatScreen.Channels,
) {
  LaunchedEffect(Unit) {
    composeNavigator.handleNavigationCommands(navController)
  }

  NavHost(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
      .background(AIChatTheme.colors.background),
    navController = navController,
    startDestination = startDestination,
  ) {
    composable<AIChatScreen.Channels> {
      Channels { index, channel ->
        composeNavigator.navigate(
          AIChatScreen.Messages(index = index, channel = channel),
        )
      }
    }

    composable<AIChatScreen.Messages>(
      typeMap = AIChatScreen.Messages.typeMap,
    ) { backStackEntry ->
      val root: AIChatScreen.Messages = backStackEntry.toRoute()
      Messages(
        index = root.index,
        channel = root.channel,
        onBackClick = { composeNavigator.navigateUp() },
      )
    }
  }
}
