package com.mahilashakti.unnati.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["memberId"])]
)
data class AttendanceEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val memberId: String,
    val date: Long = System.currentTimeMillis(),
    val isPresent: Boolean,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
