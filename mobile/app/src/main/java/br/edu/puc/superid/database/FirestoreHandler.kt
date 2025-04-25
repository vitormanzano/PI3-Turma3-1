package br.edu.puc.superid.database

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Base64
import br.edu.puc.superid.Models.Senha
import br.edu.puc.superid.auth.AuthHandler
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom
import java.util.UUID


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
                    val user = FirebaseAuth.getInstance().currentUser

                    val userUid = user!!.uid
                    inserirCategoriasIniciais(userUid)
                }
                else {
                    Log.w("FAILURE", "${task.exception}")
                }
            }
    }

    fun criptografia(text: String, key: String): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key.toByteArray(), "AES"))
        return cipher.doFinal(text.toByteArray())
    }

    fun descriptografia(encryptedData: ByteArray, key: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key.toByteArray(), "AES"))
        return String(cipher.doFinal(encryptedData))
    }

    fun criptografarSenha(senha: String): String {
        val chaveParaCriptografar = "chaveExemplo1234"

        val senhaCriptografada = criptografia(senha, chaveParaCriptografar)

        return senhaCriptografada.toString()
    }

    fun gerarAccessToken(length: Int = 256): String {
        val byteLength = (length * 6) / 8
        val randomBytes = ByteArray(byteLength)
        SecureRandom().nextBytes(randomBytes)
        return Base64.encodeToString(randomBytes, Base64.NO_WRAP).take(length)
    }

    fun gerarGuid(): String {
        return UUID.randomUUID().toString()
    }

    fun cadastrarSenha(login: String?, categoria: String, senha: String) {
        val guid = gerarGuid()

        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user!!.uid

        val accessToken = gerarAccessToken()
        val senhaCriptografada = criptografarSenha(senha)

        val senhaData = hashMapOf(
            "guid" to guid,
            "login" to login,
            "nomeCategoria" to categoria,
            "senha" to senhaCriptografada,
            "accessToken" to accessToken,
            "uidUsuario" to userUid
        )

        db.collection("senhas")
            .add(senhaData)
            .addOnSuccessListener {
                println("Senha cadastrada com sucesso!")
            }
            .addOnFailureListener { e ->
                println("Erro ao cadastrar senha: $e")
            }
    }

    fun deletarSenha(guid: String) {
        var senha = db
            .collection("senhas")
            .whereEqualTo("guid", guid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    db.collection("senhas").document(document.id)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("FIREBASE", "Deletou")
                        }
                        .addOnFailureListener { e ->
                            Log.e("FIREABSE", "Erro ao deletar")
                        }

                }
                else {
                    Log.e("FIREBASE", "SENHA NÃO encontrada")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE", "Erro ao buscar documento  ", e)
            }
    }

    fun buscarTodasAsSenhas(userUid: String): List<Senha> {
        var listaDeSenhas: MutableList<Senha> = mutableListOf()

        db.collection("senhas")
            .whereEqualTo("uidUsuario", userUid)
            .get()
            .addOnCompleteListener { task ->
                task.addOnSuccessListener { documents ->
                    for (document in documents) {
                        val guid = document.getString("guid").toString()
                        val login = document.getString("login").toString()
                        val nomeCategoria = document.getString("nomeCategoria").toString()
                        val senha = document.getString("senha").toString()
                        val accessToken = document.getString("accessToken").toString()
                        val uidUsuario = document.getString("uidUsuario").toString()

                        var senhaData = Senha(guid, login, nomeCategoria, senha, accessToken, uidUsuario)

                        listaDeSenhas.add(senhaData)
                    }
                }
                task.addOnFailureListener { e ->
                    Log.e("FAILURE", "${e.message}")
                }
            }
        return listaDeSenhas
    }

    fun buscarSenhaPorCategoria(categoria: String, userUid: String): List<Senha> {
        var listaDeSenhas: MutableList<Senha> = mutableListOf()

        db.collection("senhas")
            .whereEqualTo("uidUsuario", userUid)
            .whereEqualTo("nomeCategoria", categoria)
            .get()
            .addOnCompleteListener { task ->
                task.addOnSuccessListener { documents ->
                    for (document in documents) {
                        val guid = document.getString("guid").toString()
                        val login = document.getString("login").toString()
                        val nomeCategoria = document.getString("nomeCategoria").toString()
                        val senha = document.getString("senha").toString()
                        val accessToken = document.getString("accessToken").toString()
                        val uidUsuario = document.getString("uidUsuario").toString()

                        var senhaData = Senha(guid, login, nomeCategoria, senha, accessToken, uidUsuario)

                        listaDeSenhas.add(senhaData)
                    }
                }
                task.addOnFailureListener { e ->
                    Log.e("FAILURE", "${e.message}")
                }
            }

        return listaDeSenhas

    }

    suspend fun buscarTodasCategorias(): List<String> {
        val listaDeCategorias = mutableListOf<String>()
        val auth = AuthHandler()
        val userUid = auth.obterUidUsuario()

        try {
            val documentos = db.collection("categorias")
                .whereEqualTo("uidUsuario", userUid)
                .get()
                .await()

            for (document in documentos) {
                val nomeCategoria = document.getString("nome") ?: continue
                listaDeCategorias.add(nomeCategoria)
            }

            Log.i("CATEGORIAS", listaDeCategorias.toString())
        } catch (e: Exception) {
            Log.e("CATEGORIAS", "Erro ao buscar categorias", e)
        }

        return listaDeCategorias
    }


    fun inserirCategoriasIniciais(userUid: String) {
        val categoriaSitesWeb = "Sites da Web"
        val categoriaAplicativos = "Aplicativos"
        val categoriaTeclado = "Teclados de Acesso Físico"

        val docSitesWeb = hashMapOf(
            "nome" to categoriaSitesWeb,
            "uidUsuario" to userUid
        )

        val docAplicativos = hashMapOf(
            "nome" to categoriaAplicativos,
            "uidUsuario" to userUid
        )

        val docTeclado = hashMapOf(
            "nome" to categoriaTeclado,
            "uidUsuario" to userUid
        )

        db.collection("categorias")
            .add(docSitesWeb)

        db.collection("categorias")
            .add(docAplicativos)

        db.collection("categorias")
            .add(docTeclado)

    }

}