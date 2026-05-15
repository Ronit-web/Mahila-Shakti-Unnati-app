package com.mahilashakti.unnati.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "loan_repayments",
    foreignKeys = [
        ForeignKey(
            entity = LoanEntity::class,
            parentColumns = ["id"],
            childColumns = ["loanId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["loanId"]), Index(value = ["memberId"])]
)
data class LoanRepaymentEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val loanId: String,
    val memberId: String,
    val amountPaid: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    val emiNumber: Int,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
