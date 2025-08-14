package com.pickfoodplace.wear.ui.viewmodel

import com.pickfoodplace.wear.data.local.BookingEntity
import com.pickfoodplace.wear.repository.BookingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private lateinit var repo: BookingRepository
    private lateinit var vm: BookingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = mockk()
        vm = BookingViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun refresh_updatesBookingsOnSuccess() = runTest(dispatcher) {
        val list = listOf(BookingEntity("1", "Place", "10:00-11:00", "QR"))
        coEvery { repo.getBookings() } returns Result.success(list)

        vm.refresh()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, vm.bookings.value.size)
        assertEquals("1", vm.bookings.value.first().id)
    }

    @Test
    fun cancel_triggersRepositoryAndRefresh() = runTest(dispatcher) {
        coEvery { repo.cancelBooking("1") } returns Result.success(Unit)
        coEvery { repo.getBookings() } returns Result.success(emptyList())

        vm.cancel("1") {}
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { repo.cancelBooking("1") }
        assertEquals(0, vm.bookings.value.size)
    }
}