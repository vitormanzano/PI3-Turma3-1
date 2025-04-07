package br.edu.puc.superid.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp


// TO DO fazer a parte de login utilizando o auth
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit = {}) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    val backgroundColor = Color(0xFF102952) // fundo azul escuro
    val buttonColor = Color(0xFF00D7FF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Lock Icon",
                    tint = Color.White,
                    modifier = Modifier.size(55.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Super ID",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(
                    color = Color.Gray,
                    text = "E-mail") },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Email, contentDescription = null, tint = buttonColor)
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = buttonColor,
                    unfocusedBorderColor = Color.DarkGray,
                    cursorColor = buttonColor
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text(
                    color = Color.Gray,
                    text="Senha mestra") },
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = buttonColor)
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = buttonColor,
                    unfocusedBorderColor = Color.DarkGray,
                    cursorColor = buttonColor
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLoginSuccess,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text("ENTRAR", fontWeight = FontWeight.Bold, color = backgroundColor)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ainda não possui uma conta?",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginSuccess = {})
}