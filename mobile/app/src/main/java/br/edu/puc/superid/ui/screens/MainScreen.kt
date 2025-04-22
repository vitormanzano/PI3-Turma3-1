package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Dehaze
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val backgroundColor = Color(0xFF102952)
    val iconColor = Color(0xFF00D7FF)
    val textColor = Color.White
    val sections = listOf("Todas", "Favoritas", "Trabalho", "Pessoais")
    var selectedSection by remember { mutableStateOf("Todas") }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = backgroundColor,
                modifier = Modifier.width(280.dp) // Tamanho padrão de drawer
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Menu",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 20.sp
                )
                Divider(color = Color.White.copy(alpha = 0.2f))
                NavigationDrawerItem(
                    label = { Text("Minhas Senhas", color = textColor) },
                    selected = true,
                    onClick = { /* Ação */ }
                )
                NavigationDrawerItem(
                    label = { Text("Configurações", color = textColor) },
                    selected = false,
                    onClick = { /* Ação */ }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                TopAppBar(
                    title = {
                        Text("Minhas Senhas", color = textColor)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Menu, // Substitua por Icons.Default.Menu se quiser o ícone real de menu
                                contentDescription = "Menu",
                                tint = textColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* ação para nova senha */ },
                    containerColor = iconColor
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nova senha")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Categorias horizontais
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    sections.forEach { section ->
                        Button(
                            onClick = { selectedSection = section },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedSection == section) iconColor else backgroundColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(20)
                        ) {
                            Text(section)
                        }
                    }
                }

                // Conteúdo central
                Spacer(modifier = Modifier.height(48.dp))
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma senha cadastrada em \"$selectedSection\"",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


