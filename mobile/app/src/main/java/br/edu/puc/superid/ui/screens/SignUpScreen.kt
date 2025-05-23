package br.edu.puc.superid.ui.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import br.edu.puc.superid.R
import br.edu.puc.superid.auth.AuthHandler
import br.edu.puc.superid.ui.components.CustomDialog
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
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    val iconsColor = Color(0xFF3366FF)
    val buttonColor = Color(0xFF3366FF)

    val nameValid = name.isNotBlank()
    val emailValid = email.contains("@") && Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email)
    val senhaValid = senha.length >= 6
    val termosValid = checked

    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var dialogIcon by remember { mutableStateOf(Icons.Default.Check) }
    var dialogIconColor by remember { mutableStateOf(Color(0xFF3366FF)) }

    val allFieldsValid = nameValid && emailValid && senhaValid && termosValid
    val context = LocalContext.current

    fun trySignUp() {
        if (allFieldsValid) {
            auth.cadastrarCredencial(context, name, email, senha, imei) { success, message ->
                scope.launch {
                    if (success) {
                        dialogTitle = "Verifique seu e-mail!"
                        dialogMessage = "Sua conta foi criada com sucesso."
                        dialogIcon = Icons.Default.Check
                        dialogIconColor = Color(0xFF4CAF50)
                        showDialog = true
                    } else {
                        dialogTitle = "Erro ao criar conta"
                        dialogMessage = message
                        dialogIcon = Icons.Default.Warning
                        dialogIconColor = Color(0xFFEC4D4D)
                        showDialog = true
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
                .background(Color.Black)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Botão de voltar
                Row(
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .clickable { navController.navigate("createAccount") },
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
                    modifier = Modifier.fillMaxWidth().padding(bottom = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "SuperID logo",
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f)
                            .padding(bottom = 0.dp),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        "CRIE SUA CONTA",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Nome
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Nome Completo", color = Color.Gray, fontSize = 20.sp) },
                        leadingIcon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null, tint = iconsColor) },
                        trailingIcon = { if (nameValid) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Green) },
                        textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconsColor,
                            unfocusedBorderColor = Color.DarkGray,
                            cursorColor = iconsColor
                        )
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Email", color = Color.Gray, fontSize = 20.sp) },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = iconsColor) },
                        trailingIcon = { if (emailValid) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Green) },
                        textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconsColor,
                            unfocusedBorderColor = Color.DarkGray,
                            cursorColor = iconsColor
                        )
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // Senha
                    OutlinedTextField(
                        value = senha,
                        onValueChange = { senha = it },
                        placeholder = { Text("Senha Mestra", color = Color.Gray, fontSize = 20.sp) },
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = iconsColor) },
                        trailingIcon = { if (senhaValid) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Green) },
                        textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconsColor,
                            unfocusedBorderColor = Color.DarkGray,
                            cursorColor = iconsColor
                        )
                    )

                    if (!senhaValid && senha.isNotEmpty()) {
                        Text(
                            text = "Sua senha deve possuir no mínimo 6 caracteres",
                            fontSize = 16.sp,
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
                                style = SpanStyle(color = Color.White, fontSize = 14.sp)
                            ) { append("LI E ESTOU DE ACORDO COM O ") }
                            pushStringAnnotation(tag = "TERMOS", annotation = "mostrar_dialog")
                            withStyle(
                                style = SpanStyle(color = iconsColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            ) { append("TERMO DE USO") }
                            pop()
                        }
                        ClickableText(
                            text = annotatedText,
                            onClick = { offset ->
                                annotatedText.getStringAnnotations("TERMOS", offset, offset).firstOrNull()?.let {
                                    showTermsDialog = true
                                }
                            },
                            style = TextStyle(color = Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Botão Criar Conta
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
                        Text("CRIAR CONTA", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "JÁ POSSUI CONTA?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.clickable { navController.navigate("login") }
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
            if (showTermsDialog) {
                TermsDialog(onDismiss = { showTermsDialog = false })
            }

            if (showDialog) {
                CustomDialog(
                    title = dialogTitle,
                    message = dialogMessage,
                    icon = dialogIcon,
                    iconColor = dialogIconColor,
                    onConfirm = {
                        showDialog = false
                        if (dialogIcon == Icons.Default.Check) {
                            navController.navigate("login")
                        }
                    },
                    onDismiss = {
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun TermsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3366FF), // Cor de fundo
                    contentColor = Color.White // Cor do texto
                )
            ) {
                Text("Fechar")
            }
        },
        title = {
            Text(
                text = "Termos de Uso",
                color = Color(0xFF3366FF),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .height(300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = """
                            Bem-vindo ao SuperID. Ao utilizar nosso serviço, você concorda com estes Termos de Uso. Por favor, leia-os atentamente.

                            1. Aceitação dos Termos
                            Ao acessar ou utilizar o Aplicativo, você concorda em cumprir estes Termos de Uso e a nossa Política de Privacidade. Se você não concordar com qualquer parte dos termos, não utilize o Aplicativo.

                            2. Uso do Aplicativo
                            O SuperId permite que você armazene e gerencie suas senhas de forma segura. Ao utilizar o Aplicativo, você concorda em:
                            • Fornecer informações verdadeiras e atualizadas.
                            • Utilizar o Aplicativo apenas para fins legais e pessoais.
                            • Não compartilhar ou revender o Aplicativo.

                            3. Segurança
                            Adotamos práticas como criptografia de dados e autenticação. Ainda assim, você reconhece que o uso é por sua conta e risco.

                            4. Responsabilidades do Usuário
                            Você é responsável por manter a confidencialidade do seu login e senha.

                            5. Propriedade Intelectual
                            Todos os direitos são do Desenvolvedor. Não copie ou modifique sem autorização.

                            6. Modificações e Encerramento
                            Podemos modificar ou descontinuar o Aplicativo a qualquer momento. O uso contínuo implica aceitação das mudanças.

                            7. Limitação de Responsabilidade
                            Não nos responsabilizamos por danos resultantes do uso ou da incapacidade de usar o Aplicativo.

                            9. Contato
                            Dúvidas? vitor.mvd@puccampinas.edu.br
                    """.trimIndent(),
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        },
        modifier = Modifier
            .border(
                width = 2.dp,
                color = Color (0xFF3366FF),
                shape = RoundedCornerShape(16.dp)
            ),
        containerColor = Color.DarkGray,
        shape = RoundedCornerShape(16.dp)
    )
}