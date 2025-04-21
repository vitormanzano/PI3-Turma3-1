package br.edu.puc.superid.database

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Base64
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

    fun cadastrarSenha(uid: String, login: String?, categoria: String, senha: String) {
        val accessToken = GerarAccessToken()

        val docId = db.collection("users")
            .whereEqualTo("UID", uid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val userDocRef = document.reference

                    // Agora criamos a subcoleção "senhas"
                    val senhaData = hashMapOf(
                        "login" to login,
                        "categoria" to categoria,
                        "senha" to senha,
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

    fun encrypt(senha: String, chave: String): String {
        val keyBytes = chave.toByteArray(Charsets.UTF_8)
        val secretKey = SecretKeySpec(keyBytes.copyOf(16), "AES")

        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val encryptedBytes = cipher.doFinal(senha.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun criptografarSenha(senha: String): String {
        val chaveParaCriptografar = "chaveExemplo123"

        val senhaCriptografada = encrypt(senha, chaveParaCriptografar)

        return senhaCriptografada
    }

    fun GerarAccessToken(length: Int = 256): String {
        val byteLength = (length * 6) / 8
        val randomBytes = ByteArray(byteLength)
        SecureRandom().nextBytes(randomBytes)
        return Base64.encodeToString(randomBytes, Base64.NO_WRAP).take(length)
    }
}



