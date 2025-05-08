package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordsByCategoryScreen(
    categoryName: String,
    navController: NavHostController
) {
    val firestore = FirestoreHandler()
    var senhas by remember { mutableStateOf<List<Senha>>(emptyList()) }

    LaunchedEffect(categoryName) {
        val resultado = firestore.buscarSenhasPorCategoria(categoryName)
        senhas = resultado
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = categoryName,
                        color = Color.White,
                        fontSize = 28.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (senhas.isNotEmpty()) {
                Column {
                    senhas.forEachIndexed { index, senha ->
                        PasswordItem(
                            senha = senha,
                            onEditClick = {
                                // TODO: Navegar para tela de edição
                            },
                            onDeleteClick = {
                                // TODO: Implementar exclusão no Firestore
                            }
                        )
                        if (index != senhas.lastIndex) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            } else {
                Text("Nenhuma senha cadastrada.", color = Color.LightGray)
            }
        }
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
            Text(senha.nome, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
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
                        imageVector = if (mostrarSenha) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Ver senha",
                        tint = Color.White
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
