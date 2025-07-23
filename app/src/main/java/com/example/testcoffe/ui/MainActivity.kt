package com.example.testcoffe.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testcoffe.R
import com.example.testcoffe.domain.model.CafeItems
import com.example.testcoffe.domain.model.Point
import com.example.testcoffe.utils.AsteriskPasswordVisualTransformation
import com.example.testcoffe.utils.LocationState
import com.example.testcoffe.utils.LoginState
import com.example.testcoffe.utils.RegistrationState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

        }
        enableEdgeToEdge()
        setContent {

            val context = LocalContext.current

            var hasLocationPermission by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }
            var error by remember { mutableStateOf<String?>(null) }
            var latitude by remember { mutableStateOf<String?>(null) }
            var longitude by remember { mutableStateOf<String?>(null) }
            var userPoint by remember { mutableStateOf<Point?>(null) }

            val locationPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                hasLocationPermission = granted
                if (!granted) {
                    error = "Разрешение на геолокацию не предоставлено"
                }
            }

            LaunchedEffect(Unit) {
                if (!hasLocationPermission) {
                    locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            if (hasLocationPermission) {
                LaunchedEffect(Unit) {
                    try {
                        val fusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(context)
                        val location = fusedLocationClient.lastLocation.await()
                        if (location != null) {
                            userPoint = Point(
                                latitude = location.latitude,
                                longitude = location.longitude
                            )
                        } else {
                            error = "Локация недоступна"
                        }
                    } catch (e: SecurityException) {
                        error = "Ошибка разрешений: ${e.localizedMessage}"
                    } catch (e: Exception) {
                        error = "Ошибка получения локации: ${e.localizedMessage}"
                    }
                }
            }


            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "register") {
                composable("login") {
                    LoginScreen(
                        onNavigateToRegister = { navController.navigate("register") },
                        onNavigateToCafe = { navController.navigate("cafe") }
                    )
                }
                composable("register") {
                    RegisterScreen(
                        onNavigateToLogin = { navController.navigate("login") }
                    )
                }
                composable("cafe") {
                    CafeScreen(
                        onNavigateToLogin = { navController.navigate("login") },
                        userPoint = userPoint
                    )
                }
            }
        }
    }
}


