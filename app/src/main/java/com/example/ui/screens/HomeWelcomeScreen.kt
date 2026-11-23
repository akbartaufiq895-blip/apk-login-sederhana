package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.repository.UserProfile
import com.example.ui.components.SambutOutlinedField
import com.example.ui.state.HomeActionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeWelcomeScreen(
  user: UserProfile,
  actionState: HomeActionState,
  onUpdateProfile: (fullName: String, phone: String) -> Boolean,
  onClearNotices: () -> Unit,
  onLogout: () -> Unit,
  onDeleteAccount: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  var showEditDialog by rememberSaveable { mutableStateOf(false) }
  var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

  val profileUpdatedText = stringResource(id = R.string.home_profile_updated_msg)

  LaunchedEffect(actionState.profileUpdatedNotice) {
    if (actionState.profileUpdatedNotice) {
      snackbarHostState.showSnackbar(profileUpdatedText)
      onClearNotices()
    }
  }

  val initials =
    remember(user.fullName) {
      user.fullName
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifEmpty { "U" }
    }

  val formattedJoinDate =
    remember(user.createdAt) {
      val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
      formatter.format(Date(user.createdAt))
    }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier =
          Modifier.widthIn(max = 560.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
      ) {
        // Hero Welcome Banner
        Surface(
          shape = RoundedCornerShape(28.dp),
          tonalElevation = 2.dp,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Box(modifier = Modifier.fillMaxWidth()) {
            Image(
              painter = painterResource(id = R.drawable.img_home_banner),
              contentDescription = stringResource(id = R.string.home_banner_content_desc),
              contentScale = ContentScale.Crop,
              modifier = Modifier.matchParentSize(),
            )
            Box(
              modifier =
                Modifier.matchParentSize()
                  .background(
                    Brush.verticalGradient(
                      colors =
                        listOf(
                          Color(0xD9093440),
                          Color(0xEB0F4C5C),
                        )
                    )
                  )
            )
            Column(modifier = Modifier.padding(22.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Box(
                  modifier =
                    Modifier.size(54.dp)
                      .clip(CircleShape)
                      .background(MaterialTheme.colorScheme.secondary),
                  contentAlignment = Alignment.Center,
                ) {
                  Text(
                    text = initials,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                  )
                }

                Surface(
                  color = Color.White.copy(alpha = 0.16f),
                  contentColor = Color.White,
                  shape = RoundedCornerShape(50),
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.CheckCircle,
                      contentDescription = null,
                      modifier = Modifier.size(15.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = stringResource(id = R.string.home_session_active_badge),
                      style = MaterialTheme.typography.labelSmall,
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(18.dp))

              Text(
                text = stringResource(id = R.string.home_greeting_prefix),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.85f),
              )
              Text(
                text = user.fullName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.testTag("home_user_name"),
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = stringResource(id = R.string.home_welcome_message),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.88f),
              )
            }
          }
        }

        // Account Details Card
        Surface(
          color = MaterialTheme.colorScheme.surfaceContainerLow,
          shape = RoundedCornerShape(24.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = stringResource(id = R.string.home_account_details_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
              )
              TextButton(
                onClick = {
                  onClearNotices()
                  showEditDialog = true
                },
                modifier =
                  Modifier.minimumInteractiveComponentSize().testTag("home_edit_profile_button"),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Edit,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = stringResource(id = R.string.action_edit_profile),
                  style = MaterialTheme.typography.labelLarge,
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoRow(
              icon = Icons.Rounded.Person,
              label = stringResource(id = R.string.home_label_full_name),
              value = user.fullName,
            )
            HorizontalDivider(
              modifier = Modifier.padding(vertical = 12.dp),
              color = MaterialTheme.colorScheme.outlineVariant,
            )
            ProfileInfoRow(
              icon = Icons.Rounded.Email,
              label = stringResource(id = R.string.home_label_email),
              value = user.email,
            )
            HorizontalDivider(
              modifier = Modifier.padding(vertical = 12.dp),
              color = MaterialTheme.colorScheme.outlineVariant,
            )
            ProfileInfoRow(
              icon = Icons.Rounded.Phone,
              label = stringResource(id = R.string.home_label_phone),
              value =
                user.phone.ifBlank { stringResource(id = R.string.home_phone_not_set) },
            )
            HorizontalDivider(
              modifier = Modifier.padding(vertical = 12.dp),
              color = MaterialTheme.colorScheme.outlineVariant,
            )
            ProfileInfoRow(
              icon = Icons.Rounded.CalendarToday,
              label = stringResource(id = R.string.home_label_joined),
              value = formattedJoinDate,
            )
          }
        }

        // Security Safeguard Info Card
        Surface(
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Box(
              modifier =
                Modifier.size(40.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Rounded.VerifiedUser,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp),
              )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(
                text = stringResource(id = R.string.home_security_card_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = stringResource(id = R.string.home_security_card_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
              )
            }
          }
        }

        // Logout & Delete Actions
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          OutlinedButton(
            onClick = onLogout,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            colors =
              ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
              ),
            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("home_logout_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.Logout,
              contentDescription = null,
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = stringResource(id = R.string.action_logout),
              style = MaterialTheme.typography.labelLarge,
            )
          }

          TextButton(
            onClick = { showDeleteDialog = true },
            colors =
              ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error,
              ),
            modifier =
              Modifier.align(Alignment.CenterHorizontally)
                .minimumInteractiveComponentSize()
                .testTag("home_delete_account_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.DeleteOutline,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = stringResource(id = R.string.action_delete_account),
              style = MaterialTheme.typography.labelLarge,
            )
          }
        }
      }
    }
  }

  if (showEditDialog) {
    EditProfileDialog(
      initialName = user.fullName,
      initialPhone = user.phone,
      errorRes = actionState.errorMessageRes,
      isSaving = actionState.isUpdatingProfile,
      onDismiss = {
        onClearNotices()
        showEditDialog = false
      },
      onSave = { name, phone ->
        val accepted = onUpdateProfile(name, phone)
        if (accepted) {
          showEditDialog = false
        }
      },
    )
  }

  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text(text = stringResource(id = R.string.home_delete_dialog_title)) },
      text = { Text(text = stringResource(id = R.string.home_delete_dialog_message)) },
      confirmButton = {
        Button(
          onClick = {
            showDeleteDialog = false
            onDeleteAccount()
          },
          colors =
            ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.error,
              contentColor = MaterialTheme.colorScheme.onError,
            ),
          modifier = Modifier.testTag("confirm_delete_button"),
        ) {
          Text(text = stringResource(id = R.string.home_delete_confirm))
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text(text = stringResource(id = R.string.action_cancel))
        }
      },
    )
  }
}

