package com.mahilashakti.unnati.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val role: String, // e.g., Admin, Member
    val phoneNumber: String
)
