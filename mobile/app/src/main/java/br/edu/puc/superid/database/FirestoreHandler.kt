package br.edu.puc.superid.database

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreHandler {
    private val db = Firebase.firestore
    //Falta colocar IMEI aqui
    fun cadastrarUsuario(nome: String, uid: String) {
        val novoDocUsuario = hashMapOf(
            "Nome" to nome,
            "UID" to uid
        )

        db.collection("users").add(novoDocUsuario)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SUCCESS", "Usuário criado!")
                }
                else {
                    Log.w("FAILURE", "${task.exception}")
                }
            }
    }
}

