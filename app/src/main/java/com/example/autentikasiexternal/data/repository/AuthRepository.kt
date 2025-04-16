package com.example.autentikasiexternal.data.repository

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.autentikasiexternal.data.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

class AuthRepository(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val prefs = UserPreferences(application)

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    val userName: Flow<String> = prefs.userName
    val userEmail: Flow<String> = prefs.userEmail

    fun signInWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        viewModelScope.launch {
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        viewModelScope.launch {
                            prefs.saveUser(it.displayName ?: "", it.email ?: "")
                            _isLoggedIn.value = true
                        }
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.clearUser()
            auth.signOut()
            _isLoggedIn.value = false
        }
    }
}