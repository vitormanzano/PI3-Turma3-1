package br.edu.puc.superid.ui.screens

import android.util.Log
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.edu.puc.superid.auth.AuthHandler
import br.edu.puc.superid.database.FirestoreHandler
import br.edu.puc.superid.database.Senha
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
fun PasswordManagerScreen(navController: NavHostController) {
    var isEditMode by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val isEmailVerified = remember { mutableStateOf(false) }
    val auth = AuthHandler()
    val firestore = FirestoreHandler()
    val userName = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        auth.emailFoiVerificado() { verificado ->
            isEmailVerified.value = verificado
        }
    }

    LaunchedEffect(Unit) {
        firestore.obterNomeUsuario() {nome ->
            userName.value = nome
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = { TopBar("${userName.value}") },
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            EmailVerificationCard(isEmailVerified = isEmailVerified.value)
            Spacer(modifier = Modifier.height(24.dp))
            QuickActionsBar(navController) { isEditMode = it }
            Spacer(modifier = Modifier.height(24.dp))
            CategorySection(navController, isEditMode) // Passa o estado de edição
        }
    }
}

@Composable
fun TopBar(userName: String = "Usuário") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF0E2159),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF3366FF),
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Olá, $userName",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun EmailVerificationCard(isEmailVerified: Boolean = false) {
    val backgroundColor = if (isEmailVerified) Color(0xFF4CAF50) else Color(0xFF3366FF)
    val statusText = if (isEmailVerified) "E-mail verificado!" else "E-mail não verificado, você não poderá recuperar sua senha mestra!"
    val actionText = if (isEmailVerified) "Tudo certo!" else "Verificar agora"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Column {
            Text("Status do e-mail", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(statusText, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            if (!isEmailVerified) {
                Button(
                    onClick = { /* Acionar envio de verificação */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(actionText, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun QuickActionsBar(navController: NavHostController, onEditModeChanged: (Boolean) -> Unit) {
    var isEditMode by remember { mutableStateOf(false) }  // Estado local de edição

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp)
            .border(width = 1.dp, color = Color(0xFF3366FF), shape = RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickActionItem(
                title = "Nova Senha",
                icon = Icons.Default.Password,
                iconColor = Color(0xFF3366FF),
                onClick = { navController.navigate("signuppassword") }
            )
            DividerVertical()
            QuickActionItem(
                title = "Nova Categoria",
                icon = Icons.Default.Category,
                iconColor = Color(0xFF3366FF),
                onClick = { navController.navigate("signupcategory") }
            )
            DividerVertical()
            QuickActionItem(
                title = if (isEditMode) "Cancelar" else "Editar",
                icon = Icons.Default.Edit,
                iconColor = Color(0xFF3366FF),
                onClick = {
                    isEditMode = !isEditMode
                    onEditModeChanged(isEditMode) // Chama a função passada para alterar o estado do modo de edição
                }
            )
        }
    }
}

@Composable
fun QuickActionItem(title: String, icon: ImageVector, iconColor: Color, onClick: (() -> Unit)? = null ){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DividerVertical() {
    Box(
        modifier = Modifier
            .height(48.dp)
            .width(1.dp)
            .background(Color.Gray.copy(alpha = 0.4f))
    )
}

@Composable
fun CategorySection(navController: NavHostController, isEditMode: Boolean) {
    val firestore = FirestoreHandler()
    var categorias by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        categorias = firestore.buscarTodasCategorias()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = "CATEGORIAS",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (categorias.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .border(width = 1.dp, color = Color(0xFF3366FF), shape = RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .fillMaxWidth()
                ) {

                    categorias.forEachIndexed { index, categoria ->
                        if (categoria != "Sites da Web") {
                            CategoryItem(
                                name = categoria,
                                count = 0,
                                onClick = { navController.navigate("senhas/${categoria}") },
                                isEditMode = isEditMode, // Passa o estado de edição
                                onDeleteClick = {
                                    val userUid = FirebaseAuth.getInstance().currentUser?.uid
                                    if (userUid != null) {
                                        firestore.deletarCategoria(userUid, categoria) { success ->
                                            if (success) {
                                                categorias = categorias.filter { it != categoria }
                                            } else {
                                                Log.e("FIREBASE", "Falha ao excluir categoria $categoria")
                                            }
                                        }
                                    } else {
                                        Log.e("FIREBASE", "Usuário não autenticado")
                                    }
                                }
                            )
                        } else {
                            CategoryItem(
                                name = categoria,
                                count = 0,
                                onClick = { navController.navigate("senhas/${categoria}") },
                                isEditMode = false, // Não permite edição para "Sites da Web"
                                onDeleteClick = {}
                            )
                        }

                        if (index != categorias.lastIndex) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFF3366FF), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        } else {
            Text("Carregando categorias...", color = Color.LightGray)
        }
    }
}

@Composable
fun CategoryItem(
    name: String,
    count: Int,
    onClick: () -> Unit,
    isEditMode: Boolean, // Recebe o estado de edição
    onDeleteClick: (Boolean) -> Unit // Ação de excluir, agora passa o resultado da exclusão (sucesso ou falha)
) {
    var showDeleteDialog by remember { mutableStateOf(false) } // Estado para exibir o diálogo
    var showSnackbar by remember { mutableStateOf(false) } // Estado para exibir o Snackbar
    var snackbarMessage by remember { mutableStateOf("") } // Mensagem do Snackbar

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, color = Color.White, fontSize = 16.sp)
            Text("$count senhas", color = Color.LightGray, fontSize = 12.sp)
        }

        // Mostrar o ícone de lixeira apenas se o modo de edição estiver ativado
        if (isEditMode) {
            IconButton(
                onClick = { showDeleteDialog = true }, // Exibe o diálogo de confirmação
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Excluir Categoria",
                    tint = Color.Red
                )
            }
        } else {
            Spacer(modifier = Modifier.width(32.dp)) // Espaço onde o ícone de excluir estaria
        }

        Icon(Icons.Default.ArrowForward, contentDescription = "Ver mais", tint = Color.White)
    }

    // Diálogo de confirmação de exclusão
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClick(true) // Chama a função de deletar e sinaliza sucesso
                    showSnackbar = true
                    snackbarMessage = "Categoria '$name' excluída com sucesso!" // Mensagem de sucesso
                    showDeleteDialog = false
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Excluir categoria") },
            text = { Text("Tem certeza que deseja excluir a categoria \"$name\"?") },
            containerColor = Color.White
        )
    }

    // Exibindo a mensagem de sucesso após a exclusão
    if (showSnackbar) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            content = { Text(snackbarMessage) },
            action = {
                Button(onClick = { showSnackbar = false }) {
                    Text("Fechar")
                }
            }
        )
    }
}



@Composable
fun BottomNavigationBar(

    selectedIndex: Int = 0,
    onItemSelected: (Int) -> Unit = {}
) {
    Column {
        // Linha de separação acima da BottomBar
        Divider(
            color = Color(0xFF3366FF),
            thickness = 0.8.dp
        )

        NavigationBar(
            containerColor = Color.Black,
            contentColor = Color.White
        ) {
            val items = listOf(
                Icons.Default.Home,
                Icons.Default.QrCodeScanner,
                Icons.Default.Settings
            )

            items.forEachIndexed { index, icon ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { onItemSelected(index) },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (selectedIndex == index) Color(0xFF3366FF) else Color.White,
                                modifier = Modifier.size(
                                    if (icon == Icons.Default.QrCodeScanner) 36.dp else 30.dp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (selectedIndex == index) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF3366FF), shape = CircleShape)
                                )
                            } else {
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}


