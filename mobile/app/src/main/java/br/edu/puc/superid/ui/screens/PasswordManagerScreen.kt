package br.edu.puc.superid.ui.screens

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
import br.edu.puc.superid.database.FirestoreHandler

@Composable
fun PasswordManagerScreen(navController: NavHostController) {
    Scaffold(
        containerColor = Color.Black,
        topBar = { TopBar() }, // ← Barra azul no topo
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Espaço abaixo da barra
            EmailVerificationCard(isEmailVerified = false)
            Spacer(modifier = Modifier.height(24.dp))
            QuickActionsBar(navController)
            Spacer(modifier = Modifier.height(24.dp))
            CategorySection(navController)
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
fun QuickActionsBar(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp)
            .border(width = 1.dp, color = Color(0xFF3366FF), shape = RoundedCornerShape(24.dp)) // Borda azul
            .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(24.dp)) // Fundo escuro com transparência
            .padding(16.dp) // Padding interno do container
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
                title = "Editar",
                icon = Icons.Default.Edit,
                iconColor = Color(0xFF3366FF)
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
fun CategorySection(navController: NavHostController) {
    val firestore = FirestoreHandler()
    var categorias by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        categorias = firestore.buscarTodasCategorias()
    }

    val scrollState = rememberScrollState()
    var containerHeightPx by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = "CATEGORIAS" ,
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
                    .onGloballyPositioned { coordinates ->
                        containerHeightPx = coordinates.size.height.toFloat()
                    }
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Conteúdo rolável
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxWidth()
                    ) {
                        categorias.forEachIndexed { index, categoria ->
                            CategoryItem(
                                name = categoria,
                                count = 0,
                                onClick = { navController.navigate("senhas/${categoria}") }
                            )
                            if (index != categorias.lastIndex) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = Color(0xFF3366FF), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    // Scrollbar
                    val indicatorHeight = 40.dp
                    val scrollMax = scrollState.maxValue
                    val scrollProgress = if (scrollMax > 0) scrollState.value.toFloat() / scrollMax else 0f
                    val offsetPx = (containerHeightPx - with(density) { indicatorHeight.toPx() }) * scrollProgress

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .width(6.dp)
                            .fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(y = with(density) { offsetPx.toDp() })
                                .height(indicatorHeight)
                                .width(4.dp)
                                .background(Color(0xFF5E5E60), shape = RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        } else {
            Text("Carregando categorias...", color = Color.LightGray)
        }
    }
}


@Composable
fun CategoryItem(name: String, count: Int, onClick: () -> Unit) {
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
        Icon(Icons.Default.ArrowForward, contentDescription = "Ver mais", tint = Color.White)
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


