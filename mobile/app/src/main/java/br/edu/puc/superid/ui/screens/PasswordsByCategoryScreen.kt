package br.edu.puc.superid.ui.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.edu.puc.superid.database.FirestoreHandler
import br.edu.puc.superid.database.Senha
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordsByCategoryScreen(
    categoryName: String,
    navController: NavHostController
) {
    val firestore = FirestoreHandler()
    var senhas by remember { mutableStateOf<List<Senha>>(emptyList()) }
    var senhaSelecionada by remember { mutableStateOf<Senha?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Carrega senhas inicialmente
    LaunchedEffect(categoryName) {
        senhas = firestore.buscarSenhasPorCategoria(categoryName)
    }

    suspend fun excluirSenha(senha: Senha): Boolean =
        suspendCancellableCoroutine { continuation ->
            firestore.deletarSenha(senha.guid) { sucesso ->
                continuation.resume(sucesso)
            }
        }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = categoryName, color = Color(0xFF3366FF), fontSize = 28.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                val isError = data.visuals.message.contains("erro", ignoreCase = true)
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) Color.Red else Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (senhas.isNotEmpty()) {
                senhas.forEachIndexed { index, senha ->
                    PasswordItem(
                        senha = senha,
                        onEditClick = {
                            navController.navigate(
                                "editar_senha/${senha.guid}/${senha.descricao}/${senha.login}/${senha.nomeCategoria}/${senha.senha}"
                            )
                        },
                        onDeleteClick = {
                            senhaSelecionada = senha
                        }
                    )
                    if (index != senhas.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFF3366FF).copy(alpha = 0.3f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } else {
                Text("Nenhuma senha cadastrada.", color = Color.LightGray)
            }
        }
    }

    // Diálogo de confirmação
    senhaSelecionada?.let { senha ->
        AlertDialog(
            onDismissRequest = { senhaSelecionada = null },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        val sucesso = excluirSenha(senha)
                        senhaSelecionada = null
                        if (sucesso) {
                            isLoading = false
                            senhas = firestore.buscarSenhasPorCategoria(categoryName)
                            snackbarHostState.showSnackbar("Senha excluída com sucesso")
                        } else {
                            isLoading = false
                            snackbarHostState.showSnackbar("Erro ao excluir senha")
                        }
                    }
                }) {
                    Text("Confirmar", color = Color(0xFF3366FF), fontSize = 17.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { senhaSelecionada = null }) {
                    Text("Cancelar", color = Color.Gray, fontSize = 17.sp)
                }
            },
            title = { Text("Excluir senha",
                color = Color(0xFF3366FF),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
            },
            text = { Text("Tem certeza que deseja excluir a senha \"${senha.descricao   }\"?",
                color = Color.LightGray,
                fontSize = 18.sp
            ) },
            containerColor = Color(0xFF000000),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.border(
                width = 2.dp,
                color = Color(0xFF3366FF),
                shape = RoundedCornerShape(15.dp)
            )
        )
    }

    // Diálogo de loading
    if (isLoading) {
        AlertDialog(
            onDismissRequest = { /* impede fechamento */ },
            confirmButton = {},
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Excluindo...", color = Color.Black)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun PasswordItem(
    senha: Senha,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var mostrarSenha by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(senha.descricao, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(senha.login, color = Color.LightGray, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (mostrarSenha) senha.senha else "••••••••",
                    color = Color.Gray,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { mostrarSenha = !mostrarSenha }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (mostrarSenha) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Ver senha",
                        tint = Color(0xFF3366FF)
                    )
                }
            }
        }

        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(28.dp))
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Editar senha") },
                    onClick = {
                        expanded = false
                        onEditClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Excluir senha") },
                    onClick = {
                        expanded = false
                        onDeleteClick()
                    }
                )
            }
        }
    }
}
