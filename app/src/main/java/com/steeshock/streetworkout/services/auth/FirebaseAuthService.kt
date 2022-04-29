package com.steeshock.streetworkout.services.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.data.model.User
import com.steeshock.streetworkout.services.auth.IAuthService.SignPurpose
import com.steeshock.streetworkout.services.auth.IAuthService.SignPurpose.SIGN_IN
import com.steeshock.streetworkout.services.auth.IAuthService.SignPurpose.SIGN_UP
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthServiceImpl : IAuthService {

    private var auth: FirebaseAuth = Firebase.auth

    override val isUserAuthorized: Boolean
        get() = auth.currentUser != null

    override val currentUserEmail: String
        get() = auth.currentUser?.email ?: ""

    override val currentUserDisplayName: String
        get() = auth.currentUser?.displayName ?: ""

    override val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    override suspend fun sign(
        userCredentials: UserCredentials,
        signPurpose: SignPurpose,
    ): User {
        return suspendCoroutine { continuation ->
            when (signPurpose) {
                SIGN_UP -> {
                    auth.createUserWithEmailAndPassword(
                        userCredentials.email,
                        userCredentials.password,
                    )
                }
                SIGN_IN -> {
                    auth.signInWithEmailAndPassword(
                        userCredentials.email,
                        userCredentials.password,
                    )
                }
            }.addOnSuccessListener {
                val user = User(
                    userId = currentUserId,
                    displayName = currentUserDisplayName,
                    email = currentUserEmail,
                )
                continuation.resume(user)
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}