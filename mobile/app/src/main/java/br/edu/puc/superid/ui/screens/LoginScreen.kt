package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.edu.puc.superid.R
import br.edu.puc.superid.auth.AuthHandler
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var senhaError by remember { mutableStateOf<String?>(null) }
    var wasAttempted by remember { mutableStateOf(false) }

    val iconsColor = Color(0xFF3366FF)
    val buttonColor = Color(0xFF3366FF)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
            auth.login(email, senha) { success ->
                coroutineScope.launch {
                    if (success) {
                        navController.navigate("mainscreen")
                    } else {
                        snackbarHostState.showSnackbar("Email ou senha incorretos")
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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

                Column(
                    modifier = Modifier
                        .padding(bottom = 109.dp)
                        .fillMaxSize(), // Preenche a tela inteira
                    horizontalAlignment = Alignment.CenterHorizontally, // Centraliza os itens horizontalmente
                    verticalArrangement = Arrangement.Center // Centraliza os itens verticalmente
                ){
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "SuperID logo",
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .aspectRatio(1f)
                            .padding(bottom = 0.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        "Entre na sua conta",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("E-mail", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = iconsColor
                            )
                        },
                        isError = wasAttempted && emailError != null,
                        supportingText = {
                            if (wasAttempted && emailError != null) {
                                Text(emailError!!, color = Color(0xFFFF5858))
                            }
                        },
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconsColor,
                            unfocusedBorderColor = Color.DarkGray,
                            cursorColor = iconsColor
                        )
                    )

                    OutlinedTextField(
                        value = senha,
                        onValueChange = { senha = it },
                        placeholder = { Text("Senha mestra", color = Color.Gray) },
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = iconsColor
                            )
                        },
                        isError = wasAttempted && senhaError != null,
                        supportingText = {
                            if (wasAttempted && senhaError != null) {
                                Text(senhaError!!, color = Color(0xFFFF5858))
                            }
                        },
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconsColor,
                            unfocusedBorderColor = Color.DarkGray,
                            cursorColor = iconsColor
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { tryLogin() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (email.isNotBlank() && senha.isNotBlank()) buttonColor else Color.DarkGray,
                            disabledContainerColor = Color.DarkGray
                        ),
                        enabled = email.isNotBlank() && senha.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            "Entrar",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 55.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
        }
    }
}
