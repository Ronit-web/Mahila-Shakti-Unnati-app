package com.mahilashakti.unnati.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mahilashakti.unnati.data.UserEntity
import com.mahilashakti.unnati.data.model.MemberEntity
import com.mahilashakti.unnati.data.model.SavingsEntity
import com.mahilashakti.unnati.data.model.LoanEntity
import com.mahilashakti.unnati.data.model.LoanRepaymentEntity
import com.mahilashakti.unnati.data.model.AttendanceEntity
import com.mahilashakti.unnati.utils.Constants

@Database(
    entities = [
        UserEntity::class, 
        MemberEntity::class, 
        SavingsEntity::class,
        LoanEntity::class,
        LoanRepaymentEntity::class,
        AttendanceEntity::class
    ], 
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    // abstract fun userDao(): UserDao
    abstract fun memberDao(): MemberDao
    abstract fun savingsDao(): SavingsDao
    abstract fun loanDao(): LoanDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
