package com.example.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.HomeWelcomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.state.SessionUiState
import com.example.ui.viewmodel.AuthViewModel

const val ROUTE_WELCOME = "welcome"
const val ROUTE_LOGIN = "login"
const val ROUTE_REGISTER = "register"

@Composable
fun SambutKuApp(
  authViewModel: AuthViewModel,
  modifier: Modifier = Modifier,
) {
  val sessionState by authViewModel.sessionState.collectAsStateWithLifecycle()

  when (val state = sessionState) {
    SessionUiState.Loading -> {
      Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
      ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
      }
    }

    is SessionUiState.Authenticated -> {
      val homeAction by authViewModel.homeAction.collectAsStateWithLifecycle()
      key(state.user.id) {
        HomeWelcomeScreen(
          user = state.user,
          actionState = homeAction,
          onUpdateProfile = { name, phone ->
            authViewModel.updateProfile(state.user.id, name, phone)
          },
          onClearNotices = authViewModel::clearHomeNotices,
          onLogout = authViewModel::logout,
          onDeleteAccount = { authViewModel.deleteAccount(state.user.id) },
          modifier = modifier,
        )
      }
    }

    SessionUiState.Unauthenticated -> {
      val navController = rememberNavController()
      val loginForm by authViewModel.loginForm.collectAsStateWithLifecycle()
      val registerForm by authViewModel.registerForm.collectAsStateWithLifecycle()

      NavHost(
        navController = navController,
        startDestination = ROUTE_WELCOME,
        modifier = modifier.fillMaxSize(),
        enterTransition = { slideInHorizontally(initialOffsetX = { it / 4 }) + fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 4 }) + fadeOut() },
      ) {
        composable(ROUTE_WELCOME) {
          WelcomeScreen(
            onNavigateToLogin = {
              authViewModel.resetFormErrors()
              navController.navigate(ROUTE_LOGIN)
            },
            onNavigateToRegister = {
              authViewModel.resetFormErrors()
              navController.navigate(ROUTE_REGISTER)
            },
          )
        }

        composable(ROUTE_LOGIN) {
          LoginScreen(
            formState = loginForm,
            onEmailChange = authViewModel::onLoginEmailChange,
            onPasswordChange = authViewModel::onLoginPasswordChange,
            onSubmit = authViewModel::submitLogin,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToRegister = {
              authViewModel.resetFormErrors()
              navController.navigate(ROUTE_REGISTER) {
                popUpTo(ROUTE_LOGIN) { inclusive = true }
              }
            },
          )
        }

        composable(ROUTE_REGISTER) {
          RegisterScreen(
            formState = registerForm,
            onNameChange = authViewModel::onRegisterNameChange,
            onEmailChange = authViewModel::onRegisterEmailChange,
            onPhoneChange = authViewModel::onRegisterPhoneChange,
            onPasswordChange = authViewModel::onRegisterPasswordChange,
            onConfirmPasswordChange = authViewModel::onRegisterConfirmPasswordChange,
            onSubmit = authViewModel::submitManualRegistration,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToLogin = {
              authViewModel.resetFormErrors()
              navController.navigate(ROUTE_LOGIN) {
                popUpTo(ROUTE_REGISTER) { inclusive = true }
              }
            },
          )
        }
      }
    }
  }
}
