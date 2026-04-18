package com.jetpackduba.gitnuro.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jetpackduba.gitnuro.ui.components.PrimaryButton
import com.jetpackduba.gitnuro.generated.resources.Res
import com.jetpackduba.gitnuro.generated.resources.branch_exists_dialog_message
import com.jetpackduba.gitnuro.ui.dialogs.base.MaterialDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun BranchExistsDialog(
    icon: Painter,
    title: String,
    branchName: String,
    onDismiss: () -> Unit,
    onCheckout: () -> Unit,
    onRebase: () -> Unit,
) {
    val dismissFocusRequester = remember { FocusRequester() }
    val checkoutFocusRequester = remember { FocusRequester() }
    val rebaseFocusRequester = remember { FocusRequester() }

    MaterialDialog(onCloseRequested = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.width(320.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .size(64.dp),
                tint = MaterialTheme.colors.onBackground,
            )

            Text(
                text = title,
                modifier = Modifier
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = stringResource(
                    Res.string.branch_exists_dialog_message,
                    branchName
                ),
                modifier = Modifier
                    .padding(bottom = 16.dp),
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )

            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PrimaryButton(
                    text = "No",
                    backgroundColor = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
                    textColor = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .focusRequester(dismissFocusRequester)
                        .focusProperties {
                            this.next = checkoutFocusRequester
                        },
                    onClick = onDismiss,
                )
                PrimaryButton(
                    text = "Checkout",
                    modifier = Modifier
                        .focusRequester(checkoutFocusRequester)
                        .focusProperties {
                            this.previous = dismissFocusRequester
                            this.next = rebaseFocusRequester
                        },
                    onClick = onCheckout,
                )
                PrimaryButton(
                    text = "Rebase",
                    modifier = Modifier
                        .focusRequester(rebaseFocusRequester)
                        .focusProperties {
                            this.previous = checkoutFocusRequester
                        },
                    onClick = onRebase,
                )
            }
        }
    }
}
