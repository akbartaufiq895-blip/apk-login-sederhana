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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Shield
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.InlineErrorBanner
import com.example.ui.components.PasswordStrengthIndicator
import com.example.ui.components.SambutOutlinedField
import com.example.ui.components.SambutPasswordField
import com.example.ui.components.SambutPrimaryButton
import com.example.ui.state.RegisterFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
  formState: RegisterFormState,
  onNameChange: (String) -> Unit,
  onEmailChange: (String) -> Unit,
  onPhoneChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
  onConfirmPasswordChange: (String) -> Unit,
  onSubmit: () -> Unit,
  onNavigateBack: () -> Unit,
  onNavigateToLogin: () -> Unit,
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
            modifier = Modifier.minimumInteractiveComponentSize().testTag("register_back_button"),
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
      modifier = Modifier.fillMaxSize().padding(innerPadding).imePadding(),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier =
          Modifier.widthIn(max = 560.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
      ) {
        Box(
          modifier =
            Modifier.size(56.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.secondaryContainer),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Rounded.PersonAdd,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(28.dp),
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = stringResource(id = R.string.register_title),
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = stringResource(id = R.string.register_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
          color = MaterialTheme.colorScheme.surfaceContainerLow,
          shape = RoundedCornerShape(24.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            InlineErrorBanner(
              messageRes = formState.bannerErrorRes,
              testTag = "register_error_banner",
            )

            SambutOutlinedField(
              value = formState.fullName,
              onValueChange = onNameChange,
              label = stringResource(id = R.string.register_name_label),
              placeholder = stringResource(id = R.string.register_name_placeholder),
              leadingIcon = Icons.Rounded.Person,
              errorRes = formState.nameErrorRes,
              testTag = "register_name_input",
              capitalization = KeyboardCapitalization.Words,
              imeAction = ImeAction.Next,
              enabled = !formState.isSubmitting,
            )

            SambutOutlinedField(
              value = formState.email,
              onValueChange = onEmailChange,
              label = stringResource(id = R.string.register_email_label),
              placeholder = stringResource(id = R.string.register_email_placeholder),
              leadingIcon = Icons.Rounded.Email,
              errorRes = formState.emailErrorRes,
              testTag = "register_email_input",
              keyboardType = KeyboardType.Email,
              imeAction = ImeAction.Next,
              enabled = !formState.isSubmitting,
            )

            SambutOutlinedField(
              value = formState.phone,
              onValueChange = onPhoneChange,
              label = stringResource(id = R.string.register_phone_label),
              placeholder = stringResource(id = R.string.register_phone_placeholder),
              leadingIcon = Icons.Rounded.Phone,
              errorRes = formState.phoneErrorRes,
              testTag = "register_phone_input",
              keyboardType = KeyboardType.Phone,
              imeAction = ImeAction.Next,
              enabled = !formState.isSubmitting,
            )

            SambutPasswordField(
              value = formState.password,
              onValueChange = onPasswordChange,
              label = stringResource(id = R.string.register_password_label),
              placeholder = stringResource(id = R.string.register_password_placeholder),
              leadingIcon = Icons.Rounded.Lock,
              errorRes = formState.passwordErrorRes,
              testTag = "register_password_input",
              imeAction = ImeAction.Next,
              enabled = !formState.isSubmitting,
            )

            PasswordStrengthIndicator(strength = formState.passwordStrength)

            SambutPasswordField(
              value = formState.confirmPassword,
              onValueChange = onConfirmPasswordChange,
              label = stringResource(id = R.string.register_confirm_password_label),
              placeholder = stringResource(id = R.string.register_confirm_password_placeholder),
              leadingIcon = Icons.Rounded.Lock,
              errorRes = formState.confirmPasswordErrorRes,
              testTag = "register_confirm_password_input",
              imeAction = ImeAction.Done,
              onImeAction = {
                focusManager.clearFocus()
                onSubmit()
              },
              enabled = !formState.isSubmitting,
            )

            Surface(
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                  imageVector = Icons.Rounded.Shield,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = stringResource(id = R.string.register_local_notice),
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            SambutPrimaryButton(
              text = stringResource(id = R.string.register_submit_button),
              onClick = {
                focusManager.clearFocus()
                onSubmit()
              },
              isLoading = formState.isSubmitting,
              testTag = "register_submit_button",
              leadingIcon = Icons.Rounded.PersonAdd,
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = stringResource(id = R.string.register_have_account_prompt),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("register_to_login_button"),
          ) {
            Text(
              text = stringResource(id = R.string.register_go_to_login),
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.primary,
            )
          }
        }
      }
    }
  }
}
