package com.example.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.data.local.PasswordHasher
import com.example.data.local.SessionPreferences
import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

data class UserProfile(
  val id: Long,
  val fullName: String,
  val email: String,
  val phone: String,
  val createdAt: Long,
)

sealed interface AuthError {
  data object EmailAlreadyExists : AuthError

  data object InvalidCredentials : AuthError

  data object UnexpectedError : AuthError
}

sealed interface AuthOutcome {
  data class Success(val user: UserProfile) : AuthOutcome

  data class Failure(val error: AuthError) : AuthOutcome
}

interface AuthRepository {
  val currentUserFlow: Flow<UserProfile?>

  suspend fun login(email: String, password: String): AuthOutcome

  suspend fun registerManual(
    fullName: String,
    email: String,
    phone: String,
    password: String,
  ): AuthOutcome

  suspend fun updateProfile(userId: Long, fullName: String, phone: String): Result<Unit>

  suspend fun deleteAccount(userId: Long): Result<Unit>

  suspend fun logout()
}

class LocalAuthRepository(
  private val userDao: UserDao,
  private val sessionPreferences: SessionPreferences,
  private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AuthRepository {

  @OptIn(ExperimentalCoroutinesApi::class)
  override val currentUserFlow: Flow<UserProfile?> =
    sessionPreferences.activeUserIdFlow.flatMapLatest { userId ->
      if (userId == null) {
        flowOf(null)
      } else {
        userDao.observeById(userId).map { entity -> entity?.toProfile() }
      }
    }

  override suspend fun login(email: String, password: String): AuthOutcome =
    withContext(ioDispatcher) {
      try {
        val normalizedEmail = email.trim().lowercase()
        val user = userDao.findByEmail(normalizedEmail)
          ?: return@withContext AuthOutcome.Failure(AuthError.InvalidCredentials)

        val valid = PasswordHasher.verify(password, user.passwordHash, user.salt)
        if (!valid) {
          return@withContext AuthOutcome.Failure(AuthError.InvalidCredentials)
        }

        sessionPreferences.saveActiveUserId(user.id)
        AuthOutcome.Success(user.toProfile())
      } catch (e: CancellationException) {
        throw e
      } catch (_: Exception) {
        AuthOutcome.Failure(AuthError.UnexpectedError)
      }
    }

  override suspend fun registerManual(
    fullName: String,
    email: String,
    phone: String,
    password: String,
  ): AuthOutcome =
    withContext(ioDispatcher) {
      try {
        val normalizedEmail = email.trim().lowercase()
        val cleanName = fullName.trim()
        val cleanPhone = phone.trim()

        if (userDao.findByEmail(normalizedEmail) != null) {
          return@withContext AuthOutcome.Failure(AuthError.EmailAlreadyExists)
        }

        val hashed = PasswordHasher.hash(password)
        val newEntity =
          UserEntity(
            fullName = cleanName,
            email = normalizedEmail,
            phone = cleanPhone,
            passwordHash = hashed.hashHex,
            salt = hashed.saltHex,
            createdAt = System.currentTimeMillis(),
          )

        val insertedId = userDao.insertUser(newEntity)
        sessionPreferences.saveActiveUserId(insertedId)
        val saved = userDao.findById(insertedId) ?: newEntity.copy(id = insertedId)
        AuthOutcome.Success(saved.toProfile())
      } catch (e: CancellationException) {
        throw e
      } catch (_: SQLiteConstraintException) {
        AuthOutcome.Failure(AuthError.EmailAlreadyExists)
      } catch (_: Exception) {
        AuthOutcome.Failure(AuthError.UnexpectedError)
      }
    }

  override suspend fun updateProfile(userId: Long, fullName: String, phone: String): Result<Unit> =
    withContext(ioDispatcher) {
      try {
        userDao.updateProfile(userId = userId, fullName = fullName.trim(), phone = phone.trim())
        Result.success(Unit)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Result.failure(e)
      }
    }

  override suspend fun deleteAccount(userId: Long): Result<Unit> =
    withContext(ioDispatcher) {
      try {
        sessionPreferences.clearSession()
        userDao.deleteById(userId)
        Result.success(Unit)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Result.failure(e)
      }
    }

  override suspend fun logout() {
    withContext(ioDispatcher) { sessionPreferences.clearSession() }
  }

  private fun UserEntity.toProfile(): UserProfile =
    UserProfile(
      id = id,
      fullName = fullName,
      email = email,
      phone = phone,
      createdAt = createdAt,
    )
}
