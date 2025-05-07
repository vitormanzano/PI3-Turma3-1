package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.edu.puc.superid.R

@Composable
fun CreateAccountScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top - Back Button
            Row(
                modifier = Modifier
                    .padding(top = 48.dp)
                    .clickable { navController.navigate("firstTime")},
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,  // Ícone de seta para voltar
                    contentDescription = "Voltar",
                    tint = Color.White,  // Cor do ícone
                    modifier = Modifier.size(24.dp)  // Tamanho do ícone
                )
            }

            // Middle - Content
            Column(
                modifier = Modifier.fillMaxWidth() .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Image (replace with your resource)
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "SuperID logo",
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .aspectRatio(1f)
                        .padding(bottom = 32.dp),
                    contentScale = ContentScale.Crop
                )


                // Title
                Text(
                    text = "Crie sua\nconta SuperID",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "SuperID é uma ferramenta que te permite armazenar suas senhas de maneira simples e descomplicada.",
                    color = Color(0xFFAAAAAA),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("signUp") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A5BFF))
                    ) {
                        Text(text = "Criar conta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { navController.navigate("login") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                        )
                    ) {
                        Text(text = "Entrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bottom - Terms and Policies
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ao continuar você concorda e aceita os nossos",
                    color = Color(0xFFAAAAAA),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                Row {
                    Text(
                        text = "Termos de Uso",
                        color = Color(0xFF3A5BFF),
                        fontSize = 15.sp,
                        modifier = Modifier.clickable { /* Navigate to Terms */ }
                    )
                }
            }
        }
    }
}