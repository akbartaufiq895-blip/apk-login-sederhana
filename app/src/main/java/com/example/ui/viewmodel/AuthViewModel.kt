package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.repository.AuthError
import com.example.data.repository.AuthOutcome
import com.example.data.repository.AuthRepository
import com.example.ui.state.HomeActionState
import com.example.ui.state.LoginFormState
import com.example.ui.state.PasswordStrength
import com.example.ui.state.RegisterFormState
import com.example.ui.state.SessionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

  val sessionState: StateFlow<SessionUiState> =
    authRepository.currentUserFlow
      .map { profile ->
        if (profile == null) SessionUiState.Unauthenticated
        else SessionUiState.Authenticated(profile)
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SessionUiState.Loading,
      )

  private val _loginForm = MutableStateFlow(LoginFormState())
  val loginForm: StateFlow<LoginFormState> = _loginForm.asStateFlow()

  private val _registerForm = MutableStateFlow(RegisterFormState())
  val registerForm: StateFlow<RegisterFormState> = _registerForm.asStateFlow()

  private val _homeAction = MutableStateFlow(HomeActionState())
  val homeAction: StateFlow<HomeActionState> = _homeAction.asStateFlow()

  fun onLoginEmailChange(value: String) {
    _loginForm.update {
      it.copy(email = value, emailErrorRes = null, bannerErrorRes = null)
    }
  }

  fun onLoginPasswordChange(value: String) {
    _loginForm.update {
      it.copy(password = value, passwordErrorRes = null, bannerErrorRes = null)
    }
  }

  fun submitLogin() {
    val current = _loginForm.value
    if (current.isSubmitting) return

    val emailTrimmed = current.email.trim()
    val emailError =
      when {
        emailTrimmed.isEmpty() -> R.string.error_email_required
        !isValidEmail(emailTrimmed) -> R.string.error_email_invalid
        else -> null
      }
    val passwordError =
      when {
        current.password.isEmpty() -> R.string.error_password_required
        else -> null
      }

    if (emailError != null || passwordError != null) {
      _loginForm.update {
        it.copy(
          emailErrorRes = emailError,
          passwordErrorRes = passwordError,
          bannerErrorRes = null,
        )
      }
      return
    }

    viewModelScope.launch {
      _loginForm.update { it.copy(isSubmitting = true, bannerErrorRes = null) }
      when (val result = authRepository.login(emailTrimmed, current.password)) {
        is AuthOutcome.Success -> {
          _loginForm.value = LoginFormState()
          _registerForm.value = RegisterFormState()
        }
        is AuthOutcome.Failure -> {
          val errorRes =
            when (result.error) {
              AuthError.InvalidCredentials -> R.string.error_invalid_credentials
              AuthError.EmailAlreadyExists -> R.string.error_email_already_registered
              AuthError.UnexpectedError -> R.string.error_generic
            }
          _loginForm.update { it.copy(isSubmitting = false, bannerErrorRes = errorRes) }
        }
      }
    }
  }

  fun onRegisterNameChange(value: String) {
    _registerForm.update {
      it.copy(fullName = value, nameErrorRes = null, bannerErrorRes = null)
    }
  }

  fun onRegisterEmailChange(value: String) {
    _registerForm.update {
      it.copy(email = value, emailErrorRes = null, bannerErrorRes = null)
    }
  }

  fun onRegisterPhoneChange(value: String) {
    _registerForm.update {
      it.copy(phone = value, phoneErrorRes = null, bannerErrorRes = null)
    }
  }

  fun onRegisterPasswordChange(value: String) {
    _registerForm.update {
      it.copy(
        password = value,
        passwordStrength = evaluatePasswordStrength(value),
        passwordErrorRes = null,
        confirmPasswordErrorRes = null,
        bannerErrorRes = null,
      )
    }
  }

  fun onRegisterConfirmPasswordChange(value: String) {
    _registerForm.update {
      it.copy(confirmPassword = value, confirmPasswordErrorRes = null, bannerErrorRes = null)
    }
  }

  fun submitManualRegistration() {
    val current = _registerForm.value
    if (current.isSubmitting) return

    val nameTrimmed = current.fullName.trim()
    val emailTrimmed = current.email.trim()
    val phoneTrimmed = current.phone.trim()

    val nameError = if (nameTrimmed.length < 2) R.string.error_name_required else null
    val emailError =
      when {
        emailTrimmed.isEmpty() -> R.string.error_email_required
        !isValidEmail(emailTrimmed) -> R.string.error_email_invalid
        else -> null
      }
    val phoneError =
      if (phoneTrimmed.isNotEmpty() && !isValidPhone(phoneTrimmed)) {
        R.string.error_phone_invalid
      } else {
        null
      }
    val passwordError =
      when {
        current.password.isEmpty() -> R.string.error_password_required
        current.password.length < 6 -> R.string.error_password_short
        else -> null
      }
    val confirmError =
      when {
        current.confirmPassword != current.password -> R.string.error_password_mismatch
        else -> null
      }

    if (
      nameError != null ||
        emailError != null ||
        phoneError != null ||
        passwordError != null ||
        confirmError != null
    ) {
      _registerForm.update {
        it.copy(
          nameErrorRes = nameError,
          emailErrorRes = emailError,
          phoneErrorRes = phoneError,
          passwordErrorRes = passwordError,
          confirmPasswordErrorRes = confirmError,
          bannerErrorRes = null,
        )
      }
      return
    }

    viewModelScope.launch {
      _registerForm.update { it.copy(isSubmitting = true, bannerErrorRes = null) }
      when (
        val result =
          authRepository.registerManual(
            fullName = nameTrimmed,
            email = emailTrimmed,
            phone = phoneTrimmed,
            password = current.password,
          )
      ) {
        is AuthOutcome.Success -> {
          _registerForm.value = RegisterFormState()
          _loginForm.value = LoginFormState()
        }
        is AuthOutcome.Failure -> {
          val errorRes =
            when (result.error) {
              AuthError.EmailAlreadyExists -> R.string.error_email_already_registered
              AuthError.InvalidCredentials -> R.string.error_invalid_credentials
              AuthError.UnexpectedError -> R.string.error_generic
            }
          _registerForm.update { it.copy(isSubmitting = false, bannerErrorRes = errorRes) }
        }
      }
    }
  }

  fun updateProfile(userId: Long, fullName: String, phone: String): Boolean {
    val cleanName = fullName.trim()
    val cleanPhone = phone.trim()
    if (cleanName.length < 2) {
      _homeAction.update { it.copy(errorMessageRes = R.string.error_name_required) }
      return false
    }
    if (cleanPhone.isNotEmpty() && !isValidPhone(cleanPhone)) {
      _homeAction.update { it.copy(errorMessageRes = R.string.error_phone_invalid) }
      return false
    }

    viewModelScope.launch {
      _homeAction.update {
        it.copy(isUpdatingProfile = true, errorMessageRes = null, profileUpdatedNotice = false)
      }
      val res = authRepository.updateProfile(userId, cleanName, cleanPhone)
      if (res.isSuccess) {
        _homeAction.update {
          it.copy(isUpdatingProfile = false, profileUpdatedNotice = true)
        }
      } else {
        _homeAction.update {
          it.copy(isUpdatingProfile = false, errorMessageRes = R.string.error_generic)
        }
      }
    }
    return true
  }

  fun clearHomeNotices() {
    _homeAction.update { it.copy(profileUpdatedNotice = false, errorMessageRes = null) }
  }

  fun deleteAccount(userId: Long) {
    viewModelScope.launch {
      authRepository.deleteAccount(userId)
      _loginForm.value = LoginFormState()
      _registerForm.value = RegisterFormState()
      _homeAction.value = HomeActionState()
    }
  }

  fun logout() {
    viewModelScope.launch {
      authRepository.logout()
      _loginForm.value = LoginFormState()
      _registerForm.value = RegisterFormState()
      _homeAction.value = HomeActionState()
    }
  }

  fun resetFormErrors() {
    _loginForm.update {
      it.copy(emailErrorRes = null, passwordErrorRes = null, bannerErrorRes = null)
    }
    _registerForm.update {
      it.copy(
        nameErrorRes = null,
        emailErrorRes = null,
        phoneErrorRes = null,
        passwordErrorRes = null,
        confirmPasswordErrorRes = null,
        bannerErrorRes = null,
      )
    }
  }

  companion object {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val PHONE_CLEAN_REGEX = Regex("^[0-9+]{8,15}$")

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    fun isValidPhone(phone: String): Boolean {
      val stripped = phone.replace(" ", "").replace("-", "")
      return PHONE_CLEAN_REGEX.matches(stripped)
    }

    fun evaluatePasswordStrength(password: String): PasswordStrength {
      if (password.isEmpty()) return PasswordStrength.NONE
      var score = 0
      if (password.length >= 6) score++
      if (password.length >= 8) score++
      if (password.any { it.isDigit() } && password.any { it.isLetter() }) score++
      if (password.any { !it.isLetterOrDigit() } || (password.any { it.isUpperCase() } && password.any { it.isLowerCase() })) {
        score++
      }
      return when {
        password.length < 6 || score <= 1 -> PasswordStrength.WEAK
        score in 2..3 -> PasswordStrength.MEDIUM
        else -> PasswordStrength.STRONG
      }
    }
  }
}

class AuthViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
      return AuthViewModel(authRepository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
  }
}
