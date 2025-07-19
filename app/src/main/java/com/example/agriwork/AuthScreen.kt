import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agriwork.CountryCodeDialog
import com.example.agriwork.ui.components.PrimaryButton
import com.example.agriwork.ui.theme.AgriWorkTheme
import com.example.agriwork.ui.theme.Poppins
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class Country(
    val name: String,
    val code: String,
    var isSelected: Boolean = false
)

val countryList = listOf(
    Country("India", "+91"),
    Country("United States", "+1"),
    Country("United Kingdom", "+44"),
    Country("Germany", "+49"),
    Country("Australia", "+61")
)

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val context = LocalContext.current
    val activity = context as Activity

    var showDialog by remember { mutableStateOf(false) }
    var selectedCode by remember { mutableStateOf("+91") }
    val countries = remember { countryList }

    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 90.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding)
                .imePadding(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start

        )
        {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            )
            {
                Text(
                    text = "Let’s get started!",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter your mobile number to receive an OTP for verification.",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Light,
                    fontSize = 15.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .height(56.dp)
                            .align(Alignment.Bottom),
                        shape = RoundedCornerShape(
                            topStart = 8.dp,
                            bottomStart = 8.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp
                        )
                    ) {
                        Text(selectedCode)
                    }
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            if (it.length <= 10) phoneNumber = it
                        },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Phone
                        ),
                        singleLine = true,
                        enabled = !isOtpSent
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isOtpSent) {
                    OutlinedTextField(
                        value = otp,
                        onValueChange = {
                            if (it.length <= 6) otp = it
                        },
                        label = { Text("Enter OTP") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                if (showDialog) {
                    CountryCodeDialog(
                        countries = countries,
                        selectedCountryCode = selectedCode,
                        onDismiss = { showDialog = false },
                        onCountrySelected = {
                            selectedCode = it.code
                            showDialog = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isOtpSent) {
                    PrimaryButton(
                        onClick = {
                            if (otp.length == 6 && verificationId != null) {
                                isLoading = true
                                val credential =
                                    PhoneAuthProvider.getCredential(verificationId!!, otp)
                                auth.signInWithCredential(credential)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            message = "Login successful!"
                                            onLoginSuccess()
                                        } else {
                                            message = "Invalid OTP or error occurred."
                                        }
                                    }
                            } else {
                                message = "Please enter valid 6-digit OTP"
                            }
                        },
                        enabled = !isLoading,
                        text = "Verify OTP"
                    )

                } else {
                    PrimaryButton(
                        onClick = {
                            if (phoneNumber.length >= 10) {
                                isLoading = true
                                val options = PhoneAuthOptions.newBuilder(auth)
                                    .setPhoneNumber(selectedCode + phoneNumber)
                                    .setTimeout(60L, TimeUnit.SECONDS)
                                    .setActivity(activity)
                                    .setCallbacks(object :
                                        PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                            auth.signInWithCredential(credential)
                                                .addOnCompleteListener { task ->
                                                    isLoading = false
                                                    if (task.isSuccessful) {
                                                        coroutineScope.launch {
                                                            snackbarHostState.showSnackbar("Auto-login success!")
                                                        }
                                                        onLoginSuccess()
                                                    }
                                                }
                                        }

                                        override fun onVerificationFailed(e: FirebaseException) {
                                            isLoading = false
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Verification failed: ${e.localizedMessage}")
                                            }
                                        }

                                        override fun onCodeSent(
                                            verifId: String,
                                            token: PhoneAuthProvider.ForceResendingToken
                                        ) {
                                            isLoading = false
                                            isOtpSent = true
                                            verificationId = verifId
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("OTP sent to $phoneNumber")
                                            }
                                        }
                                    })
                                    .build()
                                PhoneAuthProvider.verifyPhoneNumber(options)
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Enter valid phone number")
                                }
                            }
                        },
                        enabled = !isLoading,
                        text = "Send OTP"
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true, name = "AuthScreen Preview")
@Composable
fun PreviewAuthScreen() {
    AgriWorkTheme {
        AuthScreen(
            onLoginSuccess = {},
            auth = FirebaseAuth.getInstance()
        )
    }
}
