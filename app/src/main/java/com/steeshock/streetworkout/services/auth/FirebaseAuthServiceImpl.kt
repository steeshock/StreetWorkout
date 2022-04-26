package com.steeshock.streetworkout.services.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.data.model.User

class FirebaseAuthServiceImpl : IAuthService {

    private var auth: FirebaseAuth = Firebase.auth

    override suspend fun isUserAuthorized(): Boolean {
        return auth.currentUser != null
    }

    override val currentUserEmail: String
        get() = auth.currentUser?.email ?: ""

    override val currentUserDisplayName: String
        get() = auth.currentUser?.displayName ?: ""

    override val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    override suspend fun signUp(
        userCredentials: UserCredentials,
        onSuccess: (User) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(
            userCredentials.email,
            userCredentials.password,
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess.invoke(
                        User(
                            userId = currentUserId,
                            displayName = currentUserDisplayName,
                            email = currentUserEmail,
                        )
                    )
                }
            }
            .addOnFailureListener {
                onError.invoke(it)
            }
    }

    override suspend fun signIn(
        userCredentials: UserCredentials,
        onSuccess: (User) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        auth.signInWithEmailAndPassword(
            userCredentials.email,
            userCredentials.password,
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess.invoke(
                        User(
                            userId = currentUserId,
                            displayName = currentUserDisplayName,
                            email = currentUserEmail,
                        )
                    )
                }
            }
            .addOnFailureListener {
                onError.invoke(it)
            }
    }

    override suspend fun signOut() {
        // TODO Сбрасывать список избранного при релогине
        auth.signOut()
    }
}