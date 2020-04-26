package com.softwareoverflow.hiit_trainer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.softwareoverflow.hiit_trainer.data.WorkoutDatabase
import com.softwareoverflow.hiit_trainer.data.dao.ExerciseTypeDao
import com.softwareoverflow.hiit_trainer.data.dao.WorkoutDao
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class WorkoutDatabaseTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var workoutDao: WorkoutDao
    private lateinit var exerciseTypeDao: ExerciseTypeDao
    private lateinit var db: WorkoutDatabase

    @Before
    fun createDB() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WorkoutDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        workoutDao = db.workoutDao
        exerciseTypeDao = db.exerciseTypeDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetWorkout() {
        /*val workout = WorkoutEntity(null, "MyWorkout")

        val exerciseType = ExerciseTypeEntity(0, "Test Type", "icon_name", "#666666")
        val exerciseTypeId = exerciseTypeDao.createOrUpdate(exerciseType)

        val exerciseTypes = getValue(exerciseTypeDao.getAllExerciseTypes())
        assertEquals(exerciseTypes.size, 1)

        val workoutId = workoutDao.createOrUpdate(workout)

        val workoutSet = WorkoutSetEntity(null, workoutId, exerciseTypeId, 5, 5, 6, 120)
        workoutSetDao.createOrUpdate(workoutSet)

        val workouts = getValue(workoutDao.getAllWorkouts())
        assertEquals(workouts.size, 1)

        val workoutSets = getValue(workoutSetDao.getAllWorkoutSets())
        assertEquals(workoutSets.size, 1)*/
    }
}