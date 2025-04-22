package br.edu.puc.superid.database

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Base64
import com.google.firebase.auth.FirebaseAuth
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

class FirestoreHandler {
    private val db = Firebase.firestore

    fun cadastrarUsuario(nome: String, uid: String, imei: String) {
        val novoDocUsuario = hashMapOf(
            "Nome" to nome,
            "UID" to uid,
            "IMEI" to imei
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

    fun cadastrarSenha(login: String?, categoria: String, senha: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user!!.uid

        val accessToken = gerarAccessToken()
        var senhaCriptografada = criptografarSenha(senha)

        db.collection("users")
            .whereEqualTo("UID", uid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val userDocRef = document.reference

                    val senhaData = hashMapOf(
                        "login" to login,
                        "categoria" to categoria,
                        "senha" to senhaCriptografada,
                        "accessToken" to accessToken
                    )

                    userDocRef.collection("senhas")
                        .add(senhaData)
                        .addOnSuccessListener {
                            println("Senha cadastrada com sucesso!")
                        }
                        .addOnFailureListener { e ->
                            println("Erro ao cadastrar senha: $e")
                        }
                } else {
                    println("Usuário com UID $uid não encontrado.")
                }
            }
    }

    fun criptografia(text: String, key: String): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key.toByteArray(), "AES"))
        return cipher.doFinal(text.toByteArray())
    }

    fun criptografarSenha(senha: String): String {
        val chaveParaCriptografar = "chaveExemplo123"

        val senhaCriptografada = criptografia(senha, chaveParaCriptografar)

        return senhaCriptografada.toString()
    }

    fun gerarAccessToken(length: Int = 256): String {
        val byteLength = (length * 6) / 8
        val randomBytes = ByteArray(byteLength)
        SecureRandom().nextBytes(randomBytes)
        return Base64.encodeToString(randomBytes, Base64.NO_WRAP).take(length)
    }
}



