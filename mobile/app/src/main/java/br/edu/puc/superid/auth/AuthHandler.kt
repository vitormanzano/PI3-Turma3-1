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
        context: Context,
        nome: String,
        email: String,
        senha: String,
        imei: String,
        onResult: (Boolean, String) -> Unit
    )
    {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SUCCESS", "Credencial criada!")
                    val uid = auth.currentUser!!.uid

                    salvarUIDUsuario(context, uid)
                    salvarEmailUsuario(context, email)

                    db.cadastrarUsuario(nome, uid, imei)

                    val user = auth.currentUser
                    enviarEmailParaVerificacao(user!!)

                    onResult(true, "Conta criada com sucesso!")
                }
                else {
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

    fun obterUser(): FirebaseUser? {
        val user = auth.currentUser
        return user
    }

    fun deslogarUsuario() {
        auth.signOut()
        Log.i("AUTH", "Usuário deslogado com sucesso.")
    }

    fun enviarEmailParaVerificacao(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener { verificationTask ->
                if (verificationTask.isSuccessful) {
                    Log.i("VERIFICATION", "Email enviado")
                }

                else {
                    Log.e("VERIFICATION", "Email não enviado")
                }
            }
    }

     fun emailFoiVerificado(uid: String, user: FirebaseUser, context: Context, onResult: (Boolean) -> Unit) {
         user.reload().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val verificado = user.isEmailVerified

                val prefs = context.getSharedPreferences("MyPrefsFile", MODE_PRIVATE)
                prefs.edit().putBoolean("email_validado", verificado).apply()

                if (verificado) {
                    db.emailValidadoVerdadeiro(uid)
                }

                Log.d("EMAIL", if (verificado) "Email verificado!" else "Email ainda não verificado.")
                onResult(verificado)
            }
            else {
                Log.e("EMAIL", "Erro ao recarregar usuário: ${task.exception}")
                onResult(false)
            }
        }
    }

    fun enviarEmailParaRedefinirSenha(email: String) {
        auth.sendPasswordResetEmail(email).continueWith { task ->
            if (task.isCanceled) {
                Log.e("RecuperarSenha", "Não foi possível mandar a recuperação de senha")
            }
            else if (task.isSuccessful) {
                Log.e("RecuperarSenha", "recuperação de senha enviada com sucesso!")
            }
            else {
                Log.e("RecuperarSenha", "Algo deu errado: " + task.exception)
            }
        }
    }

    fun salvarUIDUsuario(context: Context, uid: String) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("userUid", uid).apply()
    }

    fun salvarEmailUsuario(context: Context, email: String) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("email", email).apply()
    }
}