package br.edu.puc.superid.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.edu.puc.superid.auth.AuthHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SignUpScreen(imei: String, navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }

    val auth = AuthHandler()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDialog by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var senhaError by remember { mutableStateOf<String?>(null) }
    var termosError by remember { mutableStateOf<String?>(null) }
    var wasAttempted by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFF102952)
    val iconsColor = Color(0xFF00D7FF)
    val buttonColor = Color(0xFF00D7FF)

    fun validateFields(): Boolean {
        nameError = if (name.isBlank()) "Nome não pode ser vazio." else null
        emailError = when {
            email.isBlank() -> "Email não pode ser vazio."
            !email.contains("@") -> "Email inválido."
            else -> null
        }
        senhaError = if (senha.length < 6) "Senha deve ter no mínimo 6 caracteres." else null
        termosError = if (!checked) "Você deve aceitar os termos." else null
        return nameError == null && emailError == null && senhaError == null && termosError == null
    }

    fun trySignUp() {
        wasAttempted = true
        if (validateFields()) {
            auth.cadastrarCredencial(name, email, senha, imei)
            scope.launch {
                snackbarHostState.showSnackbar("Conta criada com sucesso!")
                delay(2000)
                navController.navigate("login")
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Lock Icon",
                        tint = Color.White,
                        modifier = Modifier.size(55.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Super ID",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Nome
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Nome completo", color = Color.Gray) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = null, tint = iconsColor)
                    },
                    textStyle = TextStyle(color = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = wasAttempted && nameError != null,
                    supportingText = {
                        if (wasAttempted && nameError != null) Text(nameError!!, color = Color.Red)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = iconsColor,
                        unfocusedBorderColor = Color.DarkGray,
                        cursorColor = iconsColor
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Email
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

                // Senha
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

                // Termos
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = iconsColor,
                            uncheckedColor = Color.Gray,
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val annotatedText = buildAnnotatedString {
                        append("Li e estou de acordo com o ")
                        pushStringAnnotation(tag = "TERMOS", annotation = "mostrar_dialog")
                        withStyle(style = SpanStyle(color = iconsColor, fontWeight = FontWeight.Bold)) {
                            append("Termo de Uso")
                        }
                        pop()
                    }
                    ClickableText(
                        text = annotatedText,
                        onClick = { offset ->
                            annotatedText.getStringAnnotations("TERMOS", offset, offset).firstOrNull()?.let {
                                showDialog = true
                            }
                        },
                        style = TextStyle(color = Color.White)
                    )
                }

                if (wasAttempted && termosError != null) {
                    Text(termosError!!, color = Color.Red, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botão de criar conta
                Button(
                    onClick = { trySignUp() },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                ) {
                    Text("CRIAR CONTA", fontWeight = FontWeight.Bold, color = backgroundColor)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Já possui uma conta?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    }
                )
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Fechar")
                        }
                    },
                    title = { Text("Termos de Uso") },
                    text = { Text("termos.") },
                    containerColor = Color.White
                )
            }
        }
    }
}
