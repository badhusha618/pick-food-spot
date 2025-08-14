package com.pickfoodplace.wear.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.pickfoodplace.wear.data.local.PlaceEntity
import com.pickfoodplace.wear.ui.viewmodel.AuthViewModel
import com.pickfoodplace.wear.ui.viewmodel.BookingViewModel
import com.pickfoodplace.wear.ui.viewmodel.PlacesViewModel
import com.pickfoodplace.wear.ui.viewmodel.SplashViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.Bitmap

@Composable
fun SplashScreen(onFinished: (Boolean) -> Unit, vm: SplashViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.checkAuth()
    }
    val isLoggedIn by vm.isLoggedIn.collectAsState()
    LaunchedEffect(isLoggedIn) {
        onFinished(isLoggedIn)
    }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("PickFoodPlace", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Loading...")
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onRegister: () -> Unit, vm: AuthViewModel = hiltViewModel()) {
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        WearTextField(label = "Email", value = emailState.value) { emailState.value = it }
        Spacer(Modifier.height(6.dp))
        WearTextField(label = "Password", value = passwordState.value, password = true) { passwordState.value = it }
        Spacer(Modifier.height(10.dp))
        Button(onClick = { vm.login(emailState.value, passwordState.value, onLoginSuccess) }, enabled = !loading) {
            Text(if (loading) "..." else "Login")
        }
        Spacer(Modifier.height(6.dp))
        Chip(onClick = onRegister, label = { Text("Register") })
        if (error != null) {
            Spacer(Modifier.height(6.dp))
            Text(error ?: "", color = Color(0xFFCF6679))
        }
    }
}

@Composable
fun RegisterScreen(onRegistered: () -> Unit, onBack: () -> Unit, vm: AuthViewModel = hiltViewModel()) {
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Chip(onClick = onBack, label = { Text("Back") }, icon = { Icon(Icons.Rounded.ArrowBack, contentDescription = null) })
            Text("Register", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        WearTextField(label = "Email", value = emailState.value) { emailState.value = it }
        Spacer(Modifier.height(6.dp))
        WearTextField(label = "Password", value = passwordState.value, password = true) { passwordState.value = it }
        Spacer(Modifier.height(10.dp))
        Button(onClick = { vm.register(emailState.value, passwordState.value, onRegistered) }, enabled = !loading) {
            Text(if (loading) "..." else "Create Account")
        }
        if (error != null) {
            Spacer(Modifier.height(6.dp))
            Text(error ?: "", color = Color(0xFFCF6679))
        }
    }
}

@Composable
fun NearbyPlacesScreen(
    onOpenSearch: () -> Unit,
    onOpenDetails: (String) -> Unit,
    onOpenBookings: () -> Unit,
    onOpenSettings: () -> Unit,
    vm: PlacesViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { vm.load(null, null, null, null) }
    val places by vm.places.collectAsState()

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Chip(onClick = onOpenSearch, label = { Text("Search") }, icon = { Icon(Icons.Rounded.Search, contentDescription = null) })
            Chip(onClick = onOpenBookings, label = { Text("Bookings") })
            Chip(onClick = onOpenSettings, label = { Text("Settings") })
        }
        Spacer(Modifier.height(6.dp))
        LazyColumn {
            items(places) { place ->
                PlaceCard(place) { onOpenDetails(place.id) }
            }
        }
    }
}

@Composable
fun PlaceCard(place: PlaceEntity, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(8.dp)) {
            Text(place.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(place.location, fontSize = 12.sp, color = Color.LightGray)
            Text("$${place.hourlyRate}/hr", fontSize = 12.sp, color = MaterialTheme.colors.primary)
        }
    }
}

