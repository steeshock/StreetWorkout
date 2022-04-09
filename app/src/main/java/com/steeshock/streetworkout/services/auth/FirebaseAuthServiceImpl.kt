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

    override suspend fun getUserEmail(): String? {
        return auth.currentUser?.email
    }

    override suspend fun getDisplayName(): String? {
        return auth.currentUser?.displayName
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun signUp(
        userCredentials: UserCredentials,
        onSuccess: (User?) -> Unit,
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
                            displayName = auth.currentUser?.displayName,
                            email = auth.currentUser?.email,
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
        onSuccess: (User?) -> Unit,
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
                            displayName = auth.currentUser?.displayName,
                            email = auth.currentUser?.email,
                        )
                    )
                }
            }
            .addOnFailureListener {
                onError.invoke(it)
            }
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}