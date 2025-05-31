package br.edu.puc.superid.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import br.edu.puc.superid.auth.AuthHandler
import br.edu.puc.superid.confirmLogin
import br.edu.puc.superid.database.FirestoreHandler
import br.edu.puc.superid.validQRCode
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun QRCodeScannerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val authHandler = AuthHandler()
    val user = authHandler.obterUser()
    val isEmailVerified = remember { mutableStateOf(user?.isEmailVerified == true) }

    var hasCameraPermission by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(1) }

    var lastScannedCode by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            hasCameraPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(hasCameraPermission) {
        if (!hasCameraPermission) return@LaunchedEffect

        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val barcodeScanner = BarcodeScanning.getClient()
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        var lastAnalyzedTime = 0L

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
            try {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastAnalyzedTime >= 2000L) {
                    lastAnalyzedTime = currentTime

                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        barcodeScanner.process(inputImage)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    barcode.rawValue?.let { value ->
                                        if (value.length == 256 && value != lastScannedCode) {
                                            lastScannedCode = value
                                            Log.d("QRCodeScanner", "QR Code lido: $value")

                                            validQRCode(
                                                loginToken = value,
                                                onSuccess = {
                                                    confirmLogin(value)
                                                    messageText = "Login confirmado com sucesso!"
                                                    showMessage = true
                                                },
                                                onFailure = {
                                                    messageText = "QR Code inválido ou já utilizado."
                                                    showMessage = true
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Log.e("QRCodeScanner", "Erro ao ler QR Code", it)
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                } else {
                    imageProxy.close()
                }
            } catch (e: Exception) {
                imageProxy.close()
            }
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    if (hasCameraPermission) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    selectedIndex = selectedIndex,
                    isEmailVerified = isEmailVerified.value,
                    onItemSelected = { index -> selectedIndex = index }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                )
                ScannerOverlay()

                if (showMessage) {
                    Snackbar(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        action = {
                            TextButton(onClick = { showMessage = false }) {
                                Text("OK")
                            }
                        }
                    ) {
                        Text(messageText)
                    }
                }
            }
        }
    }
}

@Composable
fun ScannerOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                drawContent()

                val rectSize = size.width * 0.7f
                val left = (size.width - rectSize) / 2
                val top = (size.height - rectSize) / 2
                val right = left + rectSize
                val bottom = top + rectSize

                drawRect(color = Color(0xAA000000))

                drawRect(
                    color = Color.Transparent,
                    topLeft = Offset(left, top),
                    size = androidx.compose.ui.geometry.Size(rectSize, rectSize),
                    blendMode = BlendMode.Clear
                )

                drawLine(Color.White, Offset(left, top), Offset(left + 40f, top), strokeWidth = 5f)
                drawLine(Color.White, Offset(left, top), Offset(left, top + 40f), strokeWidth = 5f)
                drawLine(Color.White, Offset(right, top), Offset(right - 40f, top), strokeWidth = 5f)
                drawLine(Color.White, Offset(right, top), Offset(right, top + 40f), strokeWidth = 5f)
                drawLine(Color.White, Offset(left, bottom), Offset(left + 40f, bottom), strokeWidth = 5f)
                drawLine(Color.White, Offset(left, bottom), Offset(left, bottom - 40f), strokeWidth = 5f)
                drawLine(Color.White, Offset(right, bottom), Offset(right - 40f, bottom), strokeWidth = 5f)
                drawLine(Color.White, Offset(right, bottom), Offset(right, bottom - 40f), strokeWidth = 5f)
            }
    )
}
