package br.edu.puc.superid

import FirstTimeScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.edu.puc.superid.ui.screens.LoginScreen
import br.edu.puc.superid.ui.screens.SignUpScreen
import br.edu.puc.superid.ui.theme.SuperIdTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import android.provider.Settings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val PREFS_NAME = "MyPrefsFile"

            val settings = getSharedPreferences(PREFS_NAME, 0)

            if (settings.getBoolean("my_first_time", true)) {
                //the app is being launched for first time, do something

                FirstTimeScreen()

                // first time task

                // record the fact that the app has been started at least once
                settings.edit().putBoolean("my_first_time", false).commit()
            }
            else {
                val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                SignUpScreen(androidId)
            }
        }
    }
}

