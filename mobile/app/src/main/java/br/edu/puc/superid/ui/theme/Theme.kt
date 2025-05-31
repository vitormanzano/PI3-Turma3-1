package br.edu.puc.superid.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Cores personalizadas para o tema escuro
private val CustomDarkColorScheme = darkColorScheme(
    primary = Color(0xFF3366FF),      // Azul para ícones e botões
    onPrimary = Color.White,          // Texto sobre elementos primários
    background = Color.Black,         // Fundo principal
    onBackground = Color.White,       // Texto sobre o fundo
    surface = Color.DarkGray,         // Cartões e superfícies
    onSurface = Color.LightGray       // Texto sobre a superfície
)

// Cores padrão para o tema claro (você pode ajustar se quiser)
private val CustomLightColorScheme = lightColorScheme(
    primary = Color(0xFF3366FF),
    onPrimary = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.LightGray,         // Cartões e superfícies
    onSurface = Color.DarkGray       // Texto sobre a superfície
)

@Composable
fun SuperIdTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> CustomDarkColorScheme
        else -> CustomLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
