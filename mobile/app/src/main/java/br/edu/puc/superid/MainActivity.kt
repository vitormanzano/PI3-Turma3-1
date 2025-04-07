package br.edu.puc.superid

import android.os.Bundle
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
                    }
                )
            }
        }
    }
}
