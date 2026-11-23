package com.example.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.state.PasswordStrength
import com.example.ui.theme.StrengthMediumColor
import com.example.ui.theme.StrengthStrongColor
import com.example.ui.theme.StrengthWeakColor

@Composable
fun SambutOutlinedField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  placeholder: String,
  leadingIcon: ImageVector,
  @StringRes errorRes: Int?,
  testTag: String,
  modifier: Modifier = Modifier,
  keyboardType: KeyboardType = KeyboardType.Text,
  capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
  imeAction: ImeAction = ImeAction.Next,
  onImeAction: () -> Unit = {},
  enabled: Boolean = true,
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier.fillMaxWidth().testTag(testTag),
    enabled = enabled,
    singleLine = true,
    label = { Text(text = label) },
    placeholder = { Text(text = placeholder) },
    leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
    isError = errorRes != null,
    supportingText =
      if (errorRes != null) {
        { Text(text = stringResource(id = errorRes)) }
      } else {
        null
      },
    shape = RoundedCornerShape(16.dp),
    colors =
      OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        cursorColor = MaterialTheme.colorScheme.primary,
      ),
    keyboardOptions =
      KeyboardOptions(
        keyboardType = keyboardType,
        capitalization = capitalization,
        imeAction = imeAction,
      ),
    keyboardActions =
      KeyboardActions(
        onNext = { onImeAction() },
        onDone = { onImeAction() },
      ),
  )
}

@Composable
fun SambutPasswordField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  placeholder: String,
  leadingIcon: ImageVector,
  @StringRes errorRes: Int?,
  testTag: String,
  modifier: Modifier = Modifier,
  imeAction: ImeAction = ImeAction.Done,
  onImeAction: () -> Unit = {},
  enabled: Boolean = true,
) {
  var passwordVisible by rememberSaveable { mutableStateOf(false) }

  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier.fillMaxWidth().testTag(testTag),
    enabled = enabled,
    singleLine = true,
    label = { Text(text = label) },
    placeholder = { Text(text = placeholder) },
    leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
    trailingIcon = {
      IconButton(
        onClick = { passwordVisible = !passwordVisible },
        modifier = Modifier.minimumInteractiveComponentSize().testTag("${testTag}_toggle"),
      ) {
        Icon(
          imageVector =
            if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
          contentDescription = stringResource(id = R.string.cd_toggle_password_visibility),
        )
      }
    },
    visualTransformation =
      if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
    isError = errorRes != null,
    supportingText =
      if (errorRes != null) {
        { Text(text = stringResource(id = errorRes)) }
      } else {
        null
      },
    shape = RoundedCornerShape(16.dp),
    colors =
      OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        cursorColor = MaterialTheme.colorScheme.primary,
      ),
    keyboardOptions =
      KeyboardOptions(
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
      ),
    keyboardActions =
      KeyboardActions(
        onNext = { onImeAction() },
        onDone = { onImeAction() },
      ),
  )
}

@Composable
fun PasswordStrengthIndicator(
  strength: PasswordStrength,
  modifier: Modifier = Modifier,
) {
  AnimatedVisibility(
    visible = strength != PasswordStrength.NONE,
    enter = fadeIn() + expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
    exit = fadeOut() + shrinkVertically(),
    modifier = modifier,
  ) {
    val (targetFraction, targetColor, labelRes) =
      when (strength) {
        PasswordStrength.NONE -> Triple(0f, Color.Transparent, R.string.register_strength_weak)
        PasswordStrength.WEAK -> Triple(0.33f, StrengthWeakColor, R.string.register_strength_weak)
        PasswordStrength.MEDIUM -> Triple(0.66f, StrengthMediumColor, R.string.register_strength_medium)
        PasswordStrength.STRONG -> Triple(1f, StrengthStrongColor, R.string.register_strength_strong)
      }

    val animatedFraction by
      animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "strengthFraction",
      )
    val animatedColor by
      animateColorAsState(targetValue = targetColor, label = "strengthColor")

    Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = stringResource(id = R.string.register_strength_label),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
          text = stringResource(id = labelRes),
          style = MaterialTheme.typography.labelMedium,
          color = animatedColor,
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Box(
          modifier =
            Modifier.fillMaxWidth(animatedFraction)
              .height(6.dp)
              .clip(RoundedCornerShape(50))
              .background(animatedColor)
        )
      }
    }
  }
}

@Composable
fun InlineErrorBanner(
  @StringRes messageRes: Int?,
  modifier: Modifier = Modifier,
  testTag: String = "error_banner",
) {
  AnimatedVisibility(
    visible = messageRes != null,
    enter = fadeIn() + expandVertically(),
    exit = fadeOut() + shrinkVertically(),
    modifier = modifier,
  ) {
    if (messageRes != null) {
      Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().testTag(testTag),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = stringResource(id = messageRes),
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }
    }
  }
}

@Composable
fun SambutPrimaryButton(
  text: String,
  onClick: () -> Unit,
  isLoading: Boolean,
  testTag: String,
  modifier: Modifier = Modifier,
  leadingIcon: ImageVector? = null,
) {
  Button(
    onClick = onClick,
    enabled = !isLoading,
    shape = RoundedCornerShape(16.dp),
    colors =
      ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
      ),
    modifier = modifier.fillMaxWidth().height(54.dp).testTag(testTag),
  ) {
    if (isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.size(22.dp),
        strokeWidth = 2.5.dp,
        color = MaterialTheme.colorScheme.onPrimary,
      )
    } else {
      if (leadingIcon != null) {
        Icon(
          imageVector = leadingIcon,
          contentDescription = null,
          modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
  }
}
