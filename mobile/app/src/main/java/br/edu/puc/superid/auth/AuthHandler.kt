package br.edu.puc.superid.auth

import android.util.Log
import br.edu.puc.superid.database.FirestoreHandler
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthHandler {
    private val auth = Firebase.auth
    private val db = FirestoreHandler()

    fun login(email: String, senha: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("SUCCESS", "Login feito!")
                    onResult(true)
                } else {
                    Log.e("FAILURE", "Não foi possível fazer o login! ${task.exception}")
                    onResult(false)
                }
            }
    }

    fun cadastrarCredencial(
        nome: String,
        email: String,
        senha: String,
        imei: String,
        onResult: (Boolean, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SUCCESS", "Credencial criada!")
                    val uid = auth.currentUser!!.uid
                    db.cadastrarUsuario(nome, uid, imei)
                    onResult(true, "Conta criada com sucesso!")
                } else {
                    val exceptionAuth = task.exception
                    val errorMessage = when (exceptionAuth) {
                        is FirebaseAuthWeakPasswordException -> "Senha deve ter no mínimo 6 caracteres!"
                        is FirebaseAuthUserCollisionException -> "O e-mail já está em uso!"
                        is FirebaseAuthInvalidCredentialsException -> "E-mail inválido!"
                        else -> "Erro ao criar conta."
                    }
                    Log.w("FAILURE", "${task.exception}")
                    onResult(false, errorMessage)
                }
            }
    }

    fun obterUidUsuario(): String? {
        val user = auth.currentUser
        return user?.uid
    }
}