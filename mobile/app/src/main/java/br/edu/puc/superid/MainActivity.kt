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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.puc.superid.ui.screens.EditPasswordScreen
import br.edu.puc.superid.ui.screens.PasswordManagerScreen
import br.edu.puc.superid.ui.screens.PasswordsByCategoryScreen
import br.edu.puc.superid.ui.screens.SignUpCategoryScreen
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
                  startDestination = "FirstTime"
              ) {
                  composable("mainScreen") { PasswordManagerScreen(navController) }
                  composable("firstTime") { FirstTimeScreen(navController) }
                  composable("createAccount") { CreateAccountScreen(navController) }
                  composable("login") { LoginScreen(navController) }
                  composable("signup") { SignUpScreen(androidId, navController) }
                  composable("signuppassword") { SignUpPasswordScreen(navController) }
                  composable("signupcategory") { SignUpCategoryScreen(navController) }
                  composable("senhas/{categoria}") { backStackEntry ->
                      val categoria = backStackEntry.arguments?.getString("categoria") ?: ""
                      PasswordsByCategoryScreen(categoryName = categoria, navController = navController)
                  }
                  composable(
                      "editar_senha/{guid}/{nome}/{login}/{categoria}/{senha}",
                      arguments = listOf(
                          navArgument("guid") { type = NavType.StringType },
                          navArgument("nome") { type = NavType.StringType },
                          navArgument("login") { type = NavType.StringType },
                          navArgument("categoria") { type = NavType.StringType },
                          navArgument("senha") { type = NavType.StringType }
                      )
                  ) { backStackEntry ->
                      EditPasswordScreen(
                          guid = backStackEntry.arguments?.getString("guid") ?: "",
                          nomeInicial = backStackEntry.arguments?.getString("nome") ?: "",
                          loginInicial = backStackEntry.arguments?.getString("login") ?: "",
                          categoriaInicial = backStackEntry.arguments?.getString("categoria") ?: "",
                          senhaInicial = backStackEntry.arguments?.getString("senha") ?: "",
                          navController = navController
                      )
                  }

              }
        }
    }
}

