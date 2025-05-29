package br.edu.puc.superid.auth

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import br.edu.puc.superid.database.FirestoreHandler
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.functions.ktx.functions

class AuthHandler {
    private val auth = Firebase.auth
    private val db = FirestoreHandler()

    //Usa a função do auth para realizar o login so user
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

    //Cadastra a credencial do user
    fun cadastrarCredencial(
        context: Context,
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

                    salvarUIDUsuario(context, uid) //Salva o uid e email nas prefs
                    salvarEmailUsuario(context, email)

                    db.cadastrarUsuario(nome, uid, imei)

                    val user = auth.currentUser
                    enviarEmailParaVerificacao( //Envia o email para validar o email
                        user = user!!,
                        onSuccess = {
                            Log.i("EMAIL", "Verificação enviada com sucesso após cadastro.")
                        },
                        onFailure = {
                            Log.e("EMAIL", "Falha ao enviar verificação após cadastro.")
                        }
                    )
                    onResult(true, "Conta criada com sucesso!")
                } else {
                    val exceptionAuth = task.exception
                    val errorMessage = when (exceptionAuth) { //Erros que podem ocorrer por causa das regras do auth
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

    //Obtem o user, o tipo FirebaseUser
    fun obterUser(): FirebaseUser? {
        val user = auth.currentUser
        return user
    }

    fun deslogarUsuario() {
        auth.signOut()
        Log.i("AUTH", "Usuário deslogado com sucesso.")
    }

    //Envia email para validar o email
    fun enviarEmailParaVerificacao(
        user: FirebaseUser,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        user.sendEmailVerification()
            .addOnCompleteListener { verificationTask ->
                if (verificationTask.isSuccessful) {
                    Log.i("VERIFICATION", "Email enviado")
                    onSuccess()
                } else {
                    Log.e("VERIFICATION", "Email não enviado")
                    onFailure()
                }
            }
    }

    //Verifica se o email foi validado
    fun emailFoiVerificado(email: String, context: Context, onResult: (Boolean) -> Unit) {
        val functions = Firebase.functions
        val data = hashMapOf("email" to email)

        functions.getHttpsCallable("getUserByEmail")
            .call(data)
            .addOnSuccessListener { result ->
                val res = result.data as Map<*, *>
                val uid = res["uid"] as String

                val verificado = res["emailVerified"] as Boolean
                
                val prefs = context.getSharedPreferences("MyPrefsFile", MODE_PRIVATE)
                prefs.edit().putBoolean("email_validado", verificado).apply()

                Log.d("EMAIL", if (verificado) "Email verificado!" else "Email ainda não verificado.")

                onResult(verificado)
            }


            .addOnFailureListener { e ->
                Log.e("EMAIL", "Erro ao buscar usuário: ${e.message}")
                onResult(false)
            }
    }

    fun enviarEmailParaRedefinirSenha(email: String) {
        auth.sendPasswordResetEmail(email).continueWith { task ->
            if (task.isCanceled) {
                Log.e("RecuperarSenha", "Não foi possível mandar a recuperação de senha")
            } else if (task.isSuccessful) {
                Log.e("RecuperarSenha", "recuperação de senha enviada com sucesso!")
            } else {
                Log.e("RecuperarSenha", "Algo deu errado: " + task.exception)
            }
        }
    }

    //Salva o uid do user nas prefs
    fun salvarUIDUsuario(context: Context, uid: String) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("userUid", uid).apply()
    }

    //Salva o email do user nas prefs
    fun salvarEmailUsuario(context: Context, email: String) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("email", email).apply()
    }

    //Salva o estado atual do email (verificado ou não)
    fun salvarEstadoEmail(context: Context, verificado: Boolean) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("emailVerificado", verificado).apply()
        Log.i("EMAIL", verificado.toString())
    }

    //Obtem o estado do email atual
    fun estadoDoEmail(context: Context) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val verificado = prefs.getBoolean("emailVerificado", false)
        Log.i("Email", verificado.toString())
    }

}