@Composable
private fun ProfileInfoRow(
  icon: ImageVector,
  label: String,
  value: String,
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(20.dp),
    )
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

@Composable
private fun EditProfileDialog(
  initialName: String,
  initialPhone: String,
  errorRes: Int?,
  isSaving: Boolean,
  onDismiss: () -> Unit,
  onSave: (String, String) -> Unit,
) {
  var name by rememberSaveable { mutableStateOf(initialName) }
  var phone by rememberSaveable { mutableStateOf(initialPhone) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = stringResource(id = R.string.home_edit_dialog_title)) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SambutOutlinedField(
          value = name,
          onValueChange = { name = it },
          label = stringResource(id = R.string.register_name_label),
          placeholder = stringResource(id = R.string.register_name_placeholder),
          leadingIcon = Icons.Rounded.Person,
          errorRes = errorRes.takeIf { it == R.string.error_name_required },
          testTag = "edit_name_input",
          capitalization = KeyboardCapitalization.Words,
          imeAction = ImeAction.Next,
          enabled = !isSaving,
        )
        SambutOutlinedField(
          value = phone,
          onValueChange = { phone = it },
          label = stringResource(id = R.string.register_phone_label),
          placeholder = stringResource(id = R.string.register_phone_placeholder),
          leadingIcon = Icons.Rounded.Phone,
          errorRes = errorRes.takeIf { it == R.string.error_phone_invalid },
          testTag = "edit_phone_input",
          keyboardType = KeyboardType.Phone,
          imeAction = ImeAction.Done,
          enabled = !isSaving,
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onSave(name, phone) },
        enabled = !isSaving,
        modifier = Modifier.testTag("edit_save_button"),
      ) {
        Text(text = stringResource(id = R.string.action_save))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss, enabled = !isSaving) {
        Text(text = stringResource(id = R.string.action_cancel))
      }
    },
  )
}
