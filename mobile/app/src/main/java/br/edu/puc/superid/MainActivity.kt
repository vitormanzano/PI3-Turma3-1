package br.edu.puc.superid

import FirstTimeScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.edu.puc.superid.ui.screens.LoginScreen
import br.edu.puc.superid.ui.screens.SignUpScreen
import br.edu.puc.superid.ui.screens.MainScreen
import br.edu.puc.superid.ui.screens.CreateAccountScreen
import br.edu.puc.superid.ui.theme.SuperIdTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import android.provider.Settings
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.puc.superid.ui.screens.SignUpPasswordScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
              val PREFS_NAME = "MyPrefsFile"
              val settings = getSharedPreferences(PREFS_NAME, 0)

              // Verifica se é a primeira vez
              val isFirstTime = settings.getBoolean("my_first_time", true)

              // Se for a primeira vez, salva que o app já foi iniciado
              if (isFirstTime) {
                  settings.edit().putBoolean("my_first_time", false).apply()
              }

              // Define o startDestination com base na verificação
              val startDestination = if (isFirstTime) "firstTime" else "signup"

              // Se necessário, obtém o Android ID
              val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

              val navController = rememberNavController()

              NavHost(
                  navController = navController,
                  startDestination = "firstTime"
              ) {
                  composable("firstTime") { FirstTimeScreen(navController) }
                  composable("createAccount") { CreateAccountScreen(navController) }
                  composable("login") { LoginScreen(navController) }
                  composable("signup") { SignUpScreen(androidId, navController) }
                  composable("mainscreen") { MainScreen(navController) }
                  composable("signuppassword") { SignUpPasswordScreen(navController) }
              }
        }
    }
}

