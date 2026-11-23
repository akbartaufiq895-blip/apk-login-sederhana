package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.local.SessionPreferences
import com.example.data.repository.LocalAuthRepository
import com.example.navigation.SambutKuApp
import com.example.ui.theme.SambutKuTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getInstance(applicationContext)
    val sessionPreferences = SessionPreferences(applicationContext)
    val authRepository = LocalAuthRepository(database.userDao(), sessionPreferences)
    val viewModelFactory = AuthViewModelFactory(authRepository)

    setContent {
      SambutKuTheme {
        val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
        SambutKuApp(authViewModel = authViewModel)
      }
    }
  }
}
