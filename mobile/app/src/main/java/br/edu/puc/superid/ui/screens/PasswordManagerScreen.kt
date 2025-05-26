package br.edu.puc.superid.ui.screens

import android.util.Log
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import br.edu.puc.superid.R
import br.edu.puc.superid.auth.AuthHandler
import br.edu.puc.superid.database.FirestoreHandler
import br.edu.puc.superid.database.Senha
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import br.edu.puc.superid.ui.components.CustomDialog
import kotlinx.coroutines.delay

@Composable
fun PasswordManagerScreen(navController: NavHostController) {
    var isEditMode by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val authHandler = AuthHandler()
    val firestore = FirestoreHandler()
    val userName = remember { mutableStateOf("") }
    val user = authHandler.obterUser()
    val isEmailVerified = remember { mutableStateOf(user?.isEmailVerified == true) }

    LaunchedEffect(Unit) {
        firestore.obterNomeUsuario() { nome ->
            userName.value = nome
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // espera 5 segundos
            user?.reload()?.addOnSuccessListener {
                isEmailVerified.value = user.isEmailVerified
            }
        }
    }

    var currentIndex by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        containerColor = Color.Black,
        topBar = { TopBar() },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                selectedIndex = currentIndex,
                onItemSelected = { currentIndex = 0 },
                isEmailVerified = isEmailVerified.value
            )
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(scrollState),
            ) {
            Spacer(modifier = Modifier.height(8.dp))
            if (user != null) {
                EmailVerificationCard(isEmailVerified = isEmailVerified.value, user, authHandler)
            }
            Spacer(modifier = Modifier.height(24.dp))
            QuickActionsBar(navController) { isEditMode = it }
            Spacer(modifier = Modifier.height(24.dp))
            CategorySection(navController, isEditMode)
        }
    }
}

@Composable
fun TopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 0.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_png),
            contentDescription = "Logo do App",
            modifier = Modifier.size(150.dp)
        )
    }
}

