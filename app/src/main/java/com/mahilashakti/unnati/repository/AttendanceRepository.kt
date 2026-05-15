package com.mahilashakti.unnati.repository

import com.mahilashakti.unnati.data.model.AttendanceEntity
import com.mahilashakti.unnati.database.AttendanceDao
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(private val attendanceDao: AttendanceDao) {

    suspend fun insertAttendance(records: List<AttendanceEntity>) = attendanceDao.insertAttendance(records)

    fun getAttendanceForMember(memberId: String): Flow<List<AttendanceEntity>> = attendanceDao.getAttendanceForMember(memberId)

    fun getAllAttendance(): Flow<List<AttendanceEntity>> = attendanceDao.getAllAttendance()

    suspend fun getTotalMeetingsForMember(memberId: String): Int = attendanceDao.getTotalMeetingsForMember(memberId)

    suspend fun getAttendedMeetingsForMember(memberId: String): Int = attendanceDao.getAttendedMeetingsForMember(memberId)
}
