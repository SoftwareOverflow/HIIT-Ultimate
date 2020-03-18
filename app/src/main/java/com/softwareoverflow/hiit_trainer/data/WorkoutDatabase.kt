package com.softwareoverflow.hiit_trainer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.softwareoverflow.hiit_trainer.data.dao.ExerciseTypeDao
import com.softwareoverflow.hiit_trainer.data.dao.WorkoutDao
import com.softwareoverflow.hiit_trainer.data.dao.WorkoutSetDao
import com.softwareoverflow.hiit_trainer.data.entity.ExerciseTypeEntity
import com.softwareoverflow.hiit_trainer.data.entity.WorkoutEntity
import com.softwareoverflow.hiit_trainer.data.entity.WorkoutSetEntity

@Database(
    entities = [WorkoutEntity::class, WorkoutSetEntity::class, ExerciseTypeEntity::class],
    version = 1,
    exportSchema = true
)
abstract class WorkoutDatabase : RoomDatabase() {


    /**
     * The DAO to be able to access the data in the database
     */
    abstract val workoutDao: WorkoutDao

    /**
     * DAO to access data related to exercise types"
     */
    abstract val exerciseTypeDao: ExerciseTypeDao

    abstract val workoutSetDao: WorkoutSetDao

    companion object {

        private val DATABASE_NAME = "WorkoutDatabase"

        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getInstance(context: Context): WorkoutDatabase {

            // Multiple threads could ask for instances at the same time
            synchronized(this) {
                var instance = INSTANCE // Copy so kotlin can use smart cast
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WorkoutDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration() // TODO - work out how migrations work when required to prevent destruction and recreation of DB
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}