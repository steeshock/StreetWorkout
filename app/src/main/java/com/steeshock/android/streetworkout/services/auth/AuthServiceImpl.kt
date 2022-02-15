package com.steeshock.android.streetworkout.services.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

class AuthServiceImpl : IAuthService {

    private var auth: FirebaseAuth = Firebase.auth

    override suspend fun isUserAuthorized(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun getUserEmail(): String? {
        return auth.currentUser?.email
    }

    override suspend fun getUsername(): String? {
        return auth.currentUser?.displayName
    }

    override suspend fun signUp(
        userCredentials: UserCredentials,
        onSuccess: (String?) -> Unit,
        onError: () -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(
            userCredentials.email,
            userCredentials.password,
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess.invoke(auth.currentUser?.email)
                } else {
                    // TODO("Обработать ошибки создания нового пользователя")
                    onError.invoke()
                }
            }
    }

    override suspend fun signIn(
        userCredentials: UserCredentials,
        onSuccess: (String?) -> Unit,
        onError: () -> Unit,
    ) {
        auth.signInWithEmailAndPassword(
            userCredentials.email,
            userCredentials.password,
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess.invoke(auth.currentUser?.email)
                } else {
                    // TODO("Обработать ошибки авторизации пользователя")
                    onError.invoke()
                }
            }
    }
}