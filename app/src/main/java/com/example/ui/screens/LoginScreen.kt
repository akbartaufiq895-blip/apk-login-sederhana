package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.InlineErrorBanner
import com.example.ui.components.SambutOutlinedField
import com.example.ui.components.SambutPasswordField
import com.example.ui.components.SambutPrimaryButton
import com.example.ui.state.LoginFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  formState: LoginFormState,
  onEmailChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
  onSubmit: () -> Unit,
  onNavigateBack: () -> Unit,
  onNavigateToRegister: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val focusManager = LocalFocusManager.current

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("login_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(id = R.string.cd_navigate_back),
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
      )
    },
  ) { innerPadding ->
    Box(
      modifier =
        Modifier.fillMaxSize().padding(innerPadding).imePadding(),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier =
          Modifier.widthIn(max = 560.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp),
      ) {
        Box(
          modifier =
            Modifier.size(56.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Rounded.LockOpen,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(28.dp),
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
          text = stringResource(id = R.string.login_title),
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = stringResource(id = R.string.login_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
          color = MaterialTheme.colorScheme.surfaceContainerLow,
          shape = RoundedCornerShape(24.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            InlineErrorBanner(
              messageRes = formState.bannerErrorRes,
              testTag = "login_error_banner",
            )

            SambutOutlinedField(
              value = formState.email,
              onValueChange = onEmailChange,
              label = stringResource(id = R.string.login_email_label),
              placeholder = stringResource(id = R.string.login_email_placeholder),
              leadingIcon = Icons.Rounded.Email,
              errorRes = formState.emailErrorRes,
              testTag = "login_email_input",
              keyboardType = KeyboardType.Email,
              imeAction = ImeAction.Next,
              enabled = !formState.isSubmitting,
            )

            SambutPasswordField(
              value = formState.password,
              onValueChange = onPasswordChange,
              label = stringResource(id = R.string.login_password_label),
              placeholder = stringResource(id = R.string.login_password_placeholder),
              leadingIcon = Icons.Rounded.Lock,
              errorRes = formState.passwordErrorRes,
              testTag = "login_password_input",
              imeAction = ImeAction.Done,
              onImeAction = {
                focusManager.clearFocus()
                onSubmit()
              },
              enabled = !formState.isSubmitting,
            )

            Spacer(modifier = Modifier.height(4.dp))

            SambutPrimaryButton(
              text = stringResource(id = R.string.login_submit_button),
              onClick = {
                focusManager.clearFocus()
                onSubmit()
              },
              isLoading = formState.isSubmitting,
              testTag = "login_submit_button",
              leadingIcon = Icons.AutoMirrored.Rounded.Login,
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = stringResource(id = R.string.login_no_account_prompt),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("login_to_register_button"),
          ) {
            Text(
              text = stringResource(id = R.string.login_go_to_register),
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.secondary,
            )
          }
        }
      }
    }
  }
}
