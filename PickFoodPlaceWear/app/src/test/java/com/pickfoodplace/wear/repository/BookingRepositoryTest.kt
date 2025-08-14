package com.pickfoodplace.wear.repository

import androidx.work.WorkManager
import com.pickfoodplace.wear.data.local.BookingDao
import com.pickfoodplace.wear.data.local.BookingEntity
import com.pickfoodplace.wear.data.remote.ApiService
import com.pickfoodplace.wear.data.remote.dto.BookingCreateRequest
import com.pickfoodplace.wear.data.remote.dto.BookingResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookingRepositoryTest {
    private val dispatcher = StandardTestDispatcher()

    private lateinit var api: ApiService
    private lateinit var dao: BookingDao
    private lateinit var work: WorkManager
    private lateinit var repo: BookingRepository

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        api = mockk()
        dao = mockk(relaxed = true)
        work = mockk(relaxed = true)
        repo = BookingRepository(api, dao, work)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun createBooking_succeeds_and_inserts() = runTest(dispatcher) {
        coEvery { api.createBooking(any()) } returns BookingResponse("1", "Place", "10:00-11:00", "QR")
        val res = repo.createBooking("p1", "10:00-11:00")
        assertTrue(res.isSuccess)
        coVerify { dao.insert(BookingEntity("1", "Place", "10:00-11:00", "QR")) }
    }
}