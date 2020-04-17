package com.softwareoverflow.hiit_trainer.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface BaseDao<T> {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrUpdate(obj: T) : Long

    @Delete
    fun delete(obj: T)

}