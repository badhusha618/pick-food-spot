package com.pickfoodplace.wear.ui.viewmodel

import com.pickfoodplace.wear.data.local.PlaceEntity
import com.pickfoodplace.wear.repository.PlaceRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlacesViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private lateinit var repo: PlaceRepository
    private lateinit var vm: PlacesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = mockk()
        vm = PlacesViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load_emitsPlaces() = runTest(dispatcher) {
        val places = listOf(PlaceEntity("1", "Test", "City", 10))
        every { repo.getPlaces(null, null, null, null, null) } returns flowOf(places)

        vm.load(null, null, null, null, null)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, vm.places.value.size)
        assertEquals("1", vm.places.value.first().id)
    }
}