package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var showTerms by remember { mutableStateOf(false) }

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
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .padding(bottom = 32.dp),
                    contentScale = ContentScale.Crop
                )


                // Title
                Text(
                    text = "CRIE SUA\nCONTA SUPERID",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "SuperID é uma ferramenta que te permite armazenar suas senhas de maneira simples e descomplicada.",
                    color = Color(0xFFAAAAAA),
                    fontSize = 18.sp,
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
                        Text(text = "CRIAR CONTA", fontSize = 22.sp, fontWeight = FontWeight.Bold)
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
                        Text(text = "ENTRAR", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bottom - Terms and Policies
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AO CONTINUAR VOCÊ CONCORDA E ACEITA OS NOSSOS ",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "TERMOS DE USO",
                    color = Color(0xFF3A5BFF),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { showTerms = true }
                )
            }
        }
    }

    if (showTerms) {
        AlertDialog(
            onDismissRequest = { showTerms = false },
            confirmButton = {
                Button(
                    onClick = { showTerms = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3366FF), // Cor de fundo
                        contentColor = Color.White // Cor do texto
                    )
                ) {
                    Text("Fechar")
                }
            },
            title = {
                Text(
                    text = "Termos de Uso",
                    color = Color(0xFF3366FF),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .height(300.dp) // Altura reduzida
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = """
                            Bem-vindo ao SuperID. Ao utilizar nosso serviço, você concorda com estes Termos de Uso. Por favor, leia-os atentamente.

                            1. Aceitação dos Termos
                            Ao acessar ou utilizar o Aplicativo, você concorda em cumprir estes Termos de Uso e a nossa Política de Privacidade. Se você não concordar com qualquer parte dos termos, não utilize o Aplicativo.

                            2. Uso do Aplicativo
                            O SuperId permite que você armazene e gerencie suas senhas de forma segura. Ao utilizar o Aplicativo, você concorda em:
                            • Fornecer informações verdadeiras e atualizadas.
                            • Utilizar o Aplicativo apenas para fins legais e pessoais.
                            • Não compartilhar ou revender o Aplicativo.

                            3. Segurança
                            Adotamos práticas como criptografia de dados e autenticação. Ainda assim, você reconhece que o uso é por sua conta e risco.

                            4. Responsabilidades do Usuário
                            Você é responsável por manter a confidencialidade do seu login e senha.

                            5. Propriedade Intelectual
                            Todos os direitos são do Desenvolvedor. Não copie ou modifique sem autorização.

                            6. Modificações e Encerramento
                            Podemos modificar ou descontinuar o Aplicativo a qualquer momento. O uso contínuo implica aceitação das mudanças.

                            7. Limitação de Responsabilidade
                            Não nos responsabilizamos por danos resultantes do uso ou da incapacidade de usar o Aplicativo.

                            8. Contato
                            Dúvidas? vitor.mvd@puccampinas.edu.br
                        """.trimIndent(),
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            },
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = Color (0xFF3366FF),
                    shape = RoundedCornerShape(16.dp)
                ),
            containerColor = Color.DarkGray,
            shape = RoundedCornerShape(16.dp)
        )
    }
}