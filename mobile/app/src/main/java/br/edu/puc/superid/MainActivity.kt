package br.edu.puc.superid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.edu.puc.superid.ui.screens.LoginScreen
import br.edu.puc.superid.ui.theme.SuperIdTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperIdTheme {
                LoginScreen(
                    onLoginSuccess = {
                        // TO DO navegar para outra tela quando o login for concluido com sucesso
//                        val PREFS_NAME = "MyPrefsFile"
//
//                        val settings = getSharedPreferences(PREFS_NAME, 0)
//
//                        if (settings.getBoolean("my_first_time", true)) {
//                            //the app is being launched for first time, do something
//                            Log.d("Comments", "First time")
//
//                            // first time task
//
//                            // record the fact that the app has been started at least once
//                            settings.edit().putBoolean("my_first_time", false).commit()
//                        } -> Verifica se é a primeira vez que ele entra no app
                    }

                )
            }
        }
    }
}