@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    registrationViewModel: RegistrationViewModel = koinViewModel()
) {
    val registrationState by registrationViewModel.state.collectAsState()
    when (registrationState) {
        is RegistrationState.Idle -> {
        }

        is RegistrationState.Content -> {
            onNavigateToLogin()
        }

        is RegistrationState.Error -> {
            val context = LocalContext.current
            val message = (registrationState as RegistrationState.Error).message
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            onNavigateToLogin()
        }

        is RegistrationState.Empty -> {
            val context = LocalContext.current
            val message = (registrationState as RegistrationState.Empty).message
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val sfUiDisplayBold = FontFamily(
        Font(R.font.sfuidisplay_bold, weight = FontWeight.Bold)
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordSecond by remember { mutableStateOf("") }


    val emailIsValid =
        email.trim().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val isEmailError by remember(email) {
        derivedStateOf {
            email.trim().isNotEmpty() && !emailIsValid
        }
    }
    val passwordIsValid = password.length >= 8
    val isPasswordError by remember(password) {
        derivedStateOf { password.isNotEmpty() && !passwordIsValid }
    }
    val isPasswordSecondError by remember(password) {
        derivedStateOf { password.isNotEmpty() && passwordSecond.isNotEmpty() && password != passwordSecond }
    }
    val buttonIsEnable by remember(email, password, passwordSecond) {
        derivedStateOf {
            email.isNotBlank() &&
                    password.length >= 8 &&
                    password == passwordSecond
        }
    }

    Column(modifier = Modifier.padding(horizontal = 18.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 55.dp),
            contentAlignment = Alignment.TopCenter

        ) {
            Text(
                color = colorResource(id = R.color.title),
                fontSize = 18.sp,
                fontFamily = sfUiDisplayBold,
                text = "Регистрация"
            )
        }
        Spacer(modifier = Modifier.height(190.dp))

        // Email Input
        CustomTextInputField(
            value = email,
            onValueChange = { email = it },
            labelText = "Email",
            placeholderText = "Введите email",
            keyboardType = KeyboardType.Email,
            isError = isEmailError,
            errorMessage = if (isEmailError) "Введите корректный email" else null,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Password Input
        CustomTextInputField(
            value = password,
            onValueChange = { password = it },
            labelText = "Пароль",
            placeholderText = "Введите пароль",
            keyboardType = KeyboardType.Password,
            isError = isPasswordError,
            errorMessage = if (isPasswordError) "Пароль должен быть не менее 8 символов" else null,
            visualTransformation = AsteriskPasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(20.dp))
        // PasswordSecond Input
        CustomTextInputField(
            value = passwordSecond,
            onValueChange = { passwordSecond = it },
            labelText = "Повторите пароль",
            placeholderText = "Повторите пароль",
            keyboardType = KeyboardType.Password,
            isError = isPasswordSecondError,
            errorMessage = if (isPasswordSecondError) "Пароли не совпадают" else null,
            visualTransformation = AsteriskPasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(30.dp))

        SimpleButton(
            onClick = {
                registrationViewModel.registration(email, password)
            },
            enable = buttonIsEnable,
            text = "Регистрация"
        )
    }
}


@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToCafe: () -> Unit,
    loginViewModel: LoginViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val loginState by loginViewModel.state.collectAsState()
    when (loginState) {
        is LoginState.Idle -> {
        }

        is LoginState.Content -> {
            onNavigateToCafe()
        }

        is LoginState.Error -> {
            val message = (loginState as LoginState.Error).message
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        is LoginState.Empty -> {
            val message = (loginState as LoginState.Empty).message
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val emailIsValid =
        email.trim().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val isEmailError by remember(email) {
        derivedStateOf {
            email.trim().isNotEmpty() && !emailIsValid
        }
    }
    val passwordIsValid = password.length >= 8
    val isPasswordError by remember(password) {
        derivedStateOf { password.isNotEmpty() && !passwordIsValid }
    }
    val buttonIsEnable by remember(email, password) {
        derivedStateOf {
            email.isNotBlank() &&
                    password.length >= 8
        }
    }
    val sfUiDisplayBold = FontFamily(
        Font(R.font.sfuidisplay_bold, weight = FontWeight.Bold)
    )
    Column(modifier = Modifier.padding(horizontal = 18.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 55.dp),
            contentAlignment = Alignment.TopCenter

        ) {
            Text(
                color = colorResource(id = R.color.title),
                fontSize = 18.sp,
                fontFamily = sfUiDisplayBold,
                text = "Вход"
            )
        }
        Spacer(modifier = Modifier.height(190.dp))

        // Email Input
        CustomTextInputField(
            value = email,
            onValueChange = { email = it },
            labelText = "Email",
            placeholderText = "Введите email",
            keyboardType = KeyboardType.Email,
            isError = isEmailError,
            errorMessage = if (isEmailError) "Введите корректный email" else null,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Password Input
        CustomTextInputField(
            value = password,
            onValueChange = { password = it },
            labelText = "Пароль",
            placeholderText = "Введите пароль",
            keyboardType = KeyboardType.Password,
            isError = isPasswordError,
            errorMessage = if (isPasswordError) "Пароль должен быть не менее 8 символов" else null,
            visualTransformation = AsteriskPasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(30.dp))

        SimpleButton(
            onClick = {
                loginViewModel.login(email, password)
            },
            enable = buttonIsEnable,
            text = "Войти",
        )
    }
}


@Composable
fun CafeScreen(
    onNavigateToLogin: () -> Unit,
    userPoint: Point?,
    locationViewModel: LocationViewModel = koinViewModel()
) {
    Log.e("user", "$userPoint")

    LaunchedEffect(Unit) {
        if (userPoint != null) {
            locationViewModel.getLocation(userPoint)
        }
    }

    val locationState by locationViewModel.state.collectAsState()


    val cafeItems = when (locationState) {
        is LocationState.Content -> (locationState as LocationState.Content).data
        else -> emptyList<CafeItems>()
    }

    val sfUiDisplayBold = FontFamily(
        Font(R.font.sfuidisplay_bold, weight = FontWeight.Bold)
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 18.dp)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 55.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    color = colorResource(id = R.color.title),
                    fontSize = 18.sp,
                    fontFamily = sfUiDisplayBold,
                    text = "Ближайшие кофейни"
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            RecyclerView(items = cafeItems)
        }

        SimpleLocationButton(
            onClick = { /* TODO обработка */ },
            enable = false,
            text = "На карте",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 16.dp)
        )
    }
}


@Composable
fun CustomTextInputField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    placeholderText: String,
    keyboardType: KeyboardType,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val customColor = colorResource(id = R.color.title)
    val colors = OutlinedTextFieldDefaults.colors().run {
        copy(
            errorTextColor = Color.Red,
            errorPlaceholderColor = Color.Red,
            errorCursorColor = Color.Red,
            errorLabelColor = Color.Red,
            errorSupportingTextColor = Color.Red,
            errorIndicatorColor = Color.Red,
            errorLeadingIconColor = Color.Red,
            disabledTextColor = customColor,
            focusedTextColor = customColor,
            unfocusedTextColor = customColor,
            focusedIndicatorColor = customColor,
            unfocusedIndicatorColor = customColor,
            focusedPlaceholderColor = customColor,
            unfocusedPlaceholderColor = customColor,
            unfocusedLabelColor = customColor,
            focusedSupportingTextColor = customColor,
            focusedLabelColor = customColor
        )
    }

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(labelText) },
            placeholder = { Text(placeholderText) },
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = colors,
            visualTransformation = visualTransformation,
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SimpleButton(
    onClick: () -> Unit,
    enable: Boolean,
    text: String
) {
    Button(
        onClick = onClick,
        enabled = enable,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = colorResource(id = R.color.button_text_color),
            containerColor = colorResource(id = R.color.button_color),
            disabledContainerColor = colorResource(id = R.color.button_color_lock),
            disabledContentColor = colorResource(id = R.color.button_text_color)
        ),
        border = BorderStroke(1.dp, Color.White)

    ) {
        Text(text = "$text")
    }
}

@Composable
fun RecyclerView(items: List<CafeItems>) {
    Column(
        modifier = Modifier

    ) {
        LazyColumn {
            items(items) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(349.dp)
                        .height(71.dp)
                        .padding(bottom = 8.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(5.dp), clip = false)
                        .border(
                            width = 2.dp,
                            color = colorResource(id = R.color.button_text_color),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .background(
                            color = colorResource(id = R.color.button_text_color),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(10.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.title)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.distance,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(id = R.color.text_location)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleLocationButton(
    onClick: () -> Unit,
    enable: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enable,
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            contentColor = colorResource(id = R.color.button_text_color),
            containerColor = colorResource(id = R.color.button_color),
            disabledContainerColor = colorResource(id = R.color.button_color_lock),
            disabledContentColor = colorResource(id = R.color.button_text_color)
        ),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Text(text = text)
    }
}
