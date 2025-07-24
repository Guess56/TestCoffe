package com.example.testcoffe.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import com.example.testcoffe.R
import com.example.testcoffe.domain.model.CafeItems
import com.example.testcoffe.domain.model.LocationId
import com.example.testcoffe.domain.model.Point
import com.example.testcoffe.utils.AsteriskPasswordVisualTransformation
import com.example.testcoffe.utils.LocationIdState
import com.example.testcoffe.utils.LocationState
import com.example.testcoffe.utils.LoginState
import com.example.testcoffe.utils.RegistrationState
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.geometry.Point as YandexPoint
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    private var mapView: MapView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val context = LocalContext.current

            var hasLocationPermission by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
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
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
            val locationViewModel: LocationViewModel = koinViewModel()
            val locationIdViewModel: LocationIdViewModel = koinViewModel()
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
                        userPoint = userPoint,
                        locationViewModel = locationViewModel,
                        navController = navController

                    )
                }
                composable("mapScreen") {
                    val locationState by locationViewModel.state.collectAsState()
                    val cafeItems = when (locationState) {
                        is LocationState.Content -> (locationState as LocationState.Content).data
                        else -> emptyList()
                    }
                    YandexMapScreen(
                        cafeItems = cafeItems,
                        onMarkerClick = { cafe ->
                            navController.navigate("cafeDetail/${cafe.id}")
                        },
                        onMapReady = { mapView = it }
                    )
                }
                composable(
                    route = "cafeDetail/{cafeId}",
                    arguments = listOf(navArgument("cafeId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val cafeId = backStackEntry.arguments?.getLong("cafeId") ?: 0L
                    CafeDetailScreen(
                        cafeId = cafeId,
                        locationIdViewModel = locationIdViewModel,
                        navController = navController
                    )
                }
                composable(
                    route = "checkout/{cafeId}",
                    arguments = listOf(navArgument("cafeId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val cafeId = backStackEntry.arguments?.getLong("cafeId") ?: 0L
                    CheckoutScreen(
                        cafeId = cafeId,
                        locationIdViewModel = locationIdViewModel,
                        navController = navController
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
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
    locationViewModel: LocationViewModel = koinViewModel(),
    navController: NavHostController
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    ) {

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
            onClick = { navController.navigate("mapScreen") },
            enable = cafeItems.isNotEmpty(),
            text = "На карте",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 16.dp),
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
fun RecyclerView(
    items: List<CafeItems>
) {
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
                        .padding(10.dp)
                        .clickable {
                            ////
                        }
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
    modifier: Modifier = Modifier,
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

@Composable
fun YandexMapScreen(
    cafeItems: List<CafeItems>,
    onMarkerClick: (CafeItems) -> Unit,
    onMapReady: (MapView) -> Unit
) {
    Log.e("items", "$cafeItems")
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            map.move(
                CameraPosition(
                    YandexPoint(cafeItems.first().latitude, cafeItems.first().longitude),
                    12f,
                    0f,
                    0f
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        onMapReady(mapView)
    }

    DisposableEffect(key1 = cafeItems) {
        val mapObjects = mapView.map.mapObjects
        mapObjects.clear()

        cafeItems.forEach { cafe ->
            mapObjects.addPlacemark { placemark ->
                placemark.geometry = YandexPoint(cafe.latitude, cafe.longitude)
                placemark.setIcon(
                    ImageProvider.fromResource(context, R.drawable.ic_marker)
                )
                placemark.addTapListener { _, _ ->
                    onMarkerClick(cafe)
                    true
                }
            }
        }
        onDispose { }
    }

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun CafeDetailScreen(
    onNavigateToLocation: () -> Unit = {},
    cafeId: Long,
    locationIdViewModel: LocationIdViewModel = koinViewModel(),
    navController: NavHostController
) {
    LaunchedEffect(cafeId) {
        locationIdViewModel.getMenu(cafeId.toInt())
    }

    val locationIdState by locationIdViewModel.state.collectAsState()

    val menu = when (locationIdState) {
        is LocationIdState.Content -> (locationIdState as LocationIdState.Content).data
        else -> emptyList<LocationId>()
    }
    Log.e("menu", "$menu")

    val quantities by locationIdViewModel.quantities.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                    fontFamily = FontFamily(Font(R.font.sfuidisplay_bold, FontWeight.Bold)),
                    text = "Меню"
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            CoffeeGrid(
                items = menu,
                quantities = quantities,
                onQuantityChange = { item, quantity ->
                    Log.d("CafeDetailScreen", "setQuantity: id=${item.id.toLong()} quantity=$quantity")
                    locationIdViewModel.setQuantity(item.id.toLong(), quantity)
                }
            )
        }
        val isAnySelected = quantities.values.any { it > 0 }


        SimpleLocationButton(
            onClick = {
                navController.navigate("checkout/$cafeId")
            },
            enable = isAnySelected,
            text = "Перейти к оплате",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 16.dp),
        )
    }
}

@Composable
fun CoffeeGrid(
    items: List<LocationId>,
    quantities: Map<Long, Int>,
    onQuantityChange: (LocationId, Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        items(items) { item ->
            CoffeeItemCard(
                item = item,
                quantity = quantities[item.id.toLong()] ?: 0,
                onQuantityChange = onQuantityChange
            )
        }
    }
}

@Composable
fun CoffeeItemCard(
    item: LocationId,
    quantity: Int,
    onQuantityChange: (LocationId, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(165.dp)
            .height(205.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .shadow(1.dp)


    ) {
        Image(
            painter = rememberImagePainter(item.imageUrl),
            contentDescription = item.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(137.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = item.name,
            modifier = Modifier.padding(start = 11.dp),
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = colorResource(id = R.color.text_location)

        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 11.dp, end = 11.dp)
        ) {

            Text(
                text = "${item.price} Руб",
                fontWeight = FontWeight.SemiBold,
                color = colorResource(id = R.color.title),
                fontSize = 14.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 11.dp)
            )


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {
                        if (quantity > 0) {
                            onQuantityChange(item, quantity - 1)
                        }
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .padding(bottom = 11.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_remove),
                        contentDescription = "Уменьшить",
                        tint = Color.Unspecified
                    )
                }
                Spacer(modifier = Modifier.width(9.dp))
                Text(
                    text = quantity.toString(),
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.title),
                    modifier = Modifier.padding(bottom = 11.dp)
                )
                Spacer(modifier = Modifier.width(9.dp))
                IconButton(
                    onClick = {
                        onQuantityChange(item, quantity + 1)
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .padding(bottom = 11.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_add),
                        contentDescription = "Увеличить",
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

@Composable
fun CheckoutScreen(
    cafeId: Long,
    navController: NavHostController,
    locationIdViewModel: LocationIdViewModel = koinViewModel()
) {
    LaunchedEffect(cafeId) {
        locationIdViewModel.getMenu(cafeId.toInt())
    }

    val locationIdState by locationIdViewModel.state.collectAsState()
    val quantities by locationIdViewModel.quantities.collectAsState()

    val menu = when (locationIdState) {
        is LocationIdState.Content -> (locationIdState as LocationIdState.Content).data
        else -> emptyList()
    }

    val selectedItems = menu.filter { (quantities[it.id.toLong()] ?: 0) > 0 }

    if (menu.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Загрузка меню...")
        }
        return
    }

    if (selectedItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Ваша корзина пуста")
        }
        return
    }

    CheckoutList(
        items = selectedItems,
        quantities = quantities,
        onQuantityChange = { item, quantity ->
            locationIdViewModel.setQuantity(item.id.toLong(), quantity)
        },
        onBackClick ={ navController.popBackStack() },
        onPayClick = {
            // Логика для перехода к оплате
        }
    )
}
@Composable
fun CheckoutList(
    items: List<LocationId>,
    quantities: Map<Long, Int>,
    onQuantityChange: (LocationId, Int) -> Unit,
    onBackClick: () -> Unit,
    onPayClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(top = 55.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(start = 20.dp)
                    .size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_back),
                    contentDescription = "Назад",
                    tint = colorResource(id = R.color.title)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Ваш заказ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.title),
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.height(26.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(items) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(5.dp), clip = false)
                        .border(
                            width = 2.dp,
                            color = colorResource(id= R.color.button_text_color),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .background(
                            color = colorResource(id = R.color.button_text_color),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(10.dp)
                ) {
                    val quantity = quantities[item.id.toLong()] ?: 0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.title)
                            )
                            Text(
                                text = "${item.price} Руб",
                                fontSize = 16.sp,
                                color = colorResource(id = R.color.text_location)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (quantity > 0) onQuantityChange(item, quantity - 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_remove),
                                    contentDescription = "Уменьшить количество",
                                    tint = colorResource(id = R.color.title)
                                )
                            }

                            Text(
                                text = quantity.toString(),
                                modifier = Modifier.padding(horizontal = 12.dp),
                                fontSize = 16.sp,
                                color = colorResource(R.color.title)
                            )

                            IconButton(
                                onClick = { onQuantityChange(item, quantity + 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_add),
                                    contentDescription = "Увеличить количество",
                                    tint = colorResource(id = R.color.title)
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "Время ожидания заказа\n15 минут!\nСпасибо, что выбрали нас!",
            fontSize = 24.sp,
            color = colorResource(id = R.color.title),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 158.dp),
            textAlign = TextAlign.Center
        )

        SimpleLocationButton(
            onClick = {},
            enable = true,
            text = "Перейти к оплате",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 16.dp),
        )
    }
}


