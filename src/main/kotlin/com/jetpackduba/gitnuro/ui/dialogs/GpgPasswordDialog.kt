package com.jetpackduba.gitnuro.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jetpackduba.gitnuro.credentials.CredentialsRequested
import com.jetpackduba.gitnuro.credentials.CredentialsState
import com.jetpackduba.gitnuro.keybindings.KeybindingOption
import com.jetpackduba.gitnuro.keybindings.matchesBinding
import com.jetpackduba.gitnuro.theme.outlinedTextFieldColors
import com.jetpackduba.gitnuro.theme.onBackgroundSecondary
import com.jetpackduba.gitnuro.ui.components.AdjustableOutlinedTextField
import com.jetpackduba.gitnuro.ui.components.PrimaryButton

@Composable
fun GpgPasswordDialog(
    gpgCredentialsRequested: CredentialsRequested.GpgCredentialsRequested,
    onReject: () -> Unit,
    onAccept: (password: String) -> Unit
) {
   PasswordDialog(
       title = "Introduce your GPG key's password",
       subtitle = "Your GPG key is protected with a password",
       icon = "key.svg",
       cancelButtonText = "Do not sign",
       isRetry = gpgCredentialsRequested.isRetry,
       password = gpgCredentialsRequested.password,
       retryMessage = "Invalid password, please try again",
       onReject = onReject,
       onAccept = onAccept,
   )
}