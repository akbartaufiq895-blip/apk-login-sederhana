package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
  suspend fun findByEmail(email: String): UserEntity?

  @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
  fun observeById(userId: Long): Flow<UserEntity?>

  @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
  suspend fun findById(userId: Long): UserEntity?

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insertUser(user: UserEntity): Long

  @Query("UPDATE users SET fullName = :fullName, phone = :phone WHERE id = :userId")
  suspend fun updateProfile(userId: Long, fullName: String, phone: String)

  @Query("DELETE FROM users WHERE id = :userId")
  suspend fun deleteById(userId: Long)
}
