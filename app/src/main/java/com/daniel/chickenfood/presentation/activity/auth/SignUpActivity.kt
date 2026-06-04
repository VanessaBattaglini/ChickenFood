package com.daniel.chickenfood.presentation.activity.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.daniel.chickenfood.R
import com.daniel.chickenfood.domain.model.UserTokenModel
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.activity.dashboard.MainActivity
import com.daniel.chickenfood.presentation.viewModel.TokenState
import com.daniel.chickenfood.presentation.viewModel.TokenViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "SignUpActivity"

class SignUpActivity : BaseActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val tokenViewModel: TokenViewModel by viewModel()

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "Google Sign-Up exitoso: ${account.email}")
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.e(TAG, "Google Sign-Up falló: ${e.statusCode}", e)
            Toast.makeText(this, "Error en autenticación: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Observar cambios en el estado de guardado de token
        lifecycleScope.launch {
            tokenViewModel.saveTokenState.collect { state ->
                when (state) {
                    is TokenState.Success -> {
                        Log.d(TAG, "Token guardado exitosamente")
                        Toast.makeText(this@SignUpActivity, "Autenticación exitosa", Toast.LENGTH_SHORT).show()
                        navigateToDashboard()
                    }
                    is TokenState.Error -> {
                        Log.e(TAG, "Error guardando token: ${state.message}")
                        Toast.makeText(this@SignUpActivity, "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }

        setContent {
            SignUpScreen(
                onGoogleSignUpClick = { signUpWithGoogle() },
                onBackClick = { finish() }
            )
        }
    }

    private fun signUpWithGoogle() {
        Log.d(TAG, "Iniciando Google Sign-Up")
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d(TAG, "Autenticando con Firebase usando Google ID Token")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    Log.d(TAG, "Firebase Auth exitoso: ${user?.email}")
                    
                    // Obtener el token de Firebase
                    user?.getIdToken(false)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val firebaseToken = tokenTask.result?.token ?: ""
                            Log.d(TAG, "Firebase token obtenido: ${firebaseToken.take(20)}...")
                            
                            // Crear modelo de token con toda la información
                            val userToken = UserTokenModel(
                                userId = user.uid,
                                email = user.email ?: "",
                                googleIdToken = idToken,
                                firebaseToken = firebaseToken,
                                refreshToken = "", // Firebase lo maneja internamente
                                displayName = user.displayName ?: "",
                                photoUrl = user.photoUrl?.toString() ?: "",
                                createdAt = System.currentTimeMillis(),
                                lastLoginAt = System.currentTimeMillis(),
                                expiresAt = 0L, // Firebase maneja la expiración
                                isActive = true
                            )
                            
                            Log.d(TAG, "Guardando token en Firebase para usuario: ${user.uid}")
                            // Guardar token usando TokenViewModel
                            tokenViewModel.saveUserToken(userToken)
                        } else {
                            Log.e(TAG, "Error obteniendo Firebase token: ${tokenTask.exception?.message}")
                            Toast.makeText(this, "Error obteniendo token", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e(TAG, "Firebase Auth falló: ${task.exception?.message}")
                    Toast.makeText(this, "Error en autenticación: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToDashboard() {
        Log.d(TAG, "Navegando al Dashboard")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}


@Composable
fun SignUpScreen(
    onGoogleSignUpClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.darkBrown))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Botón de atrás
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.back_grey),
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Inscribete",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    color = colorResource(R.color.orange),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Text(
                    text = "Autenticando...",
                    fontSize = 16.sp,
                    color = Color.White
                )
            } else {
                // Botón "Continuar con Google"
                Button(
                    onClick = {
                        isLoading = true
                        onGoogleSignUpClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1F7FE8) // Azul de Google
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Logo de Google
                        Image(
                            painter = painterResource(R.drawable.ic_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier
                                .height(24.dp)
                                .padding(end = 12.dp)
                        )
                        
                        Text(
                            text = "Continuar con Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
