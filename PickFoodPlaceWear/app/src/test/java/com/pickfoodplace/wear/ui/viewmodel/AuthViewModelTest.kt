package com.pickfoodplace.wear.ui.viewmodel

import com.pickfoodplace.wear.repository.AuthRepository
import io.mockk.coEvery
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
class AuthViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: AuthRepository
    private lateinit var vm: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = mockk()
        vm = AuthViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_success_updatesLoading() = runTest(dispatcher) {
        coEvery { repo.login("a@b.com", "pass") } returns Result.success(Unit)
        var called = false
        vm.login("a@b.com", "pass") { called = true }
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(false, vm.loading.value)
        assertEquals(true, called)
    }

    @Test
    fun register_failure_setsError() = runTest(dispatcher) {
        coEvery { repo.register(any(), any()) } returns Result.failure(RuntimeException("fail"))
        vm.register("a", "b") {}
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals("fail", vm.error.value)
    }
}