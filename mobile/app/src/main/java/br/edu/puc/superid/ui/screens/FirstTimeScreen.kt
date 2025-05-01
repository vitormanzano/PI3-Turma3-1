import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch

@Composable
fun FirstTimeScreen(navController: NavHostController) {
    val pages = listOf(
        "SuperID oferece uma solução prática e segura para login sem senhas em sites",
        "Armazene suas senhas de forma criptografada e acessível em um único lugar",
        "Esqueça a dificuldade de gerenciar várias senhas, use SuperID para maior segurança e agilidade"
    )

    val images = listOf(
        R.drawable.person,
        R.drawable.lock,
        R.drawable.thinking_person
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        // Contêiner principal com o conteúdo
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 72.dp), // Deixe espaço para o botão
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f) // O conteúdo ocupa o restante do espaço
            ) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = images[page]),
                        contentDescription = "Imagem da página $page",
                        modifier = Modifier
                            .height(400.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text = pages[page],
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )
                }
            }

            PagerIndicator(
                size = pages.size,
                currentPage = pagerState.currentPage
            )
        }

        // Botão "Próximo" sempre na parte inferior, com um pequeno espaço
        Button(
            onClick = {
                scope.launch {
                    if (pagerState.currentPage < pages.lastIndex) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        navController.navigate("createAccount")                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3366FF)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter) // Fixando o botão na parte inferior
        ) {
            Text(text = "Próximo", fontSize = 16.sp)
        }
    }
}

@Composable
fun PagerIndicator(
    size: Int,
    currentPage: Int
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(32.dp)
    ) {
        repeat(size) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(8.dp)
                    .width(if (index == currentPage) 16.dp else 32.dp) // Agora o inverso!
                    .background(
                        color = if (index == currentPage) Color(0xFF3366FF) else Color.White,
                        shape = CircleShape
                    )
            )
        }
    }
}




