package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.edu.puc.superid.R
import br.edu.puc.superid.database.FirestoreHandler
import kotlinx.coroutines.launch
import br.edu.puc.superid.ui.components.CustomDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPasswordScreen(
    navController: NavHostController,
    guid: String,
    nomeInicial: String = "",
    loginInicial: String = "",
    senhaInicial: String = "",
    categoriaInicial: String = ""
) {
    var name by remember { mutableStateOf(nomeInicial) }
    var login by remember { mutableStateOf(loginInicial) }
    var senha by remember { mutableStateOf(senhaInicial) }
    var categoria by remember { mutableStateOf(categoriaInicial) }
    var expanded by remember { mutableStateOf(false) }

    val senhaValid = senha.length >= 1
    val allFieldsValid = senhaValid && categoria.isNotBlank() && name.isNotBlank()

    val firestore = FirestoreHandler()
    var categorias by remember { mutableStateOf<List<String>>(emptyList()) }
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var dialogIcon by remember { mutableStateOf(Icons.Default.Check) }
    var dialogIconColor by remember { mutableStateOf(Color.Green) }

    val iconsColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val inputTextColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    LaunchedEffect(Unit) {
        categorias = firestore.buscarTodasCategorias()
    }

    Scaffold(containerColor = backgroundColor) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
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
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = textColor, modifier = Modifier.size(24.dp))
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
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop
                    )

                    Text("Editar Senha", fontWeight = FontWeight.Bold, color = textColor, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Nome da senha", color = Color.Gray) },
                        textStyle = TextStyle(color = textColor),
                        leadingIcon = { Icon(Icons.Outlined.Label, null, tint = iconsColor) },
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
                        leadingIcon = { Icon(Icons.Outlined.AccountCircle, null, tint = iconsColor) },
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
                        textStyle = TextStyle(color = textColor),
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = iconsColor) },
                        trailingIcon = {
                            val visibilityIcon = if (senhaVisivel) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            val description = if (senhaVisivel) "Esconder senha" else "Mostrar senha"
                            IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                                Icon(imageVector = visibilityIcon, contentDescription = description, tint = iconsColor)
                            }
                        },
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
                            leadingIcon = { Icon(Icons.Outlined.Email, null, tint = iconsColor) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                focusedBorderColor = iconsColor,
                                unfocusedBorderColor = surfaceColor,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color(0xFF2B2B2B))
                        ) {
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
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    firestore.alterarSenha(guid, name, login, categoria, senha)
                                    dialogTitle = "Senha alterada!"
                                    dialogMessage = "Sua senha foi atualizada com sucesso."
                                    dialogIcon = Icons.Default.Check
                                    dialogIconColor = Color(0xFF4CAF50)
                                } catch (e: Exception) {
                                    dialogTitle = "Erro ao alterar senha"
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
                            containerColor = if (allFieldsValid) iconsColor else surfaceColor
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Salvar alterações", fontWeight = FontWeight.Bold, color = textColor)
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
                    popUpTo("EditPasswordScreen") { inclusive = true }
                }
            },
            onDismiss = { showDialog = false }
        )
    }
}
