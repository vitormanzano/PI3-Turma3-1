package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.puc.superid.database.FirestoreHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Abc
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Label
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavHostController
import br.edu.puc.superid.R
import br.edu.puc.superid.ui.components.CustomDialog
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPasswordScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val senhaValid = senha.length >= 1
    val firestore = FirestoreHandler()
    var categorias by remember { mutableStateOf<List<String>>(emptyList()) }
    val iconsColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val inputTextColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val allFieldsValid = senhaValid && categoria.isNotBlank() && name.isNotBlank()

    val scope = rememberCoroutineScope()

    // Estados do diálogo
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var dialogIcon by remember { mutableStateOf(Icons.Default.Check) }
    var dialogIconColor by remember { mutableStateOf(Color.Green) }

    LaunchedEffect(Unit) {
        val resultado = firestore.buscarTodasCategorias()
        categorias = resultado
    }

    Scaffold(
        containerColor = backgroundColor
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .clickable { navController.popBackStack() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 41.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_png),
                        contentDescription = "SuperID logo",
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .aspectRatio(1f)
                            .padding(bottom = 0.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        "Nova Senha",
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Nome da senha", color = Color.Gray) },
                        textStyle = TextStyle(color = textColor),
                        leadingIcon = { Icon(Icons.Outlined.Label, contentDescription = null, tint = iconsColor) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconsColor,
                            unfocusedBorderColor = surfaceColor,
                            cursorColor = iconsColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = login,
                        onValueChange = { login = it },
                        placeholder = { Text("Login (opcional)", color = Color.Gray) },
                        textStyle = TextStyle(color = textColor),
                        leadingIcon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null, tint = iconsColor) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconsColor,
                            unfocusedBorderColor = surfaceColor,
                            cursorColor = iconsColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    var senhaVisivel by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = senha,
                        onValueChange = { senha = it },
                        placeholder = { Text("Senha", color = Color.Gray) },
                        visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = iconsColor)
                        },
                        trailingIcon = {
                            val icon = if (senhaVisivel) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            val description = if (senhaVisivel) "Esconder senha" else "Mostrar senha"
                            IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                                Icon(imageVector = icon, contentDescription = description, tint = iconsColor)
                            }
                        },
                        textStyle = TextStyle(color = textColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconsColor,
                            unfocusedBorderColor = surfaceColor,
                            cursorColor = iconsColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = categoria,
                            onValueChange = {},
                            placeholder = { Text("Categoria", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = iconsColor) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                focusedBorderColor = iconsColor,
                                unfocusedBorderColor = surfaceColor,
                                cursorColor = iconsColor,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color(0xFF2B2B2B))
                        ) {
                            if (categorias.isNotEmpty()) {
                                categorias.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = textColor) },
                                        onClick = {
                                            categoria = option
                                            expanded = false
                                        },
                                        modifier = Modifier.background(Color(0xFF2B2B2B))
                                    )
                                }
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Carregando categorias...", color = onSurfaceColor) },
                                    onClick = {},
                                    enabled = false,
                                    modifier = Modifier.background(Color(0xFF2B2B2B))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    firestore.cadastrarSenha(name, login, categoria, senha)
                                    dialogTitle = "Senha salva!"
                                    dialogMessage = "Sua senha foi cadastrada com sucesso."
                                    dialogIcon = Icons.Default.Check
                                    dialogIconColor = Color(0xFF4CAF50)
                                } catch (e: Exception) {
                                    dialogTitle = "Erro ao salvar senha"
                                    dialogMessage = e.message ?: "Erro desconhecido"
                                    dialogIcon = Icons.Default.Warning
                                    dialogIconColor = Color(0xFFEC4D4D)
                                } finally {
                                    showDialog = true
                                }
                            }
                        },
                        enabled = allFieldsValid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (allFieldsValid) iconsColor else surfaceColor,
                            disabledContainerColor = surfaceColor
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Salvar senha", fontWeight = FontWeight.Bold, color = textColor)
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    if (showDialog) {
        CustomDialog(
            title = dialogTitle,
            message = dialogMessage,
            icon = dialogIcon,
            iconColor = dialogIconColor,
            onConfirm = {
                showDialog = false
                navController.navigate("mainScreen") {
                    popUpTo("signUpPassword") { inclusive = true }
                }
            },
            onDismiss = { showDialog = false }
        )
    }
}
