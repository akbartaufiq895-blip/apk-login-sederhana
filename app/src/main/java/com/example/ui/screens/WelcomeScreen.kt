package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun WelcomeScreen(
  onNavigateToLogin: () -> Unit,
  onNavigateToRegister: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val backgroundBrush =
    Brush.verticalGradient(
      colors =
        listOf(
          MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f),
          MaterialTheme.colorScheme.background,
          MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.22f),
        )
    )

  Box(
    modifier =
      modifier
        .fillMaxSize()
        .background(backgroundBrush)
        .statusBarsPadding()
        .navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier =
        Modifier.widthIn(max = 560.dp)
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 24.dp, vertical = 20.dp),
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
          shape = RoundedCornerShape(50),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.VerifiedUser,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = stringResource(id = R.string.welcome_badge_label),
              style = MaterialTheme.typography.labelSmall,
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
          Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 2.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_welcome_hero),
              contentDescription = stringResource(id = R.string.welcome_hero_content_desc),
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f).clip(RoundedCornerShape(28.dp)),
            )
          }

          Surface(
            color = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 4.dp,
            modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-12).dp, y = 14.dp),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
          text = stringResource(id = R.string.welcome_title),
          style = MaterialTheme.typography.displaySmall,
          color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = stringResource(id = R.string.welcome_subtitle),
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(22.dp))

        Surface(
          color = MaterialTheme.colorScheme.surfaceContainerLow,
          shape = RoundedCornerShape(22.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            WelcomeFeatureRow(
              icon = Icons.Rounded.Bolt,
              label = stringResource(id = R.string.welcome_feature_login),
            )
            WelcomeFeatureRow(
              icon = Icons.Rounded.AppRegistration,
              label = stringResource(id = R.string.welcome_feature_register),
            )
            WelcomeFeatureRow(
              icon = Icons.Rounded.Lock,
              label = stringResource(id = R.string.welcome_feature_privacy),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Button(
          onClick = onNavigateToLogin,
          shape = RoundedCornerShape(16.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
          modifier = Modifier.fillMaxWidth().height(54.dp).testTag("welcome_login_button"),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.Login,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(id = R.string.welcome_action_login),
            style = MaterialTheme.typography.labelLarge,
          )
        }

        OutlinedButton(
          onClick = onNavigateToRegister,
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
          colors =
            ButtonDefaults.outlinedButtonColors(
              contentColor = MaterialTheme.colorScheme.primary,
            ),
          modifier = Modifier.fillMaxWidth().height(54.dp).testTag("welcome_register_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(id = R.string.welcome_action_register),
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}

@Composable
private fun WelcomeFeatureRow(icon: ImageVector, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier =
        Modifier.size(34.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.size(18.dp),
      )
    }
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface,
    )
  }
}
