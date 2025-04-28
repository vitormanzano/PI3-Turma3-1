package br.edu.puc.superid.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.edu.puc.superid.R
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

    val iconsColor = Color(0xFF3366FF)
    val buttonColor = Color(0xFF3366FF)

    val nameValid = name.isNotBlank()
    val emailValid =
        email.contains("@") && Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(
            email
        )
    val senhaValid = senha.length >= 6
    val termosValid = checked

    val allFieldsValid = nameValid && emailValid && senhaValid && termosValid

    fun trySignUp() {
        if (allFieldsValid) {
            auth.cadastrarCredencial(name, email, senha, imei) { success, message ->
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                    if (success) {
                        delay(500)
                        navController.navigate("login")
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
                // Seta para voltar para a página inicial
                Row(
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .clickable { navController.navigate("createAccount") },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,  // Ícone de seta para voltar
                        contentDescription = "Voltar",
                        tint = Color.White,  // Cor do ícone
                        modifier = Modifier.size(24.dp)  // Tamanho do ícone
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 41.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
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
                        "Crie sua conta",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Nome
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Nome completo", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = null,
                                tint = iconsColor
                            )
                        },
                        trailingIcon = {
                            if (nameValid) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.Green
                                )
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
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
                        trailingIcon = {
                            if (emailValid) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.Green
                                )
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Senha
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
                        trailingIcon = {
                            if (senhaValid) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.Green
                                )
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

                    // Texto de ajuda senha
                    if (!senhaValid && senha.isNotEmpty()) {
                        Text(
                            text = "Sua senha deve possuir no mínimo 6 caracteres",
                            fontSize = 16.sp,  // Alterado para 18.sp
                            color = Color.Gray,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .align(Alignment.Start)
                        )
                    }

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
                            withStyle(
                                style = SpanStyle(
                                    color = Color.White,  // Ajustei para o texto normal, se necessário
                                    fontSize = 16.sp // Aumentei o tamanho da fonte para o texto todo
                                )
                            ) {
                                append("Li e estou de acordo com o ")
                            }
                            pushStringAnnotation(tag = "TERMOS", annotation = "mostrar_dialog")
                            withStyle(
                                style = SpanStyle(
                                    color = iconsColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            ) {
                                append("Termo de Uso")
                            }
                            pop()
                        }
                        ClickableText(
                            text = annotatedText,
                            onClick = { offset ->
                                annotatedText.getStringAnnotations("TERMOS", offset, offset)
                                    .firstOrNull()?.let {
                                        showDialog = true
                                    }
                            },
                            style = TextStyle(color = Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botão de criar conta
                    Button(
                        onClick = { trySignUp() },
                        enabled = allFieldsValid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (allFieldsValid) buttonColor else Color.DarkGray,
                            disabledContainerColor = Color.DarkGray
                        ),

                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            "Criar conta",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
}
