package com.mahilashakti.unnati.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mahilashakti.unnati.data.model.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(records: List<AttendanceEntity>)

    @Query("SELECT * FROM attendance WHERE memberId = :memberId ORDER BY date DESC")
    fun getAttendanceForMember(memberId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE isSynced = 0")
    fun getUnsyncedAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT COUNT(*) FROM attendance WHERE memberId = :memberId")
    suspend fun getTotalMeetingsForMember(memberId: String): Int

    @Query("SELECT COUNT(*) FROM attendance WHERE memberId = :memberId AND isPresent = 1")
    suspend fun getAttendedMeetingsForMember(memberId: String): Int

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)
}
