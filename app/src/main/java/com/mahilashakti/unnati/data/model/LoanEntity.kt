package com.mahilashakti.unnati.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class LoanStatus {
    PENDING,
    APPROVED,
    REJECTED,
    PAID_OFF
}

@Entity(
    tableName = "loans",
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
data class LoanEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val memberId: String,
    val principalAmount: Double,
    val interestRate: Double,
    val durationMonths: Int,
    val emiAmount: Double,
    val status: String = LoanStatus.PENDING.name, // Stored as String for Room simplicity
    val requestDate: Long = System.currentTimeMillis(),
    val approvalDate: Long? = null,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
