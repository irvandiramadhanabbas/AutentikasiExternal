package com.example.autentikasiexternal.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.*
import com.example.autentikasiexternal.data.repository.AuthRepository
import com.example.autentikasiexternal.presentation.login.LoginScreen
import com.example.autentikasiexternal.presentation.home.HomeScreen
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import com.example.autentikasiexternal.R
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authViewModel: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(this)[AuthRepository::class.java]

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
            val name by authViewModel.userName.collectAsState(initial = "")
            val email by authViewModel.userEmail.collectAsState(initial = "")

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    if (account != null) {
                        authViewModel.signInWithGoogle(account)
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }

            if (isLoggedIn) {
                HomeScreen(
                    name = name,
                    email = email,
                    onLogout = {
                        authViewModel.logout()
                        googleSignInClient.signOut()
                    }
                )
            } else {
                LoginScreen {
                    launcher.launch(googleSignInClient.signInIntent)
                }
            }
        }
    }
}