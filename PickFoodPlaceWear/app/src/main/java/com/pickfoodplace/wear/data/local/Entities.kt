package com.pickfoodplace.wear.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Entity

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val hourlyRate: Int
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val placeName: String,
    val slot: String,
    val qrData: String
)

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places")
    suspend fun getAll(): List<PlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<PlaceEntity>)

    @Query("DELETE FROM places")
    suspend fun clear()
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings")
    suspend fun getAll(): List<BookingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: BookingEntity)

    @Delete
    suspend fun delete(booking: BookingEntity)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Database(entities = [PlaceEntity::class, BookingEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
    abstract fun bookingDao(): BookingDao

    companion object {
        fun build(context: Context): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pickfoodplace-wear-db"
        ).fallbackToDestructiveMigration().build()
    }
}