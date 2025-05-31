package br.edu.puc.superid.database

import android.os.Handler
import android.os.Looper
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import br.edu.puc.superid.auth.AuthHandler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.KeyStore
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

//Molde para a senha
data class Senha (
    var descricao: String,
    var guid: String,
    var login: String,
    var nomeCategoria: String,
    var senha: String,
    var accessToken: String,
    var uidUsuario: String
)

class FirestoreHandler {
    private val db = Firebase.firestore

    //Cadastra campos do usuário no
    fun cadastrarUsuario(nome: String, uid: String, imei: String) {
        val novoDocUsuario = hashMapOf(
            "Nome" to nome,
            "UID" to uid,
            "IMEI" to imei,
            "emailFoiVerificado" to false
        )

        db.collection("users").add(novoDocUsuario)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SUCCESS", "Usuário criado!")
                    val user = FirebaseAuth.getInstance().currentUser
                    val userUid = user!!.uid
                    inserirCategoriasIniciais(userUid) //Coloca as 3 categorias iniciais para o user
                } else {
                    Log.w("FAILURE", "${task.exception}")
                }
            }
    }

    //Coloca o email como validado
    fun emailValidadoVerdadeiro(uid: String) {
        db.collection("users")
            .whereEqualTo("UID", uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("users")
                        .document(document.id)
                        .update("emailFoiVerificado", true)
                        .addOnSuccessListener {
                            Log.d("SUCCESS", "Email verificado atualizado para true.")
                        }
                        .addOnFailureListener { e ->
                            Log.w("FAILURE", "Erro ao atualizar email verificado", e)
                        }
                }

                if (documents.isEmpty) {
                    Log.w("NOT FOUND", "Nenhum usuário encontrado com UID: $uid")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FAILURE", "Erro ao buscar usuário", exception)
            }
    }

    fun generateRandomIV(): ByteArray {
        return Random.Default.nextBytes(16)
    }

    //Função de criptografar a senha
    fun criptografar(text: String, key: String): String {
        val keyBytes = key.toByteArray().copyOf(16)
        val secretKey = SecretKeySpec(keyBytes, "AES")
        val iv = generateRandomIV()
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

        val encrypted = cipher.doFinal(text.toByteArray())
        val encryptedWithIV = iv + encrypted

        return Base64.encodeToString(encryptedWithIV, Base64.DEFAULT)
    }

    //Função de descriptografar a senha
    fun descriptografar(encryptedBase64: String, key: String): String {
        val encryptedWithIV = Base64.decode(encryptedBase64, Base64.DEFAULT)
        val iv = encryptedWithIV.sliceArray(0 until 16)
        val encrypted = encryptedWithIV.sliceArray(16 until encryptedWithIV.size)

        val keyBytes = key.toByteArray().copyOf(16)
        val secretKey = SecretKeySpec(keyBytes, "AES")
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

        return String(cipher.doFinal(encrypted))
    }

    //Chama a função para descriptografar
    fun descriptografarSenha(senha: String): String {
        val chaveParaCriptografar = "chaveExemplo1234"
        return descriptografar(senha, chaveParaCriptografar)
    }

    //Chama a funçào de criptografar
    fun criptografarSenha(senha: String): String {
        val chaveParaCriptografar = "chaveExemplo1234"
        return criptografar(senha, chaveParaCriptografar)
    }

    //Gera accessToken do usuário
    fun gerarAccessToken(length: Int = 256): String {
        val byteLength = (length * 6) / 8
        val randomBytes = ByteArray(byteLength)
        SecureRandom().nextBytes(randomBytes)
        return Base64.encodeToString(randomBytes, Base64.NO_WRAP).take(length)
    }

    fun gerarGuid(): String {
        return UUID.randomUUID().toString()
    }

    //Cadastra a senha do user
    fun cadastrarSenha(descricao: String, login: String?, categoria: String, senha: String) {
        val guid = gerarGuid()
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user!!.uid

        val accessToken = gerarAccessToken()
        val senhaCriptografada = criptografarSenha(senha)

        val senhaData = hashMapOf(
            "descricao" to descricao,
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

    //Troca o campo access token da senha
    fun trocarAccessTokenDaSenha() {
        
    }

    //Altera senha do user
    fun alterarSenha(guid: String, descricao: String, login: String, nomeCategoria: String, senha: String) {
        db.collection("senhas")
            .whereEqualTo("guid", guid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val novosDados = hashMapOf<String, Any>(
                        "descricao" to descricao,
                        "login" to login,
                        "nomeCategoria" to nomeCategoria,
                        "senha" to criptografarSenha(senha)
                    )

                    val document = documents.documents[0]
                    db.collection("senhas").document(document.id)
                        .update(novosDados)
                        .addOnSuccessListener {
                            Log.d("FIRESTORE", "Senha alterada")
                        }
                        .addOnFailureListener {
                            Log.e("FIRESTORE", "Erro ao alterar")
                        }
                } else {
                    Log.e("FIRESTORE", "SENHA NÃO encontrada")
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE", "Não foi possível alterar a senha")
            }
    }

    //Deleta senha do user
    fun deletarSenha(guid: String, onComplete: (Boolean) -> Unit) {
        db.collection("senhas")
            .whereEqualTo("guid", guid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val docRef = db.collection("senhas").document(document.id)
                    val deleteTask = docRef.delete()

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (!deleteTask.isComplete) {
                            Log.w("FIREBASE", "Timeout no delete, assumindo sucesso local")
                            onComplete(true)
                        }
                    }, 2000)

                    deleteTask
                        .addOnSuccessListener {
                            Log.d("FIREBASE", "Senha deletada com sucesso")
                            onComplete(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("FIREBASE", "Erro ao deletar senha", e)
                            onComplete(false)
                        }
                } else {
                    Log.e("FIREBASE", "Senha não encontrada")
                    onComplete(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE", "Erro ao buscar senha", e)
                onComplete(false)
            } }

    //Pega as senhas pelas categorias
    suspend fun buscarSenhasPorCategoria(categoria: String): List<Senha> {
        val listaDeSenhas = mutableListOf<Senha>()
        val auth = AuthHandler()
        val userUid = auth.obterUidUsuario()

        try {
            val documentos = db.collection("senhas")
                .whereEqualTo("uidUsuario", userUid)
                .whereEqualTo("nomeCategoria", categoria)
                .get()
                .await()

            for (document in documentos) { //Pega os valores das senhas
                val descricao = document.getString("descricao").orEmpty()
                val guid = document.getString("guid").orEmpty()
                val login = document.getString("login").orEmpty()
                val nomeCategoria = document.getString("nomeCategoria").orEmpty()
                val senha = document.getString("senha").orEmpty()
                val senhaDescriptografada = descriptografarSenha(senha)
                val accessToken = document.getString("accessToken").orEmpty()
                val uidUsuario = document.getString("uidUsuario").orEmpty()

                listaDeSenhas.add(Senha(descricao, guid, login, nomeCategoria, senhaDescriptografada, accessToken, uidUsuario))
            }
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Erro ao buscar senhas: ${e.message}")
        }

        return listaDeSenhas
    }

    //Pega a quantidade de senhas que tem na categoria
    suspend fun quantidadeDeSenhasPorCategoria(categoria: String): Int {
        val listaDeSenhas = buscarSenhasPorCategoria(categoria)

        return listaDeSenhas.size;
    }

    //Pega as categorias cadastradas
    suspend fun buscarTodasCategorias(): List<String> {
        val listaDeCategorias = mutableListOf<String>()
        val auth = AuthHandler()
        val userUid = auth.obterUidUsuario()

        try {
            val documentos = db.collection("categorias")
                .whereEqualTo("uidUsuario", userUid)
                .get()
                .await()

            for (document in documentos) { //Pega todos as categorias e coloca na lista
                val nomeCategoria = document.getString("nome") ?: continue
                listaDeCategorias.add(nomeCategoria)
            }

            Log.i("CATEGORIAS", listaDeCategorias.toString())
        } catch (e: Exception) {
            Log.e("CATEGORIAS", "Erro ao buscar categorias", e)
        }

        return listaDeCategorias
    }

    fun criarCategoria(nomeCategoria: String) {
        val auth = AuthHandler()
        val userUid = auth.obterUidUsuario()

        db.collection("categorias")
            .whereEqualTo("uidUsuario", userUid)
            .whereEqualTo("nome", nomeCategoria)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) { //Verifica se a categoria ainda não existe
                    val docCategoria = hashMapOf(
                        "nome" to nomeCategoria,
                        "uidUsuario" to userUid
                    )

                    db.collection("categorias")
                        .add(docCategoria)

                    Log.i("FIRESTORE", "Categoria criada")
                } else {
                    Log.e("FIRESTORE", "Já existe essa categoria")
                }
            }
            .addOnFailureListener {
                Log.e("FIREBASE", "Erro ao criar categoria")
            }
    }

    fun deletarCategoria(userUid: String, nomeCategoria: String, onComplete: (Boolean) -> Unit) {
        db.collection("categorias")
            .whereEqualTo("uidUsuario", userUid)
            .whereEqualTo("nome", nomeCategoria)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) { //Verifica se existe a categoria
                    val document = documents.documents[0]

                    CoroutineScope(Dispatchers.IO).launch {
                        val senhasNaCategoria = buscarSenhasPorCategoria(nomeCategoria)
                        if (senhasNaCategoria.isEmpty()) { //Se tiver senhas na categoria, não deixa excluir
                            db.collection("categorias").document(document.id)
                                .delete()
                                .addOnSuccessListener {
                                    Log.d("FIREBASE", "Categoria deletada com sucesso")
                                    onComplete(true)
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FIREBASE", "Erro ao deletar categoria", e)
                                    onComplete(false)
                                }
                        } else {
                            Log.e("FIREBASE", "Existem senhas na categoria")
                            onComplete(false)
                        }
                    }
                } else {
                    Log.e("FIREBASE", "Categoria NÃO encontrada")
                    onComplete(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE", "Erro ao buscar documento", e)
                onComplete(false)
            }
    }

    fun inserirCategoriasIniciais(userUid: String) { //Coloca as 3 primeiras categorias
        val categorias = listOf("Sites da Web", "Aplicativos", "Teclados de Acesso Físico")
        categorias.forEach { nome ->
            val docCategoria = hashMapOf(
                "nome" to nome,
                "uidUsuario" to userUid
            )
            db.collection("categorias").add(docCategoria)
        }
    }

    fun obterNomeUsuario(onResult: (String) -> Unit) { //Pega o nome do user
        val auth = AuthHandler()
        val userUid = auth.obterUidUsuario()

        db.collection("users")
            .whereEqualTo("UID", userUid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val nome = document.getString("Nome") ?: ""
                    onResult(nome)
                } else {
                    onResult("") // Se não encontrar
                }
            }
            .addOnFailureListener {
                onResult("") // Em caso de erro
            }
    }
}