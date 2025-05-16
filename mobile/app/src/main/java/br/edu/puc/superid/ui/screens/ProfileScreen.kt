package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser



@Composable
fun ProfileScreen(navController: NavHostController) {
    val authHandler = AuthHandler()
    val auth = AuthHandler()
    val firestore = FirestoreHandler()
    val userName = remember { mutableStateOf("") }

    val user = auth.obterUser()

    LaunchedEffect(Unit) {
        firestore.obterNomeUsuario() {nome ->
            userName.value = nome
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        // Botão de Voltar
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
        }

        // Logo centralizada no topo
        Image(
            painter = painterResource(id = R.drawable.logo_png), // sua logo
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .height(150.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(80.dp)) // espaço para não sobrepor com a logo

            // Ícone de usuário (imagem enviada)
            Image(
                painter = painterResource(id = R.drawable.profile_icon1),
                contentDescription = "Ícone de Usuário",
                modifier = Modifier
                    .size(250.dp) // AUMENTADO
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Olá, ${userName.value}",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botão Editar Senha Mestra
            Button(
                onClick = {
                    navController.navigate("editarSenhaMestra")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3366FF)),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("EDITAR SENHA MESTRA", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Sair
            Button(
                onClick = {
                    authHandler.deslogarUsuario()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A3A3A)),
                shape = RoundedCornerShape(32.dp),
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
}

