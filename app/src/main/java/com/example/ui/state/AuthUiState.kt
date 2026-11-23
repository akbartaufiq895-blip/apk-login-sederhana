package com.example.ui.state

import androidx.annotation.StringRes
import com.example.data.repository.UserProfile

sealed interface SessionUiState {
  data object Loading : SessionUiState

  data object Unauthenticated : SessionUiState

  data class Authenticated(val user: UserProfile) : SessionUiState
}

enum class PasswordStrength {
  NONE,
  WEAK,
  MEDIUM,
  STRONG,
}

data class LoginFormState(
  val email: String = "",
  val password: String = "",
  @StringRes val emailErrorRes: Int? = null,
  @StringRes val passwordErrorRes: Int? = null,
  @StringRes val bannerErrorRes: Int? = null,
  val isSubmitting: Boolean = false,
)

data class RegisterFormState(
  val fullName: String = "",
  val email: String = "",
  val phone: String = "",
  val password: String = "",
  val confirmPassword: String = "",
  val passwordStrength: PasswordStrength = PasswordStrength.NONE,
  @StringRes val nameErrorRes: Int? = null,
  @StringRes val emailErrorRes: Int? = null,
  @StringRes val phoneErrorRes: Int? = null,
  @StringRes val passwordErrorRes: Int? = null,
  @StringRes val confirmPasswordErrorRes: Int? = null,
  @StringRes val bannerErrorRes: Int? = null,
  val isSubmitting: Boolean = false,
)

data class HomeActionState(
  val isUpdatingProfile: Boolean = false,
  val profileUpdatedNotice: Boolean = false,
  @StringRes val errorMessageRes: Int? = null,
)
