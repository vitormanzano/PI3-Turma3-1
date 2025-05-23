package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.edu.puc.superid.R
import br.edu.puc.superid.auth.AuthHandler
import br.edu.puc.superid.database.FirestoreHandler
import br.edu.puc.superid.ui.components.CustomDialog

@Composable
fun ProfileScreen(navController: NavHostController) {
    val authHandler = AuthHandler()
    val firestore = FirestoreHandler()
    val userName = remember { mutableStateOf("") }
    val user = authHandler.obterUser()
    val isEmailVerified = remember { mutableStateOf(user?.isEmailVerified == true) }

    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        firestore.obterNomeUsuario { nome ->
            userName.value = nome
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp)
        ) {
            IconButton(
                onClick = {
                    navController.navigate("mainScreen") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }

            Image(
                painter = painterResource(id = R.drawable.logo_png),
                contentDescription = "Logo",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .height(150.dp)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                Image(
                    painter = painterResource(id = R.drawable.profile_icon1),
                    contentDescription = "Ícone de Usuário",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nome:", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(userName.value, color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    color = Color(0xFF3366FF),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Email:", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(user?.email ?: "", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(80.dp))

                Button(
                    onClick = {
                        authHandler.deslogarUsuario()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, Color(0xFF3366FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SAIR", color = Color.White, fontSize = 16.sp)
                }
            }
        }

        BottomNavigationBar(
            navController = navController,
            selectedIndex = 2,
            isEmailVerified = isEmailVerified.value,
            onItemSelected = { index ->
                when (index) {
                    0 -> navController.navigate("mainScreen") {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }

                    1 -> {
                        if (isEmailVerified.value) {
                            navController.navigate("qrcode") {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            showDialog.value = true
                        }
                    }

                    2 -> navController.navigate("perfil") {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        if (showDialog.value) {
            CustomDialog(
                title = "Acesso negado",
                message = "Verifique seu e-mail para acessar esta funcionalidade.",
                icon = Icons.Default.Warning,
                iconColor = Color(0xFFEC4D4D),
                onConfirm = { showDialog.value = false },
                onDismiss = { showDialog.value = false }
            )
        }
    }
}


