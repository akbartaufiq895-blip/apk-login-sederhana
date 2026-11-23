package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "users",
  indices = [Index(value = ["email"], unique = true)],
)
data class UserEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0L,
  val fullName: String,
  val email: String,
  val phone: String = "",
  val passwordHash: String,
  val salt: String,
  val createdAt: Long = System.currentTimeMillis(),
)