@Composable
fun SearchScreen(onBack: () -> Unit, vm: PlacesViewModel = hiltViewModel()) {
    val location = remember { mutableStateOf("") }
    val minPrice = remember { mutableStateOf("") }
    val maxPrice = remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Chip(onClick = onBack, label = { Text("Back") }, icon = { Icon(Icons.Rounded.ArrowBack, contentDescription = null) })
            Text("Search", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        WearTextField(label = "Location", value = location.value) { location.value = it }
        Spacer(Modifier.height(6.dp))
        WearTextField(label = "Min Price", value = minPrice.value) { minPrice.value = it }
        Spacer(Modifier.height(6.dp))
        WearTextField(label = "Max Price", value = maxPrice.value) { maxPrice.value = it }
        Spacer(Modifier.height(10.dp))
        Button(onClick = {
            val min = minPrice.value.toIntOrNull()
            val max = maxPrice.value.toIntOrNull()
            vm.load(null, null, min, max, if (location.value.isBlank()) null else location.value)
        }) { Text("Apply") }
    }
}

@Composable
fun PlaceDetailsScreen(placeId: String, onBooked: (String) -> Unit, onBack: () -> Unit, vm: BookingViewModel = hiltViewModel()) {
    val selectedSlot = remember { mutableStateOf("10:00-11:00") }

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Chip(onClick = onBack, label = { Text("Back") }, icon = { Icon(Icons.Rounded.ArrowBack, contentDescription = null) })
            Text("Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text("Place #$placeId")
        Spacer(Modifier.height(6.dp))
        Text("Select a time slot:")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("10:00-11:00", "11:00-12:00", "12:00-13:00").forEach { slot ->
                Chip(onClick = { selectedSlot.value = slot }, label = { Text(slot) }, colors = ChipDefaults.chipColors(
                    backgroundColor = if (selectedSlot.value == slot) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                ))
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = { vm.create(placeId, selectedSlot.value) { onBooked(it.id) } }) { Text("Book Now") }
    }
}

@Composable
fun BookingConfirmationScreen(bookingId: String, onDone: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Booking Confirmed", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text("Booking #$bookingId")
        Spacer(Modifier.height(8.dp))
        val bitmap = remember(bookingId) { generateQrCodeBitmap(bookingId) }
        if (bitmap != null) {
            androidx.compose.foundation.Image(bitmap = bitmap.asImageBitmap(), contentDescription = "QR", modifier = Modifier.size(120.dp))
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onDone) { Text("Done") }
    }
}

private fun generateQrCodeBitmap(data: String): Bitmap? {
    return try {
        val matrix: BitMatrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 256, 256)
        val width = matrix.width
        val height = matrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (matrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }
        bmp
    } catch (e: Exception) {
        null
    }
}

@Composable
fun UpcomingBookingsScreen(onBack: () -> Unit, vm: BookingViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { vm.refresh() }
    val bookings by vm.bookings.collectAsState()

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Chip(onClick = onBack, label = { Text("Back") }, icon = { Icon(Icons.Rounded.ArrowBack, contentDescription = null) })
            Text("Upcoming", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        LazyColumn {
            items(bookings) { b ->
                Card(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(8.dp)) {
                        Text(b.placeName, fontWeight = FontWeight.Bold)
                        Text(b.slot, color = Color.LightGray)
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Chip(onClick = { vm.cancel(b.id) {} }, label = { Text("Cancel") })
                            Chip(onClick = { }, label = { Text("QR") }, icon = { Icon(Icons.Rounded.Check, contentDescription = null) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(onLogout: () -> Unit, onBack: () -> Unit, vm: AuthViewModel = hiltViewModel()) {
    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Chip(onClick = onBack, label = { Text("Back") }, icon = { Icon(Icons.Rounded.ArrowBack, contentDescription = null) })
            Text("Settings", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { vm.logout(); onLogout() }) { Text("Logout") }
    }
}

@Composable
fun WearTextField(label: String, value: String, password: Boolean = false, onValueChange: (String) -> Unit) {
    androidx.wear.compose.material.TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (password) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None
    )
}