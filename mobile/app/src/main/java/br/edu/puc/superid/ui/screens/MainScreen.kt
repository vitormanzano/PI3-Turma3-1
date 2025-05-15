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
    val backgroundColor = Color.DarkGray
    val iconColor = Color(0xFF3366FF)
    val sectionBackground = Color.Black
    val textColor = Color.White

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val firestore = FirestoreHandler()
    var categorias by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val resultado = firestore.buscarTodasCategorias()
        categorias = resultado
    }

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
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
                Divider(color = Color.White.copy(alpha = 0.2f))

                NavigationDrawerItem(
                    label = { Text("Minhas Senhas", style = MaterialTheme.typography.bodyMedium) },
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
                    title = {
                        Text(
                            "Minhas senhas",
                            style = MaterialTheme.typography.titleLarge,
                            color = textColor
                        )
                    },
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
                            text = { Text("Nova Senha", style = MaterialTheme.typography.bodyMedium, color = Color.White) },
                            onClick = {
                                navController.navigate("signuppassword")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nova Categoria", style = MaterialTheme.typography.bodyMedium, color = Color.White) },
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
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (editMode) Color(0xFFFF5858) else iconColor
                                )
                            }
                        }

                        categorias.forEach { nome ->
                            ItemRow(
                                label = nome.toString(),
                                icon = Icons.Filled.Lock,
                                count = "0",
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
        style = MaterialTheme.typography.bodySmall,
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
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = count,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        if (editMode) {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = iconColor)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Filled.Delete, contentDescription = "Excluir", tint = Color(0xFFFF5858))
            }
        }
    }
}
