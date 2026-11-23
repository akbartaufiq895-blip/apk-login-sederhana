package com.example

import com.example.data.local.PasswordHasher
import com.example.data.repository.AuthError
import com.example.data.repository.AuthOutcome
import com.example.data.repository.AuthRepository
import com.example.data.repository.UserProfile
import com.example.ui.state.PasswordStrength
import com.example.ui.state.SessionUiState
import com.example.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthLogicTest {
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun passwordHasher_verifiesCorrectPasswordAndRejectsWrongPassword() {
    val hashed1 = PasswordHasher.hash("Rahasia123!")
    val hashed2 = PasswordHasher.hash("Rahasia123!")

    assertNotEquals(hashed1.saltHex, hashed2.saltHex)
    assertNotEquals(hashed1.hashHex, hashed2.hashHex)
    assertTrue(PasswordHasher.verify("Rahasia123!", hashed1.hashHex, hashed1.saltHex))
    assertFalse(PasswordHasher.verify("Salah123!", hashed1.hashHex, hashed1.saltHex))
  }

  @Test
  fun validationHelpers_validateEmailPhoneAndStrength() {
    assertTrue(AuthViewModel.isValidEmail("budi@contoh.co.id"))
    assertFalse(AuthViewModel.isValidEmail("budi@"))
    assertTrue(AuthViewModel.isValidPhone("0812 3456 7890"))
    assertFalse(AuthViewModel.isValidPhone("123"))

    assertEquals(PasswordStrength.WEAK, AuthViewModel.evaluatePasswordStrength("12345"))
    assertEquals(PasswordStrength.MEDIUM, AuthViewModel.evaluatePasswordStrength("rahasia1"))
    assertEquals(PasswordStrength.STRONG, AuthViewModel.evaluatePasswordStrength("Rahasia123!"))
  }

  @Test
  fun manualRegistrationAndLogin_updateSessionState() = runTest {
    val repo = FakeAuthRepository()
    val vm = AuthViewModel(repo)

    vm.onRegisterNameChange("Budi Santoso")
    vm.onRegisterEmailChange("budi@example.com")
    vm.onRegisterPhoneChange("08123456789")
    vm.onRegisterPasswordChange("Rahasia123!")
    vm.onRegisterConfirmPasswordChange("Rahasia123!")
    vm.submitManualRegistration()
    advanceUntilIdle()

    val authenticated = vm.sessionState.first { it is SessionUiState.Authenticated }
    assertTrue(authenticated is SessionUiState.Authenticated)
    assertEquals("Budi Santoso", (authenticated as SessionUiState.Authenticated).user.fullName)

    vm.logout()
    advanceUntilIdle()
    val loggedOut = vm.sessionState.first { it is SessionUiState.Unauthenticated }
    assertEquals(SessionUiState.Unauthenticated, loggedOut)

    vm.onLoginEmailChange("budi@example.com")
    vm.onLoginPasswordChange("SalahPass")
    vm.submitLogin()
    advanceUntilIdle()
    assertEquals(R.string.error_invalid_credentials, vm.loginForm.value.bannerErrorRes)

    vm.onLoginPasswordChange("Rahasia123!")
    vm.submitLogin()
    advanceUntilIdle()
    val relogged = vm.sessionState.first { it is SessionUiState.Authenticated }
    assertEquals("budi@example.com", (relogged as SessionUiState.Authenticated).user.email)
    assertNull(vm.loginForm.value.bannerErrorRes)
  }

  private class FakeAuthRepository : AuthRepository {
    private val users = mutableListOf<Triple<UserProfile, String, String>>()
    private val activeUser = MutableStateFlow<UserProfile?>(null)
    private var nextId = 1L

    override val currentUserFlow: Flow<UserProfile?> = activeUser

    override suspend fun login(email: String, password: String): AuthOutcome {
      val cleanEmail = email.trim().lowercase()
      val match = users.firstOrNull { it.first.email == cleanEmail }
        ?: return AuthOutcome.Failure(AuthError.InvalidCredentials)
      return if (PasswordHasher.verify(password, match.second, match.third)) {
        activeUser.value = match.first
        AuthOutcome.Success(match.first)
      } else {
        AuthOutcome.Failure(AuthError.InvalidCredentials)
      }
    }

    override suspend fun registerManual(
      fullName: String,
      email: String,
      phone: String,
      password: String,
    ): AuthOutcome {
      val cleanEmail = email.trim().lowercase()
      if (users.any { it.first.email == cleanEmail }) {
        return AuthOutcome.Failure(AuthError.EmailAlreadyExists)
      }
      val hashed = PasswordHasher.hash(password)
      val profile =
        UserProfile(
          id = nextId++,
          fullName = fullName.trim(),
          email = cleanEmail,
          phone = phone.trim(),
          createdAt = 1_700_000_000_000L,
        )
      users.add(Triple(profile, hashed.hashHex, hashed.saltHex))
      activeUser.value = profile
      return AuthOutcome.Success(profile)
    }

    override suspend fun updateProfile(userId: Long, fullName: String, phone: String): Result<Unit> {
      val idx = users.indexOfFirst { it.first.id == userId }
      if (idx >= 0) {
        val updated = users[idx].first.copy(fullName = fullName, phone = phone)
        users[idx] = users[idx].copy(first = updated)
        activeUser.value = updated
      }
      return Result.success(Unit)
    }

    override suspend fun deleteAccount(userId: Long): Result<Unit> {
      users.removeAll { it.first.id == userId }
      activeUser.value = null
      return Result.success(Unit)
    }

    override suspend fun logout() {
      activeUser.value = null
    }
  }
}
