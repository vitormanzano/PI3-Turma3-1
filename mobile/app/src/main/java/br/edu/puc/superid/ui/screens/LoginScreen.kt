package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.edu.puc.superid.auth.AuthHandler

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var senhaError by remember { mutableStateOf<String?>(null) }
    var wasAttempted by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFF102952)
    val iconsColor = Color(0xFF00D7FF)
    val buttonColor = Color(0xFF00D7FF)

    fun validateFields(): Boolean {
        emailError = when {
            email.isBlank() -> "Email não pode ser vazio."
            !email.contains("@") -> "Email inválido."
            else -> null
        }
        senhaError = if (senha.isBlank()) "Senha não pode ser vazia." else null
        return emailError == null && senhaError == null
    }

    fun tryLogin() {
        wasAttempted = true
        if (validateFields()) {
            val auth = AuthHandler()
            auth.login(email, senha)
            navController.navigate("mainscreen")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Lock Icon",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Super ID",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("E-mail", color = Color.Gray) },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Email, contentDescription = null, tint = iconsColor)
                },
                textStyle = TextStyle(color = Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                isError = wasAttempted && emailError != null,
                supportingText = {
                    if (wasAttempted && emailError != null) Text(emailError!!, color = Color.Red)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = iconsColor,
                    unfocusedBorderColor = Color.DarkGray,
                    cursorColor = iconsColor
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                placeholder = { Text("Senha mestra", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = iconsColor)
                },
                textStyle = TextStyle(color = Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                isError = wasAttempted && senhaError != null,
                supportingText = {
                    if (wasAttempted && senhaError != null) Text(senhaError!!, color = Color.Red)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = iconsColor,
                    unfocusedBorderColor = Color.DarkGray,
                    cursorColor = iconsColor
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { tryLogin() },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text("ENTRAR", fontWeight = FontWeight.Bold, color = backgroundColor)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ainda não possui uma conta?",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.clickable {
                    navController.navigate("signup")
                }
            )
        }
    }
}
