package br.edu.puc.superid.auth

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthHandler {
    private val auth = Firebase.auth

    fun login(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener{ task ->
                task.addOnFailureListener { e ->
                    Log.e("FAILURE", "Não foi possível fazer o login!")
                }
                task .addOnSuccessListener {
                    Log.i("SUCCESS", "Login feito!")
                }
            }
    }
}