package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import br.edu.puc.superid.auth.AuthHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import br.edu.puc.superid.R
import br.edu.puc.superid.ui.components.CustomDialog

@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var dialogIcon by remember { mutableStateOf(Icons.Default.Check) }
    var dialogIconColor by remember { mutableStateOf(Color(0xFF4CAF50)) }
    var showDialogVerificationEmail by remember {mutableStateOf(false)}
    val context = LocalContext.current

    val auth = AuthHandler()
    val iconsColor = Color(0xFF3366FF)
    val emailValid =
        email.contains("@") && Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email)

    fun sendResetPasswordEmail() {
        auth.enviarEmailParaRedefinirSenha(email)
        auth.estadoDoEmail(context)
        dialogTitle = "Verifique seu e-mail"
        dialogMessage = "Se o e-mail informado estiver cadastrado e validadado, você receberá um link para redefinir sua senha."
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
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(bottom = 200.dp)
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "SuperID logo",
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "RECUPERAR SENHA MESTRA",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "DIGITE SEU EMAIL CADASTRADO. ENVIAREMOS UM LINK PARA REDEFINIR SUA SENHA",
                        color = Color.Gray,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Email", color = Color.Gray, fontSize = 20.sp) },
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
                        textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconsColor,
                            unfocusedBorderColor = Color.DarkGray,
                            cursorColor = iconsColor
                        )
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            val emailEstaVerificado = auth.estadoDoEmail(context)

                            if (emailEstaVerificado) {
                                sendResetPasswordEmail()
                            }
                            else {
                                showDialogVerificationEmail = true
                            }


                                  },
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
                        Text("ENVIAR EMAIL", color = Color.White, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        "VOLTAR PARA O LOGIN",
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

                if (showDialogVerificationEmail) { //Feedback caso email não esteja validado
                    CustomDialog(
                        title = "Email não verificado!",
                        message = "Para recuperar a senha mestra seu email precisa estar verificado!",
                        icon = Icons.Default.Close,
                        iconColor = Color(0xFFEC4D4D),
                        onConfirm = { showDialogVerificationEmail = false },
                        onDismiss = { showDialogVerificationEmail = false }
                    )
                }
            }
        }
    }
}
