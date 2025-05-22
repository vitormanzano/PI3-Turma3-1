package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import br.edu.puc.superid.auth.AuthHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import br.edu.puc.superid.R

@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var dialogIcon by remember { mutableStateOf(Icons.Default.Check) }
    var dialogIconColor by remember { mutableStateOf(Color(0xFF4CAF50)) }

    val auth = AuthHandler()
    val iconsColor = Color(0xFF3366FF)
    val emailValid =
        email.contains("@") && Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email)

    fun sendResetPasswordEmail() {
        auth.enviarEmailParaRedefinirSenha(email)
        dialogTitle = "Verifique seu e-mail"
        dialogMessage = "Se o e-mail informado estiver cadastrado, você receberá um link para redefinir sua senha."
        dialogIcon = Icons.Default.Check
        dialogIconColor = Color(0xFF4CAF50)
        showDialog = true
    }

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Ícone voltar
                Row(
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .clickable { navController.popBackStack() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Conteúdo centralizado verticalmente
                Column(
                    modifier = Modifier
                        .padding(bottom = 109.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        tint = iconsColor,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Recuperar Senha",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Digite seu e-mail cadastrado. Enviaremos um link para redefinir sua senha.",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("E-mail", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Email,
                                contentDescription = null,
                                tint = iconsColor
                            )
                        },
                        trailingIcon = {
                            if (emailValid) Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.Green
                            )
                        },
                        textStyle = TextStyle(color = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconsColor,
                            unfocusedBorderColor = Color.DarkGray,
                            cursorColor = iconsColor
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { sendResetPasswordEmail() },
                        enabled = emailValid,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (emailValid) iconsColor else Color.DarkGray,
                            disabledContainerColor = Color.DarkGray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Enviar e-mail", color = Color.White, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Voltar para o login",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { navController.navigate("login") }
                    )
                }

                // Diálogo opcional (pode estar fora da Column também)
                if (showDialog) {
                    CustomDialog(
                        title = dialogTitle,
                        message = dialogMessage,
                        icon = dialogIcon,
                        iconColor = dialogIconColor,
                        onConfirm = { showDialog = false },
                        onDismiss = { showDialog = false }
                    )
                }
            }
        }
    }
}
