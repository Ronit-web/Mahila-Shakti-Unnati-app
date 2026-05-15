package com.mahilashakti.unnati.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "savings",
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
data class SavingsEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val memberId: String,
    val date: Long = System.currentTimeMillis(),
    val amountPaid: Double,
    val isPaid: Boolean,
    val fineAmount: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
