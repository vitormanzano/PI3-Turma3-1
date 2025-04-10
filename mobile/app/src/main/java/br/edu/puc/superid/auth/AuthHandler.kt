package br.edu.puc.superid.auth

import android.util.Log
import br.edu.puc.superid.database.FirestoreHandler
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthHandler {
    private val auth = Firebase.auth
    private val db = FirestoreHandler()

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

    fun cadastrarCredencial(nome: String, email: String, senha: String, imei: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SUCCESS", "Credencial criado!")
                    val uid = auth.currentUser!!.uid

                    db.cadastrarUsuario(nome, uid, imei)
                }
                else {
                    val exceptionAuth = task.exception

                    when (exceptionAuth) {
                        is FirebaseAuthWeakPasswordException -> Log.e("FAILURE", "Senha deve ter no mínimo 6 caracteres!")
                        is FirebaseAuthInvalidCredentialsException -> Log.e("FAILURE", "Email mal formatado")
                    }

                    Log.w("FAILURE", "${task.exception}")
                }
            }
    }
}