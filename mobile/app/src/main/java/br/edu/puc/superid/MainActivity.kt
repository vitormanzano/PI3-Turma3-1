package br.edu.puc.superid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.edu.puc.superid.ui.screens.LoginScreen
import br.edu.puc.superid.ui.screens.SignUpScreen
import br.edu.puc.superid.ui.screens.CreateAccountScreen
import android.provider.Settings
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.puc.superid.ui.screens.EditPasswordScreen
import br.edu.puc.superid.ui.screens.FirstTimeScreen
import br.edu.puc.superid.ui.screens.ForgotPasswordScreen
import br.edu.puc.superid.ui.screens.PasswordManagerScreen
import br.edu.puc.superid.ui.screens.PasswordsByCategoryScreen
import br.edu.puc.superid.ui.screens.ProfileScreen
import br.edu.puc.superid.ui.screens.QRCodeScannerScreen
import br.edu.puc.superid.ui.screens.SignUpCategoryScreen
import br.edu.puc.superid.ui.screens.SignUpPasswordScreen
import br.edu.puc.superid.ui.theme.SuperIdTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperIdTheme(dynamicColor = false) { // <- Aqui você força o uso das cores personalizadas
                val PREFS_NAME = "MyPrefsFile"
                val settings = getSharedPreferences(PREFS_NAME, 0)

                val isFirstTime = settings.getBoolean("my_first_time", true)
                if (isFirstTime) {
                    settings.edit().putBoolean("my_first_time", false).apply()
                }

                val startDestination = if (isFirstTime) "firstTime" else "signup"
                val androidId =
                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "mainScreen"
                ) {
                    composable("mainScreen") { PasswordManagerScreen(navController) }
                    composable("firstTime") { FirstTimeScreen(navController) }
                    composable("createAccount") { CreateAccountScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("perfil") { ProfileScreen(navController) }
                    composable("signup") { SignUpScreen(androidId, navController) }
                    composable("signuppassword") { SignUpPasswordScreen(navController) }
                    composable("signupcategory") { SignUpCategoryScreen(navController) }
                    composable("qrcode") { QRCodeScannerScreen(navController) }
                    composable("forgotPassword") { ForgotPasswordScreen(navController) }
                    composable("senhas/{categoria}") { backStackEntry ->
                        val categoria = backStackEntry.arguments?.getString("categoria") ?: ""
                        PasswordsByCategoryScreen(
                            categoryName = categoria,
                            navController = navController
                        )
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
                            categoriaInicial = backStackEntry.arguments?.getString("categoria")
                                ?: "",
                            senhaInicial = backStackEntry.arguments?.getString("senha") ?: "",
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

