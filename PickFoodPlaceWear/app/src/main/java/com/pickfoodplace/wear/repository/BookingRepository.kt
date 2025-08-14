package com.pickfoodplace.wear.repository

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pickfoodplace.wear.data.local.BookingDao
import com.pickfoodplace.wear.data.local.BookingEntity
import com.pickfoodplace.wear.data.remote.ApiService
import com.pickfoodplace.wear.data.remote.dto.BookingCreateRequest
import com.pickfoodplace.wear.work.ReminderWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class BookingRepository(
    private val api: ApiService,
    private val bookingDao: BookingDao,
    private val workManager: WorkManager
) {
    suspend fun createBooking(placeId: String, slot: String): Result<BookingEntity> = withContext(Dispatchers.IO) {
        return@withContext try {
            val res = api.createBooking(BookingCreateRequest(placeId, slot))
            val entity = BookingEntity(res.id, res.placeName, res.slot, res.qrData)
            bookingDao.insert(entity)

            val workData = Data.Builder()
                .putString(ReminderWorker.KEY_BOOKING_ID, res.id)
                .putString(ReminderWorker.KEY_PLACE_NAME, res.placeName)
                .putString(ReminderWorker.KEY_SLOT, res.slot)
                .build()
            val delayMinutes = 5L
            val req = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .setInputData(workData)
                .build()
            workManager.enqueue(req)

            Result.success(entity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBookings(): Result<List<BookingEntity>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val res = api.getBookings()
            val list = res.map { BookingEntity(it.id, it.placeName, it.slot, it.qrData) }
            list.forEach { bookingDao.insert(it) }
            Result.success(list)
        } catch (e: Exception) {
            val cached = bookingDao.getAll()
            if (cached.isNotEmpty()) Result.success(cached) else Result.failure(e)
        }
    }

    suspend fun cancelBooking(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            api.cancelBooking(id)
            bookingDao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}