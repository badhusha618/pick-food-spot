package com.pickfoodplace.wear.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pickfoodplace.wear.data.local.BookingEntity
import com.pickfoodplace.wear.data.local.PlaceEntity
import com.pickfoodplace.wear.repository.AuthRepository
import com.pickfoodplace.wear.repository.BookingRepository
import com.pickfoodplace.wear.repository.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun checkAuth() {
        _isLoggedIn.value = authRepository.isLoggedIn()
    }
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            val res = authRepository.login(email, password)
            _loading.value = false
            res.onSuccess { onSuccess() }.onFailure { _error.value = it.message }
        }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            val res = authRepository.register(email, password)
            _loading.value = false
            res.onSuccess { onSuccess() }.onFailure { _error.value = it.message }
        }
    }

    fun logout() = authRepository.logout()
}

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val placeRepository: PlaceRepository
) : ViewModel() {
    private val _places = MutableStateFlow<List<PlaceEntity>>(emptyList())
    val places: StateFlow<List<PlaceEntity>> = _places.asStateFlow()

    fun load(lat: Double?, lng: Double?, minPrice: Int?, maxPrice: Int?, query: String? = null) {
        viewModelScope.launch {
            placeRepository.getPlaces(lat, lng, minPrice, maxPrice, query).collect { _places.value = it }
        }
    }
}

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {
    private val _bookings = MutableStateFlow<List<BookingEntity>>(emptyList())
    val bookings: StateFlow<List<BookingEntity>> = _bookings

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun refresh() {
        viewModelScope.launch {
            val res = bookingRepository.getBookings()
            res.onSuccess { _bookings.value = it }.onFailure { _error.value = it.message }
        }
    }

    fun create(placeId: String, slot: String, onSuccess: (BookingEntity) -> Unit) {
        viewModelScope.launch {
            val res = bookingRepository.createBooking(placeId, slot)
            res.onSuccess {
                onSuccess(it)
                refresh()
            }.onFailure { _error.value = it.message }
        }
    }

    fun cancel(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val res = bookingRepository.cancelBooking(id)
            res.onSuccess { onSuccess(); refresh() }.onFailure { _error.value = it.message }
        }
    }
}