@Composable
fun EmailVerificationCard(
    isEmailVerified: Boolean = false,
    user: FirebaseUser,
    auth: AuthHandler
) {
    if (!isEmailVerified) {
        var showCard by remember { mutableStateOf(true) }

        var showDialog by remember { mutableStateOf(false) }
        var dialogTitle by remember { mutableStateOf("") }
        var dialogMessage by remember { mutableStateOf("") }
        var dialogIcon by remember { mutableStateOf(Icons.Default.Warning) }
        var dialogIconColor by remember { mutableStateOf(Color(0xFFEC4D4D)) }

        val backgroundColor = Color(0xFF3366FF)
        val statusText = "E-mail não verificado, você não poderá recuperar sua senha mestra e nem utilizar o login sem senha!"
        val actionText = "Verificar agora"

        if (showCard) {
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

                    Button(
                        onClick = {
                            auth.enviarEmailParaVerificacao(
                                user = user,
                                onSuccess = {
                                    dialogTitle = "Verificação enviada"
                                    dialogMessage = "Um e-mail de verificação foi enviado para ${user.email}"
                                    dialogIcon = Icons.Default.Check
                                    dialogIconColor = Color(0xFF4CAF50)
                                    showDialog = true
                                },
                                onFailure = {
                                    dialogTitle = "Erro ao enviar"
                                    dialogMessage = "Não foi possível enviar o e-mail de verificação. Tente novamente mais tarde."
                                    dialogIcon = Icons.Default.Warning
                                    dialogIconColor = Color(0xFFEC4D4D)
                                    showDialog = true
                                }
                            )
                        },
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
    }
}

@Composable
fun QuickActionsBar(navController: NavHostController, onEditModeChanged: (Boolean) -> Unit) {
    var isEditMode by remember { mutableStateOf(false) }  // Estado local de edição

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color(0xFF3366FF), shape = RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(24.dp))
            .padding(16.dp) // Agora o padding está dentro, igual ao segundo Box
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickActionItem(
                title = "  Nova Senha  ",
                icon = Icons.Default.Password,
                iconColor = Color(0xFF3366FF),
                onClick = { navController.navigate("signuppassword") }
            )
            DividerVertical()
            QuickActionItem(
                title = "  Nova Categoria  ",
                icon = Icons.Default.Category,
                iconColor = Color(0xFF3366FF),
                onClick = { navController.navigate("signupcategory") }
            )
            DividerVertical()
            QuickActionItem(
                title = if (isEditMode) "Cancelar" else " Editar Categoria",
                icon = Icons.Default.Edit,
                iconColor = Color(0xFF3366FF),
                onClick = {
                    isEditMode = !isEditMode
                    onEditModeChanged(isEditMode)
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
    var quantidades by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    LaunchedEffect(Unit) {
        val categoriasCarregadas = firestore.buscarTodasCategorias()
        categorias = categoriasCarregadas

        // Para cada categoria, busca a quantidade (paralelamente se quiser)
        val quantidadesMap = categoriasCarregadas.associateWith { categoria ->
            firestore.quantidadeDeSenhasPorCategoria(categoria) // Supondo que é uma suspend function
        }
        quantidades = quantidadesMap
    }

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
                    .border(width = 1.dp, color = Color(0xFF3366FF), shape = RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    categorias.forEachIndexed { index, categoria ->
                        val count = quantidades[categoria] ?: 0

                        if (categoria != "Sites da Web") {
                            CategoryItem(
                                name = categoria,
                                count = count,
                                onClick = { navController.navigate("senhas/${categoria}") },
                                isEditMode = isEditMode,
                                onDeleteClick = {
                                    val userUid = FirebaseAuth.getInstance().currentUser?.uid
                                    if (userUid != null) {
                                        firestore.deletarCategoria(userUid, categoria) { success ->
                                            if (success) {
                                                categorias = categorias.filter { it != categoria }
                                                quantidades = quantidades - categoria
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
                                isEditMode = false,
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
    isEditMode: Boolean,
    onDeleteClick: (Boolean) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = "$count senhas",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditMode) {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir Categoria",
                            tint = Color.Red
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Ver mais",
                    tint = Color.White
                )
            }
        }

        // Diálogo de confirmação
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteClick(true)
                        showSnackbar = true
                        snackbarMessage = "Categoria '$name' excluída com sucesso!"
                        showDeleteDialog = false
                    }) {
                        Text("Confirmar", color = Color(0xFF3366FF), fontSize = 17.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar", color = Color.LightGray, fontSize = 17.sp)
                    }
                },
                title = {
                    Text(
                        "Excluir categoria",
                        color = Color(0xFF3366FF),
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "Tem certeza que deseja excluir a categoria \"$name\"?",
                        color = Color.LightGray,
                        fontSize = 18.sp
                    )
                },
                containerColor = Color.DarkGray,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = Color(0xFF3366FF),
                    shape = RoundedCornerShape(15.dp)
                )
            )
        }

        // Snackbar de sucesso
        if (showSnackbar) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = Color(0xFF1E1E1E),
                contentColor = Color.White,
                content = { Text(snackbarMessage) },
                action = {
                    Button(
                        onClick = { showSnackbar = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3366FF),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Fechar")
                    }
                }
            )
        }

    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    selectedIndex: Int = 0,
    onItemSelected: (Int) -> Unit = {},
    isEmailVerified: Boolean
) {
    val items = listOf(
        NavigationItem(Icons.Default.Home, "mainScreen"),
        NavigationItem(Icons.Default.QrCodeScanner, "qrcode"),
        NavigationItem(Icons.Default.Person, "perfil")
    )

    var showDialog by remember { mutableStateOf(false) }
    val dialogTitle = "Email não verificado"
    val dialogMessage = "Você precisa verificar seu email antes de usar o login sem senha."

    Column {
        Divider(
            color = Color(0xFF3366FF),
            thickness = 0.8.dp
        )

        NavigationBar(
            containerColor = Color.Black,
            contentColor = Color.White
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = {
                        if (item.route == "qrcode" && !isEmailVerified) {
                            showDialog = true
                        } else {
                            onItemSelected(index)
                            val currentRoute = navController.currentBackStackEntry?.destination?.route
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = if (selectedIndex == index) Color(0xFF3366FF) else Color.White,
                                modifier = Modifier.size(
                                    if (item.icon == Icons.Default.QrCodeScanner) 36.dp else 30.dp
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

        if (showDialog) {
            CustomDialog(
                title = dialogTitle,
                message = dialogMessage,
                icon = Icons.Default.Warning,
                iconColor = Color(0xFFEC4D4D),
                onConfirm = { showDialog = false },
                onDismiss = { showDialog = false }
            )
        }
    }
}

data class NavigationItem(val icon: ImageVector, val route: String)



