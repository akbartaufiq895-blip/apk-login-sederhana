package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by
  preferencesDataStore(name = "sambutku_session")

class SessionPreferences(private val context: Context) {
  private val activeUserIdKey = longPreferencesKey("active_user_id")

  val activeUserIdFlow: Flow<Long?> =
    context.sessionDataStore.data.map { preferences -> preferences[activeUserIdKey] }

  suspend fun saveActiveUserId(userId: Long) {
    context.sessionDataStore.edit { preferences -> preferences[activeUserIdKey] = userId }
  }

  suspend fun clearSession() {
    context.sessionDataStore.edit { preferences -> preferences.remove(activeUserIdKey) }
  }
}
