package br.edu.puc.superid.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Dehaze
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.edu.puc.superid.database.FirestoreHandler
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val backgroundColor = Color(0xFF102952)
    val iconColor = Color(0xFF00D7FF)
    val sectionBackground = Color(0xFF1C355E)
    val textColor = Color.White

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val firestore = FirestoreHandler()
    var categorias by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val resultado = firestore.buscarTodasCategorias()
        categorias = resultado
    }

    Log.i("CATEGORIAS", categorias.toString())

    var expanded by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }

    val fabPosition = remember { mutableStateOf(Offset.Zero) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = backgroundColor,
                modifier = Modifier.width(280.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Menu",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 20.sp
                )
                Divider(color = Color.White.copy(alpha = 0.2f))

                NavigationDrawerItem(
                    label = { Text("Minhas Senhas") },
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = iconColor
                        )
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = backgroundColor,
                        selectedIconColor = iconColor,
                        selectedTextColor = iconColor
                    )
                )
            }
        }
    ) {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                TopAppBar(
                    title = { Text("Minhas senhas", color = textColor) },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = textColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = sectionBackground)
                )
            },
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .onGloballyPositioned {
                            fabPosition.value = it.positionInWindow()
                        }
                ) {
                    FloatingActionButton(
                        onClick = { expanded = !expanded },
                        containerColor = iconColor
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Abrir Menu")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = fabPosition.value.x.dp, y = fabPosition.value.y.dp),
                        modifier = Modifier.background(sectionBackground)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Nova Senha", color = Color.White) },
                            onClick = {
                                navController.navigate("signuppassword")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nova Categoria", color = Color.White) },
                            onClick = { expanded = false }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Surface(
                    color = sectionBackground,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SectionTitle("CATEGORIAS (${categorias.size})", textColor)
                            TextButton(onClick = { editMode = !editMode }) {
                                Text(
                                    if (editMode) "Cancelar" else "Editar",
                                    color = if(editMode) Color.Red else iconColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        categorias.forEach { nome ->
                            ItemRow(
                                label = nome.toString(),
                                icon = Icons.Filled.Lock, // Use um ícone padrão, ou escolha dinamicamente
                                count = "0", // Pode trocar para a quantidade de senhas se quiser
                                iconColor = iconColor,
                                editMode = editMode,
                                onEditClick = { /* Lógica de edição */ },
                                onDeleteClick = { /* Lógica de exclusão */ }
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, color: Color) {
    Text(
        text = title,
        color = color,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun ItemRow(
    label: String,
    icon: ImageVector,
    count: String,
    iconColor: Color,
    editMode: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = count,
            color = Color.Gray,
            fontSize = 14.sp
        )
        if (editMode) {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = iconColor)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Filled.Delete, contentDescription = "Excluir", tint = Color.Red)
            }
        }
    }
}
